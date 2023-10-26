package com.rebalance.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.rebalance.entity.Image;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    @Value("${cloud.storage.profile.image.name.prefix}")
    private String imageNamePrefix;

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
        throwExceptionIfImageNotExists(id);
        byte[] imageBytes = loadImageBytes(id, false);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public String getImageIconByGlobalId(Long id) {
        throwExceptionIfIconNotExists(id);
        byte[] imageBytes = loadImageBytes(id, true);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public void deleteImageByGlobalId(Long id) {
        throwExceptionIfImageNotExists(id);

        BlobServiceClient connection = connectToCloudStorage();

        connection.getBlobContainerClient(storageContainerImages).getBlobClient(id + imageNamePrefix + ".png").delete();
        connection.getBlobContainerClient(storageContainerThumbnails).getBlobClient(id + imageNamePrefix + "_icon.jpg").delete();

        imageRepository.deleteById(id);
    }

    public void throwExceptionIfImageNotExists(Long id) {
        imageRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_301));
    }

    public void throwExceptionIfIconNotExists(Long id) {
        imageRepository.findById(id).orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_302));
    }

    public void updateImage(Long id, String base64Image) {
        BlobServiceClient connection = connectToCloudStorage();

        connection.getBlobContainerClient(storageContainerImages).getBlobClient(id + imageNamePrefix + ".png").delete();
        connection.getBlobContainerClient(storageContainerThumbnails).getBlobClient(id + imageNamePrefix + "_icon.jpg").delete();

        saveImageAndIconToCloud(base64Image, id);
    }

    private byte[] loadImageBytes(Long globalId, boolean getIcon) {
        BlobServiceClient connection = connectToCloudStorage();
        String container = getIcon ? storageContainerThumbnails : storageContainerImages;
        String blobName = getIcon ? globalId + imageNamePrefix + "_icon.jpg" : globalId + imageNamePrefix + ".png";
        return connection.getBlobContainerClient(container).getBlobClient(blobName).downloadContent().toBytes();
    }

    private BlobServiceClient connectToCloudStorage() {
        return new BlobServiceClientBuilder()
                .endpoint(String.format("https://%s.blob.core.windows.net/", storageAccountName))
                .connectionString(storageConnectionString)
                .buildClient();
    }

    private String saveImageAndIconToCloud(String base64Image, Long globalId) {
        try {
            BlobServiceClient connection = connectToCloudStorage();

            byte[] decodedImage = Base64.getDecoder().decode(base64Image.getBytes(StandardCharsets.UTF_8));

            String blobName = globalId + imageNamePrefix + ".png";
            connection.getBlobContainerClient(storageContainerImages).getBlobClient(blobName).upload(BinaryData.fromBytes(Base64.getDecoder().decode(base64Image.getBytes(StandardCharsets.UTF_8))));

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedImage));
            java.awt.Image scaledImage = img.getScaledInstance(100, 100, java.awt.Image.SCALE_REPLICATE);

            BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

            String iconBlobName = globalId + imageNamePrefix + "_icon.jpg";

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", b);

            connection.getBlobContainerClient(storageContainerThumbnails).getBlobClient(iconBlobName).upload(BinaryData.fromBytes(b.toByteArray()));
            return String.format("https://%s.blob.core.windows.net/%s/%s", storageAccountName, storageContainerImages, blobName);
        } catch (IOException e) {
            throw new RuntimeException("Error saving image", e);
        }
    }

}
