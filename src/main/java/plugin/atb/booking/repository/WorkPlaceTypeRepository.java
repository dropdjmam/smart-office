package plugin.atb.booking.repository;

import java.util.*;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface WorkPlaceTypeRepository extends JpaRepository<WorkPlaceTypeEntity, Long> {

    List<WorkPlaceTypeEntity> findAllByNameContainingOrderByName(String name);

    boolean existsByName(String name);

}
