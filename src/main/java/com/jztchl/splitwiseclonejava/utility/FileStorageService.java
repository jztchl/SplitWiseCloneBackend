package com.jztchl.splitwiseclonejava.utility;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads");

    public String storeFile(MultipartFile file, String folder) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }


            Path dir = rootLocation.resolve(folder);
            Files.createDirectories(dir);


            String original = file.getOriginalFilename();
            assert original != null;
            String safeName = original.replaceAll("[^a-zA-Z0-9.\\-]", "_");
            String filename = System.currentTimeMillis() + "_" + safeName;

            Path destinationFile = dir.resolve(Paths.get(filename)).normalize().toAbsolutePath();


            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);


            return "/uploads/" + folder + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
