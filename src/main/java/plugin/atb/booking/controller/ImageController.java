package plugin.atb.booking.controller;

import java.util.*;

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
@RequiredArgsConstructor
@RequestMapping("/image")
@Tag(name = "Изображение", description = "Фото профиля пользователя и карта этажа")
public class ImageController {

    private final ImageService imageService;

    private final ImageMapper imageMapper;

    private final FloorService floorService;

    private final EmployeeService employeeService;

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Сотрудник: Загрузка изображения - размер одного файла: max 10Мб")
    @PostMapping(value = "/employee/{employeeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadEmployeeImage(
        @PathVariable Long employeeId, @RequestParam MultipartFile file
    ) throws Exception {

        ValidationUtils.checkId(employeeId);
        validate(file);

        var image = imageMapper.dtoToImage(file);
        if (image == null) {
            throw new NotFoundException("Изображение не найдено");
        }
        var employee = employeeService.getById(employeeId);
        if (employee == null) {
            throw new NotFoundException("Не найден сотрудник с id: " + employeeId);
        }

        var newImage = imageService.add(image);
        var oldImage = employee.getPhoto();

        employee.setPhoto(newImage);
        employeeService.update(employee);

        if (oldImage != null) {
            imageService.delete(image.getId());
        }

        return "Фотография пользователя успешно загружена";
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Этаж: Загрузка изображения - размер одного файла: max 10Мб")
    @PostMapping(value = "/floor/{floorId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFloorImage(
        @PathVariable Long floorId, @RequestParam MultipartFile file
    ) throws Exception {

        ValidationUtils.checkId(floorId);
        validate(file);

        var image = imageMapper.dtoToImage(file);
        if (image == null) {
            throw new NotFoundException("Изображение не найдено");
        }
        var floor = floorService.getById(floorId);
        if (floor == null) {
            throw new NotFoundException("Не найден этаж с id: " + floorId);
        }

        var newImage = imageService.add(image);
        var oldImage = floor.getMapFloor();

        floor.setMapFloor(newImage);
        floorService.update(floor);

        if (oldImage != null) {
            imageService.delete(oldImage.getId());
        }

        return "Карта этажа успешно загружена";
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получить изображение")
    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
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

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Изменить изображение")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String update(@PathVariable Long id, @RequestParam MultipartFile file) throws Exception {

        ValidationUtils.checkId(id);
        validate(file);

        var image = imageMapper.dtoToImage(file).setId(id);

        imageService.update(image);

        return "Изображение изменено";
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить фото сотрудника")
    @DeleteMapping(value = "/employee/{employeeId}")
    public String deleteFromEmployee(@PathVariable Long employeeId) {
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

        return "Изображение удалено";
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить карту этажа")
    @DeleteMapping(value = "/floor/{floorId}")
    public String deleteFromFloor(@PathVariable Long floorId) {
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

        return "Изображение удалено";
    }

    private void validate(MultipartFile file) {
        var isJpeg = Objects.equals(file.getContentType(), MediaType.IMAGE_JPEG_VALUE);
        var isPng = Objects.equals(file.getContentType(), MediaType.IMAGE_PNG_VALUE);

        if (!(isJpeg || isPng)) {
            throw new IncorrectArgumentException(
                "Переданный файл не является JPEG или PNG изображением");
        }
    }

}
