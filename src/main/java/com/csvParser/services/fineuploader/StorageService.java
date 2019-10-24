package com.csvParser.services.fineuploader;

import com.csvParser.models.fineuploader.UploadRequest;
import com.csvParser.services.abstraction.LoggableService;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService extends LoggableService {

    private Path basePath = Paths.get("./uploads");

    public void save(UploadRequest ur) {
        if (ur.getFile().isEmpty()) {
            throw new StorageException(String.format("File with uuid = [%s] is empty", ur.getUuid()));
        }
        Path targetFile;
        if (ur.getPartIndex() > -1) {
            targetFile = basePath.resolve(ur.getUuid()).resolve(String.format("%s_%05d", ur.getUuid(), ur.getPartIndex()));
        } else {
            targetFile = basePath.resolve(ur.getUuid()).resolve(ur.getFileName());
        }
        try {
            Files.createDirectories(targetFile.getParent());
            Files.copy(ur.getFile().getInputStream(), targetFile);
        } catch (IOException e) {
            String errorMsg = String.format("Error occurred when saving file with uuid = [%s]", ur);
            logger.error(errorMsg, e);
            throw new StorageException(errorMsg, e);
        }
    }

    public void delete(String uuid) {
        File targetDir = basePath.resolve(uuid).toFile();
        FileSystemUtils.deleteRecursively(targetDir);
    }

    public void mergeChunks(String uuid, String fileName, int totalParts) {
        File targetFile = basePath.resolve(uuid).resolve(fileName).toFile();
        try (FileChannel dest = new FileOutputStream(targetFile, true).getChannel()) {
            for (int i = 0; i < totalParts; i++) {
                File sourceFile = basePath.resolve(uuid).resolve(String.format("%s_%05d", uuid, i)).toFile();
                try (FileChannel src = new FileInputStream(sourceFile).getChannel()) {
                    dest.position(dest.size());
                    src.transferTo(0, src.size(), dest);
                }
                sourceFile.delete();
            }
        } catch (IOException e) {
            String errorMsg = String.format("Error occurred when merging chunks for uuid = [%s]", uuid);
            logger.error(errorMsg, e);
            throw new StorageException(errorMsg, e);
        }
    }
}