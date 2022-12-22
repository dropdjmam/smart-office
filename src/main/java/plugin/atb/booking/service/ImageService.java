package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public void add(ImageEntity image) {

        imageRepository.save(image);
    }

    public ImageEntity getById(Long id) {

        ValidationUtils.checkId(id);

        return imageRepository.findById(id).orElse(null);
    }

    public Page<ImageEntity> getAll(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }

    public void update(ImageEntity image) {

        if (getById(image.getId()) == null) {
            throw new NotFoundException("Изображение не найдено.");
        }

        imageRepository.save(image);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format("Не найдено изображение с id: %s", id));
        }

        imageRepository.deleteById(id);
    }

}
