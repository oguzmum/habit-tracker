package com.ozzo.habit_tracker.service;

import com.ozzo.habit_tracker.entity.UploadedImage;
import com.ozzo.habit_tracker.repository.UploadedImageRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadedImageService {

    private static final Logger log = LoggerFactory.getLogger(UploadedImageService.class);

    private final UploadedImageRepository uploadedImageRepository;
    private final Path uploadDirectory;

    public UploadedImageService(
            UploadedImageRepository uploadedImageRepository,
            @Value("${image.upload-dir:uploads}") String uploadDir) {
        this.uploadedImageRepository = uploadedImageRepository;
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDirectory);
            log.info("Ensured upload directory exists at {}", this.uploadDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    public UploadedImage store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please choose a file to upload.");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload"
        );
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("The file name is invalid.");
        }
        String fileExtension = "";
        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex != -1) {
            fileExtension = originalFilename.substring(extensionIndex);
        }

        String storedFilename = UUID.randomUUID() + fileExtension;
        Path targetLocation = uploadDirectory.resolve(storedFilename);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        UploadedImage uploadedImage = new UploadedImage();
        uploadedImage.setOriginalFilename(originalFilename);
        uploadedImage.setStoredFilename(storedFilename);
        uploadedImage.setStoragePath(targetLocation.toString());
        uploadedImage.setUploadedAt(LocalDateTime.now());

        UploadedImage saved = uploadedImageRepository.save(uploadedImage);
        log.info("Stored uploaded image {} at {}", saved.getId(), targetLocation);
        return saved;
    }

    public List<UploadedImage> listUploads() {
        return uploadedImageRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadedAt"));
    }
}
