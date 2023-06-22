package com.example.databaseservice.servises;

import com.example.databaseservice.entities.Image;
import com.example.databaseservice.exceptions.ImageNotFoundException;
import com.example.databaseservice.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

@Service
public class ImageService {

    @Value("${IMAGES_PATH}")
    private String IMAGES_PATH;

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

            ImageIcon imageIcon = new ImageIcon(decodedImage);
            java.awt.Image originalImage = imageIcon.getImage();

            java.awt.Image scaledImage = originalImage.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);

            BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

            Path iconPath = Paths.get(IMAGES_PATH, (globalId + "_icon.jpg"));
            ImageIO.write(bufferedImage, "jpg", iconPath.toFile());

            Image iconToRepository = new Image(globalId, iconPath.toString());
            imageRepository.save(iconToRepository);
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
        Image image = imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Image not found with id: " + id));
        byte[] imageBytes = loadImageBytes(image.getImagePath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }

    public String getImageIconByGlobalId(Long id) {
        Image image = imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Image not found with id: " + id));
        byte[] imageBytes = loadImageBytes(image.getImagePath().replace(".jpg", "_icon.jpg"));
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
