package com.rebalance.it;

import com.rebalance.RebalanceApplication;
import com.rebalance.service.ImageService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = RebalanceApplication.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:/app_test.cfg")
class ITBlobStorage {

    @Autowired
    @SuppressWarnings("unused")
    private ImageService imageService;

    @Test
    @SneakyThrows
    void should_save_read_change_and_delete_image_and_thumbnail() {
        Long id = 1L;

        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String imageBytesEncoded = Base64.getEncoder().encodeToString(imageBytes);

        imageService.saveImage(id, imageBytes);

        String imageRetrieved = imageService.loadImageBase64(id, false);
        assertThat(imageRetrieved).isEqualTo(imageBytesEncoded);

        BufferedImage imageNew = new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
        ByteArrayOutputStream outputStreamNew = new ByteArrayOutputStream();
        ImageIO.write(imageNew, "png", outputStreamNew);
        byte[] imageBytesNew = outputStream.toByteArray();
        String imageBytesNewEncoded = Base64.getEncoder().encodeToString(imageBytesNew);

        imageService.updateImage(id, imageBytesNew);

        imageRetrieved = imageService.loadImageBase64(id, false);
        assertThat(imageRetrieved).isEqualTo(imageBytesNewEncoded);

        imageService.deleteImageByGlobalId(id);

        assertNull(imageService.loadImageBase64(id, false));
    }

}
