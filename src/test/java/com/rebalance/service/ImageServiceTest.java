package com.rebalance.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.rebalance.entity.Image;
import com.rebalance.exception.RebalanceException;
import com.rebalance.repository.ImageRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    private static final String STORAGE_ACC = "acc";
    private static final String CONNECTION_STR = "conn-str";
    private static final String IMAGES_CONTAINER = "images";
    private static final String ICONS_CONTAINER = "icons";
    private static final String IMAGE_NAME_PREFIX = "prefix";
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private BlobServiceClient connection;
    @Mock
    private BlobContainerClient blobContainerClient;
    @Mock
    private BlobClient blobClient;
    @Mock
    private BinaryData binaryData;
    @InjectMocks
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "storageAccountName", STORAGE_ACC);
        ReflectionTestUtils.setField(imageService, "storageConnectionString", CONNECTION_STR);
        ReflectionTestUtils.setField(imageService, "storageContainerImages", IMAGES_CONTAINER);
        ReflectionTestUtils.setField(imageService, "storageContainerThumbnails", ICONS_CONTAINER);
        ReflectionTestUtils.setField(imageService, "imageNamePrefix", IMAGE_NAME_PREFIX);
        ReflectionTestUtils.setField(imageService, "connection", connection);
    }

    @Test
    void should_throw_exception_when_image_does_not_exist() {
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RebalanceException.class, () -> imageService.getImagesByGlobalIds(new Long[]{1L}));
    }

    @Test
    void should_provide_image_when_requested() {
        byte[] expected = new byte[]{1, 2, 3};
        Long id = 1L;

        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(connection.getBlobContainerClient(IMAGES_CONTAINER)).thenReturn(blobContainerClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.PNG)).thenReturn(blobClient);
        when(blobClient.downloadContent()).thenReturn(binaryData);
        when(binaryData.toBytes()).thenReturn(expected);

        byte[][] images = imageService.getImagesByGlobalIds(new Long[]{1L});

        assertEquals(1, images.length);
        assertEquals(expected, images[0]);
    }

    @Test
    void should_throw_exception_when_checking_if_exists() {
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RebalanceException.class, () -> imageService.throwExceptionIfMediaNotExists(1L));
    }

    @Test
    void should_not_throw_exception_when_checking_if_exists() {
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        assertDoesNotThrow(() -> imageService.throwExceptionIfMediaNotExists(1L));
    }

    @Test
    @SneakyThrows
    void should_save_image_when_requested() {
        Long id = 1L;

        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        when(connection.getBlobContainerClient(IMAGES_CONTAINER)).thenReturn(blobContainerClient);
        when(connection.getBlobContainerClient(ICONS_CONTAINER)).thenReturn(blobContainerClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.PNG)).thenReturn(blobClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.ICON_JPG)).thenReturn(blobClient);

        imageService.saveImage(id, imageBytes);

        verify(blobClient, times(2)).upload(any());
        verify(imageRepository).save(new Image(id, String.format("https://%s.blob.core.windows.net/images/%s%s%s", STORAGE_ACC, id, IMAGE_NAME_PREFIX, ImageService.PNG)));
    }

    @Test
    @SneakyThrows
    void should_wrap_npe_when_thrown() {
        Long id = 1L;

        when(connection.getBlobContainerClient(IMAGES_CONTAINER)).thenReturn(blobContainerClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.PNG)).thenReturn(blobClient);

        assertThrows(RebalanceException.class, () -> imageService.saveImage(id, new byte[]{1, 2, 3, 4}));
    }


    @Test
    @SneakyThrows
    void should_update_image_when_requested_and_present() {
        Long id = 1L;
        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));

        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        when(connection.getBlobContainerClient(IMAGES_CONTAINER)).thenReturn(blobContainerClient);
        when(connection.getBlobContainerClient(ICONS_CONTAINER)).thenReturn(blobContainerClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.PNG)).thenReturn(blobClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.ICON_JPG)).thenReturn(blobClient);

        imageService.updateImage(id, imageBytes);

        verify(blobClient, times(2)).delete();
        verify(blobClient, times(2)).upload(any());
        verify(imageRepository, never()).save(any());
    }

    @Test
    @SneakyThrows
    void should_update_image_when_requested_and_absent() {
        Long id = 1L;
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        when(connection.getBlobContainerClient(IMAGES_CONTAINER)).thenReturn(blobContainerClient);
        when(connection.getBlobContainerClient(ICONS_CONTAINER)).thenReturn(blobContainerClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.PNG)).thenReturn(blobClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.ICON_JPG)).thenReturn(blobClient);

        imageService.updateImage(id, imageBytes);

        verify(blobClient, times(2)).delete();
        verify(blobClient, times(2)).upload(any());
        verify(imageRepository).save(new Image(id, String.format("https://%s.blob.core.windows.net/images/%s%s%s", STORAGE_ACC, id, IMAGE_NAME_PREFIX, ImageService.PNG)));
    }

    @Test
    void should_throw_exception_when_image_absent_for_delete() {
        when(imageRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RebalanceException.class, () -> imageService.deleteImageByGlobalId(1L));
    }

    @Test
    void should_delete_when_present() {
        Long id = 1L;

        when(imageRepository.findById(any())).thenReturn(Optional.of(new Image()));
        when(connection.getBlobContainerClient(IMAGES_CONTAINER)).thenReturn(blobContainerClient);
        when(connection.getBlobContainerClient(ICONS_CONTAINER)).thenReturn(blobContainerClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.PNG)).thenReturn(blobClient);
        when(blobContainerClient.getBlobClient(id + IMAGE_NAME_PREFIX + ImageService.ICON_JPG)).thenReturn(blobClient);

        imageService.deleteImageByGlobalId(id);
        verify(blobClient, times(2)).delete();
    }

}