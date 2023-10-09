package com.example.databaseservice.servises;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.example.databaseservice.entities.Image;
import com.example.databaseservice.exceptions.ImageNotFoundException;
import com.example.databaseservice.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class ImageService {

    @Value("${cloud.storage.account.name}")
    private String storageAccountName;

    @Value("${cloud.storage.account.connection.string}")
    private String storageConnectionString;

    @Value("${cloud.storage.container.image}")
    private String storageContainerImages;

    @Value("${cloud.storage.container.thumbnail}")
    private String storageContainerThumbnails;

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void saveImage(String base64Image, Long globalId) {
        String uri = saveImageAndIconToCloud(base64Image, globalId);
        Image toRepository = new Image(globalId, uri);
        imageRepository.save(toRepository);
    }

    public String getImageByGlobalId(Long id) {
        throwExceptionIfNotExistsById(id);
        byte[] imageBytes = loadImageBytes(id, false);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }

    public String getImageIconByGlobalId(Long id) {
        throwExceptionIfNotExistsById(id);
        byte[] imageBytes = loadImageBytes(id, true);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }

    public void deleteImageByGlobalId(Long id) {
        throwExceptionIfNotExistsById(id);

        BlobServiceClient connection = connectToCloudStorage();

        connection.getBlobContainerClient(storageContainerImages).getBlobClient(id + ".png").delete();
        connection.getBlobContainerClient(storageContainerThumbnails).getBlobClient(id + "_icon.jpg").delete();

        imageRepository.deleteById(id);
    }

    public void throwExceptionIfNotExistsById(Long id) {
        imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Image not found with id: " + id));
    }

    public void updateImage(Long id, String base64Image) {
        BlobServiceClient connection = connectToCloudStorage();

        connection.getBlobContainerClient(storageContainerImages).getBlobClient(id + ".png").delete();
        connection.getBlobContainerClient(storageContainerThumbnails).getBlobClient(id + "_icon.jpg").delete();

        saveImageAndIconToCloud(base64Image, id);
    }

    private byte[] loadImageBytes(Long globalId, boolean getIcon) {
        BlobServiceClient connection = connectToCloudStorage();
        String container = getIcon ? storageContainerThumbnails : storageContainerImages;
        String blobName = getIcon ? globalId + "_icon.jpg" : globalId + ".png";
        return connection.getBlobContainerClient(container).getBlobClient(blobName).downloadContent().toBytes();
    }

    private BlobServiceClient connectToCloudStorage() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(String.format("https://%s.blob.core.windows.net/", storageAccountName))
                .connectionString(storageConnectionString)
                .buildClient();
        return blobServiceClient;
    }

    private String saveImageAndIconToCloud(String base64Image, Long globalId) {
        try {
            BlobServiceClient connection = connectToCloudStorage();

            byte[] decodedImage = Base64.getDecoder().decode(base64Image.getBytes("UTF-8"));

            String blobName = globalId + ".png";
            connection.getBlobContainerClient(storageContainerImages).getBlobClient(blobName).upload(BinaryData.fromBytes(Base64.getDecoder().decode(base64Image.getBytes("UTF-8"))));

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedImage));
            java.awt.Image scaledImage = img.getScaledInstance(100, 100, java.awt.Image.SCALE_REPLICATE);

            BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

            String iconBlobName = globalId + "_icon.jpg";

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", b);

            connection.getBlobContainerClient(storageContainerThumbnails).getBlobClient(iconBlobName).upload(BinaryData.fromBytes(b.toByteArray()));
            return String.format("https://%s.blob.core.windows.net/%s/%s", storageAccountName, storageContainerImages, blobName);
        } catch (IOException e) {
            throw new RuntimeException("Error saving image", e);
        }
    }

}
