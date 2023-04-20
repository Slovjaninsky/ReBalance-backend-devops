package com.example.databaseservice.servises;

import com.example.databaseservice.entities.Image;
import com.example.databaseservice.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

@Service
public class ImageService {

    private static final String IMAGES_PATH = "D:\\rebalance_images";

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void saveImage(String base64Image, Long globalId) {
        try {
            byte[] decodedImage = Base64.getDecoder().decode(base64Image.getBytes("UTF-8"));
            Path imagePath = Paths.get(IMAGES_PATH, (globalId + ".jpg"));
            Files.write(imagePath, decodedImage);
            Image toRepository = new Image(globalId, imagePath.toString());
            imageRepository.save(toRepository);
        } catch (IOException e) {
            throw new RuntimeException("Error saving image", e);
        }
    }

    public byte[] loadImageBytes(String imagePath) {
        try {
            return Files.readAllBytes(Paths.get(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Error loading image", e);
        }
    }

    public String getImageByGlobalId(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Image not found with id: " + id));
        byte[] imageBytes = loadImageBytes(image.getImagePath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }

    public void deleteImageByGlobalId(Long id){
        throwExceptionIfNotExistsById(id);
        Path imagePath = Paths.get(IMAGES_PATH, (id + ".jpg"));
        try {
            Files.delete(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image", e);
        }
        imageRepository.deleteById(id);
    }

    public void throwExceptionIfNotExistsById(Long id){
        getImageByGlobalId(id);
    }

    public void updateImage(Long id, String base64Image) {
        try {
            Path imagePath = Paths.get(IMAGES_PATH, (id + ".jpg"));
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image.getBytes("UTF-8"));
            Files.write(imagePath, decodedBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error updating image", e);
        }
    }

}
