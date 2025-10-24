package com.jztchl.splitwiseclonejava.controllers;

import com.jztchl.splitwiseclonejava.models.Doc;
import com.jztchl.splitwiseclonejava.repos.DocRepository;
import com.jztchl.splitwiseclonejava.utility.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocController {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(DocController.class);

    private final DocRepository docRepository;
    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Doc> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file, "settlements/");
        Doc doc = new Doc();
        doc.setUrl(fileUrl);
        docRepository.save(doc);
        logger.info("File uploaded: {}", file.getOriginalFilename());
        return ResponseEntity.ok(doc);
    }
}
