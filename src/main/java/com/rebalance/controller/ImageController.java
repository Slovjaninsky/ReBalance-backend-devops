package com.rebalance.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebalance.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@Tag(name = "Image management")
@RestController
@RequestMapping(APIVersion.current)
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping(
            value = "/images",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[][] getImagesByIds(@RequestBody String imageIdsJson) throws IOException {
        return getMediaByIds(imageIdsJson, true);
    }

    @GetMapping(
            value = "/icons",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[][] getIconsByIds(@RequestBody String imageIdsJson) throws IOException {
        return getMediaByIds(imageIdsJson, false);
    }

    private byte[][] getMediaByIds(String data, boolean images) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Long[] imageIds = mapper.readValue(data, Long[].class);
        return images ? imageService.getImagesByGlobalIds(imageIds) : imageService.getImageIconsByGlobalIds(imageIds);
    }

    @PostMapping(
            value = "/images/{globalId}",
            consumes = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<Void> saveImage(@PathVariable Long globalId, @RequestBody byte[] imageData) {
        imageService.saveImage(globalId, imageData);
        return ResponseEntity.ok().build();
    }

    @PutMapping(
            value = "/images/{globalId}",
            consumes = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<Void> updateImage(@PathVariable Long globalId, @RequestBody byte[] imageData) {
        imageService.updateImage(globalId, imageData);
        return ResponseEntity.created(URI.create("/images/" + globalId)).build();
    }

    @DeleteMapping(
            value = "/images/{globalId}"
    )
    public ResponseEntity<Void> deleteImage(@PathVariable Long globalId) {
        imageService.deleteImageByGlobalId(globalId);
        return ResponseEntity.noContent().build();
    }
}
