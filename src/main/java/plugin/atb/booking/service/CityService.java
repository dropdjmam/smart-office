package plugin.atb.booking.service;

import java.time.*;
import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

import static org.springframework.data.domain.Sort.Direction.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;

    @Transactional
    public void add(String name, String zoneId) {

        validate(name, zoneId);

        boolean exists = cityRepository.existsByName(name);
        if (exists) {
            throw new AlreadyExistsException(
                "Город " + name + "уже существует!");
        }

        City city = new City();
        city.setName(name);
        cityRepository.save(city);
    }

    public List<City> getAll() {
        var sort = Sort.by(ASC, "name");
        return cityRepository.findAll(sort);
    }

    public City getById(Long id) {
        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return cityRepository.findById(id).orElse(null);
    }

    public List<City> getAllByName(String name) {

        if (name.isBlank()) {
            throw new IncorrectArgumentException(
                "Имя города не может быть пустым или состоять только из пробелов");
        }

        return cityRepository.findAllByNameContainingIgnoreCaseOrderByName(name);
    }

    @Transactional
    public void update(City city) {

        if (getById(city.getId()) == null) {
            throw new NotFoundException("Город  с id " + city.getId() + " не найден!");
        }

        validate(city.getName(), city.getZoneId());

        cityRepository.save(city);
    }

    @Transactional
    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Город с id " + id + " не найден");
        }

        cityRepository.deleteById(id);
    }

    private void validate(String name, String zoneId) {
        if (name.isBlank()) {
            throw new IncorrectArgumentException(
                "Имя города не может быть пустым или состоять только из пробелов");
        }

        if (zoneId.isBlank()) {
            throw new IncorrectArgumentException(
                "Строка с тайм зоной не может быть пустой или состоять только из пробелов");
        }

        try {
            ZoneId.of(zoneId);
        } catch (DateTimeException e) {
            throw new IncorrectArgumentException(
                "Неверный формат или значение тайм зоны, пример: [Asia/Vladivostok]");
        }

    }

}
