package plugin.atb.booking.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.*;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.mapper.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.service.*;

@RestController
@Tag(name = "Изображение")
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    private final ImageRepository imageRepository;

    private final ImageMapper imageMapper;

    @Operation(summary = "Загрузка изображения - размер одного файла: max 10Мб")
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile multipartImage)
        throws Exception {

        var image = imageMapper.dtoToImage(multipartImage);

        imageService.add(image);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Получение изображения")
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@PathVariable Long imageId) {
        byte[] image = imageRepository.findById(imageId)
            .orElseThrow(() -> new NotFoundException("Изображение не найдено"))
            .getContent();
        return new ByteArrayResource(image);
    }

    @Operation(summary = "Изменение изображения")
    @PutMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(@RequestParam MultipartFile multipartImage)

        throws Exception {

        var image = imageMapper.dtoToImage(multipartImage);

        imageService.update(image);

        return ResponseEntity.ok("Изображение изменено.");
    }

    @Operation(summary = "Удаление изображения")
    @DeleteMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> delete(@PathVariable Long id) {

        imageService.delete(id);

        return ResponseEntity.ok("Изображение успешно удалено.");
    }

}
