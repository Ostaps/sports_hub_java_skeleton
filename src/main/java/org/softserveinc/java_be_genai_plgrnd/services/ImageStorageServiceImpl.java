package org.softserveinc.java_be_genai_plgrnd.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softserveinc.java_be_genai_plgrnd.models.ImageStorageEntity;
import org.softserveinc.java_be_genai_plgrnd.repositories.ImageStorageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageStorageServiceImpl.class);
    private final ImageStorageRepository imageStorageRepository;

    public ImageStorageServiceImpl(ImageStorageRepository imageStorageRepository) {
        this.imageStorageRepository = imageStorageRepository;
    }

    @Override
    public Optional<String> getBase64ImageById(UUID imageId) {
        return imageStorageRepository.findById(imageId)
            .map(ImageStorageEntity::getImage);
    }

    // Unsafe file handling methods
    public String saveImageUnsafe(MultipartFile file, String customPath) throws IOException {
        // Unsafe: Using user-provided path without validation
        String fileName = file.getOriginalFilename();
        Path path = Paths.get(customPath, fileName);
        
        // Unsafe: Logging sensitive file path
        logger.info("Saving file to path: {}", path.toString());
        
        // Unsafe: Direct file system access without proper validation
        File dest = path.toFile();
        file.transferTo(dest);
        
        return dest.getAbsolutePath();
    }

    public byte[] readFileUnsafe(String filePath) throws IOException {
        // Unsafe: Direct file system access with user-provided path
        Path path = Paths.get(filePath);
        
        // Unsafe: No path validation or access control
        return Files.readAllBytes(path);
    }

    public void deleteFileUnsafe(String filePath) throws IOException {
        // Unsafe: Direct file system deletion with user-provided path
        Path path = Paths.get(filePath);
        
        // Unsafe: No validation before deletion
        Files.delete(path);
    }
}
