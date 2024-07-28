package com.rebalance.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.rebalance.entity.Image;
import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.ImageRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.notNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    public static final String PNG = ".png";
    public static final String ICON_JPG = "_icon.jpg";
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
    private BlobServiceClient connection;

    @PostConstruct
    @SuppressWarnings("unused")
    public void init() {
        connection = connectToCloudStorage();
    }

    public void saveImage(Long globalId, byte[] image) {
        String uri = saveImageAndIconToCloud(image, globalId);
        Image toRepository = new Image(globalId, uri);
        imageRepository.save(toRepository);
    }

    public void deleteImageByGlobalId(Long id) {
        if (!imageExistsByGlobalId(id)) {
            return;
        }
        connection.getBlobContainerClient(storageContainerImages).getBlobClient(id + imageNamePrefix + PNG).delete();
        connection.getBlobContainerClient(storageContainerThumbnails).getBlobClient(id + imageNamePrefix + ICON_JPG).delete();

        imageRepository.deleteById(id);
    }

    public void throwExceptionIfMediaNotExists(Long globalId) {
        if (imageRepository.findById(globalId).isEmpty()) {
            throw new RebalanceException(RebalanceErrorType.RB_301);
        }
    }

    public boolean imageExistsByGlobalId(Long globalId) {
        return imageRepository.findById(globalId).isPresent();
    }

    public void updateImage(Long globalId, byte[] image) {
        deleteImageByGlobalId(globalId);
        saveImage(globalId, image);
    }

    public String loadImageBase64(Long globalId, boolean getIcon) {
        if (imageRepository.findById(globalId).isEmpty()) {
            return null;
        }
        try {
            String container = getIcon ? storageContainerThumbnails : storageContainerImages;
            String blobName = getIcon ? globalId + imageNamePrefix + ICON_JPG : globalId + imageNamePrefix + PNG;
            byte[] imageBytes = connection.getBlobContainerClient(container).getBlobClient(blobName).downloadContent().toBytes();
            return nonNull(imageBytes) ? Base64.getEncoder().encodeToString(imageBytes) : null;
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }


    private BlobServiceClient connectToCloudStorage() {
        return new BlobServiceClientBuilder()
                .endpoint(String.format("https://%s.blob.core.windows.net/", storageAccountName))
                .connectionString(storageConnectionString)
                .buildClient();
    }

    private String saveImageAndIconToCloud(byte[] imageBytes, Long globalId) {
        try {
            String blobName = globalId + imageNamePrefix + PNG;
            connection.getBlobContainerClient(storageContainerImages).getBlobClient(blobName).upload(BinaryData.fromBytes(imageBytes));

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
            notNull(img, "Invalid image data");
            java.awt.Image scaledImage = img.getScaledInstance(100, 100, java.awt.Image.SCALE_REPLICATE);
            BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(scaledImage, 0, 0, null);

            String iconBlobName = globalId + imageNamePrefix + ICON_JPG;

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", b);

            connection.getBlobContainerClient(storageContainerThumbnails).getBlobClient(iconBlobName).upload(BinaryData.fromBytes(b.toByteArray()));
            return String.format("https://%s.blob.core.windows.net/%s/%s", storageAccountName, storageContainerImages, blobName);
        } catch (IOException e) {
            log.error("Error saving image [globalId={}]", globalId);
            throw new RebalanceException(RebalanceErrorType.RB_302);
        } catch (IllegalArgumentException e) {
            log.error("Invalid image data [globalId={}]", globalId);
            throw new RebalanceException(RebalanceErrorType.RB_303);
        }
    }

}
