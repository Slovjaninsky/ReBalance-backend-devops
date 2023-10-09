package com.rebalance.servises;

import com.rebalance.entities.Image;
import com.rebalance.exceptions.ImageNotFoundException;
import com.rebalance.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
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

            BufferedImage img = ImageIO.read(imagePath.toFile());
            java.awt.Image scaledImage = img.getScaledInstance(100, 100, java.awt.Image.SCALE_REPLICATE);

            BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

            Path iconPath = Paths.get(IMAGES_PATH, (globalId + "_icon.jpg"));
            ImageIO.write(bufferedImage, "jpg", iconPath.toFile());
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
        Image image = imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Icon not found with id: " + id));
        byte[] imageBytes = loadImageBytes(image.getImagePath().replace(".jpg", "_icon.jpg"));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }

    public void deleteImageByGlobalId(Long id){
        throwExceptionIfNotExistsById(id);
        Path imagePath = Paths.get(IMAGES_PATH, (id + ".jpg"));
        Path iconPath = Paths.get(IMAGES_PATH, (id + "_icon.jpg"));
        try {
            Files.delete(imagePath);
            Files.delete(iconPath);
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
