package com.csvParser.fineuploader.io;

import com.csvParser.fineuploader.model.UploadRequest;

public interface StorageService {

    void save(UploadRequest uploadRequest);

    void delete(String uuid);

    void mergeChunks(String uuid, String fileName, int totalParts, long totalFileSize);
}
