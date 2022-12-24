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
import plugin.atb.booking.service.*;
import plugin.atb.booking.utils.*;

@RestController
@Tag(name = "Изображение")
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    private final ImageMapper imageMapper;

    private final FloorService floorService;

    private final EmployeeService employeeService;

    @Operation(summary = "Загрузка изображения - размер одного файла: max 10Мб")
    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile multipartImage)
        throws Exception {

        var image = imageMapper.dtoToImage(multipartImage);
        if (image == null) {
            throw new NotFoundException("Изображение не найдено");
        }
        imageService.add(image);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Сотрудник: Загрузка изображения - размер одного файла: max 10Мб")
    @PostMapping(value = "/employee", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadEmployeeImage(
        @RequestParam Long employeeId,
        @RequestParam MultipartFile multipartImage
    )
        throws Exception {

        var image = imageMapper.dtoToImage(multipartImage);
        if (image == null) {
            throw new NotFoundException("Изображение не найдено");
        }
        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + employeeId);
        }

        employee.setPhoto(image);
        imageService.add(image);
        employeeService.update(employee);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Этаж: Загрузка изображения - размер одного файла: max 10Мб")
    @PostMapping(value = "/floor", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFloorImage(
        @RequestParam Long floorId,
        @RequestParam MultipartFile multipartImage
    )
        throws Exception {

        var image = imageMapper.dtoToImage(multipartImage);
        if (image == null) {
            throw new NotFoundException("Изображение не найдено");
        }
        var floor = floorService.getById(floorId);
        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + floorId);
        }

        floor.setMapFloor(image);
        imageService.add(image);
        floorService.update(floor);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @Operation(summary = "Получение изображения")
    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    Resource downloadImage(@PathVariable Long id) {
        ValidationUtils.checkId(id);
        var image = imageService.getById(id);
        if (image == null) {
            throw new NotFoundException("Не найдено изображение по id: " + id);
        }

        var content = image.getContent();
        if (content == null) {
            throw new NotFoundException("Не найдено изображение");
        }

        return new ByteArrayResource(content);
    }

    @Operation(summary = "Изменение изображения")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(
        @PathVariable Long id,
        @RequestParam MultipartFile multipartImage
    )
        throws Exception {

        ValidationUtils.checkId(id);

        var image = imageMapper.dtoToImage(multipartImage).setId(id);

        imageService.update(image);

        return ResponseEntity.ok("Изображение изменено.");
    }

    @Operation(summary = "Удаление изображения из сотрудника")
    @DeleteMapping(value = "/employee/{employeeId}")
    public ResponseEntity<String> deleteFromEmployee(@PathVariable Long employeeId) {
        ValidationUtils.checkId(employeeId);

        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException(String.format("Сотрудник с id:%s не найден", employeeId));
        }
        var image = employee.getPhoto();

        if (image == null) {
            throw new NotFoundException("Изображение не найдено");
        }

        employee.setPhoto(null);
        imageService.delete(image.getId());
        employeeService.update(employee);
        return ResponseEntity.ok("Изображение удалено");
    }

    @Operation(summary = "Удаление изображения из этажа")
    @DeleteMapping(value = "/floor/{floorId}")
    public ResponseEntity<String> deleteFromFloor(@PathVariable Long floorId) {
        ValidationUtils.checkId(floorId);
        var floor = floorService.getById(floorId);
        if (floor == null) {
            throw new NotFoundException(String.format("Этаж с id:%s не найден", floorId));
        }
        var image = floor.getMapFloor();
        if (image == null) {
            throw new NotFoundException("Изображение не найдено");
        }

        floor.setMapFloor(null);
        imageService.delete(image.getId());
        floorService.update(floor);

        return ResponseEntity.ok("Изображение удалено");
    }

    @Operation(summary = "Удаление изображения по id")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        ValidationUtils.checkId(id);

        var image = imageService.getById(id);

        if (image == null) {
            throw new NotFoundException(String.format("Изображение с id:%s не найдено", id));
        }

        imageService.delete(image.getId());
        return ResponseEntity.ok("Изображение удалено");
    }

}
