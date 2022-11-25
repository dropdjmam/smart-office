package plugin.atb.booking.service;

import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

import static org.springframework.data.domain.Sort.Direction.*;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public void add(String name) {
        boolean exists = cityRepository.existsByName(name);

        if (exists) {
            throw new AlreadyExistsException(
                "Город " + name + "уже существует!");
        }

        CityEntity city = new CityEntity();
        city.setName(name);
        cityRepository.save(city);
    }

    public List<CityEntity> getAll() {
        var sort = Sort.by(ASC, "name");
        return cityRepository.findAll(sort);
    }

    public CityEntity getById(Long id) {
        return cityRepository.findById(id).orElse(null);
    }

    public List<CityEntity> getAllByName(String name) {
        return cityRepository.findAllByNameContainingOrderByName(name);
    }

    public void update(CityEntity city) {
        String newName = city.getName();

        boolean exists = cityRepository.existsByName(newName);

        if (exists) {
            throw new AlreadyExistsException("Город  " + newName + " уже существует!");
        }

        CityEntity cityUpdate = getById(city.getId());

        if (cityUpdate == null) {
            throw new NotFoundException("Город  с id " + cityUpdate.getId() + " не найден!");
        }

        cityUpdate.setName(newName);

        cityRepository.save(cityUpdate);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException("Города с id " + id + " не существует");
        }

        cityRepository.deleteById(id);
    }

}
