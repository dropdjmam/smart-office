package plugin.atb.booking.service;

import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

import static org.springframework.data.domain.Sort.Direction.*;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public void add(String name) {

        if (name.isBlank()) {
            throw new IncorrectArgumentException(
                "Имя города не может быть пустым или состоять только из пробелов");
        }

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

        return cityRepository.findAllByNameContainingOrderByName(name);
    }

    public void update(City city) {
        String newName = city.getName();

        boolean exists = cityRepository.existsByName(newName);
        if (exists) {
            throw new AlreadyExistsException("Город  " + newName + " уже существует!");
        }

        if (getById(city.getId()) == null) {
            throw new NotFoundException("Город  с id " + city.getId() + " не найден!");
        }

        cityRepository.save(city);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Город с id " + id + " не найден");
        }

        cityRepository.deleteById(id);
    }

}
