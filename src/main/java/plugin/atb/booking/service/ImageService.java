package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public void add(ImageEntity image) {

        boolean exists = imageRepository
            .existsByContent(image.getContent());

        if (exists) {
            throw new AlreadyExistsException("Изображение уже добавлено");
        }

        imageRepository.save(image);
    }

    public ImageEntity getById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    public Page<ImageEntity> getAll(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }

    public void update(ImageEntity image) {
        ImageEntity updateImage = getById(image.getId());

        if (updateImage == null) {
            throw new NotFoundException("Изображение не найдено");
        }

        if (image.getContent() != null) {
            updateImage.setContent(image.getContent());
        }

        imageRepository.save(updateImage);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format("Не найдено изображение с id: %s", id));
        }

        imageRepository.deleteById(id);
    }

}