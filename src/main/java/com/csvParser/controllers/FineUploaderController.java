package com.csvParser.controllers;

import com.csvParser.models.fineuploader.UploadRequest;
import com.csvParser.models.fineuploader.UploadResponse;
import com.csvParser.services.fineuploader.StorageException;
import com.csvParser.services.fineuploader.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FineUploaderController {

    @Autowired
    private StorageService storageService;

    @CrossOrigin
    @PostMapping("/uploads")
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("qqfile") MultipartFile file,
            @RequestParam("qquuid") String uuid,
            @RequestParam("qqfilename") String fileName,
            @RequestParam(value = "qqpartindex", required = false, defaultValue = "-1") int partIndex,
            @RequestParam(value = "qqtotalparts", required = false, defaultValue = "-1") int totalParts,
            @RequestParam(value = "qqtotalfilesize", required = false, defaultValue = "-1") long totalFileSize) {

        UploadRequest request = new UploadRequest(uuid, file);
        request.setFileName(fileName);
        request.setTotalFileSize(totalFileSize);
        request.setPartIndex(partIndex);
        request.setTotalParts(totalParts);

        storageService.save(request);

        return ResponseEntity.ok().body(new UploadResponse(true));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<UploadResponse> handleException(StorageException ex) {
        UploadResponse response = new UploadResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @CrossOrigin
    @DeleteMapping("/uploads/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable("uuid") String uuid) {
        storageService.delete(uuid);

        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @PostMapping("/chunksdone")
    public ResponseEntity<Void> chunksDone(
            @RequestParam("qquuid") String uuid,
            @RequestParam("qqfilename") String fileName,
            @RequestParam(value = "qqtotalparts") int totalParts){

        storageService.mergeChunks(uuid, fileName, totalParts);

        return ResponseEntity.noContent().build();
    }
}