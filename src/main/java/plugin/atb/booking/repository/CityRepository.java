package plugin.atb.booking.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plugin.atb.booking.model.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByNameContainingIgnoreCaseOrderByName(String name);

    boolean existsByName(String name);

}
