package com.rebalance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebalance.dto.response.ImageResponse;
import com.rebalance.mapper.ExpenseWrapper;
import com.rebalance.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Tag(name = "Image management")
@RestController
@RequestMapping(APIVersion.current)
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;

    @PostMapping(
            value = "/images",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ImageResponse>> getImagesByIds(@RequestBody String imageIdsJson) throws IOException {
        return getMedia(imageIdsJson, false);
    }

    @PostMapping(
            value = "/icons",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ImageResponse>> getIconsByIds(@RequestBody String imageIdsJson) throws JsonProcessingException {
        return getMedia(imageIdsJson, true);
    }

    private ResponseEntity<List<ImageResponse>> getMedia(String json, boolean icons) throws JsonProcessingException {
        List<ImageResponse> mediaResponses = getMediaByIds(json, icons);
        if (mediaResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mediaResponses);
    }

    private List<ImageResponse> getMediaByIds(String data, boolean icons) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ExpenseWrapper expenseWrapper = mapper.readValue(data, ExpenseWrapper.class);
        Long[] imageIds = expenseWrapper.getExpenseIds();

        List<ImageResponse> iconResponses = new ArrayList<>();
        for (Long imageId : imageIds) {
            String res = imageService.loadImageBase64(imageId, icons);
            if (nonNull(res)) {
                iconResponses.add(new ImageResponse(imageId, res));
            }
        }
        return iconResponses;
    }

    @PostMapping(
            value = "images/{globalId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> saveImage(@PathVariable Long globalId, @RequestParam("file") MultipartFile file) {
        try {
            byte[] imageData = file.getBytes();
            imageService.saveImage(globalId, imageData);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(
            value = "/images/{globalId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> updateImage(@PathVariable Long globalId, @RequestParam("file") MultipartFile file) {
        try {
            byte[] imageData = file.getBytes();
            imageService.updateImage(globalId, imageData);
            return ResponseEntity.created(URI.create("/images/" + globalId)).build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(
            value = "/images/{id}"
    )
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteImageByGlobalId(id);
        return ResponseEntity.noContent().build();
    }
}
