package plugin.atb.booking.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface WorkPlaceTypeRepository extends JpaRepository<WorkPlaceTypeEntity, Long> {

    WorkPlaceTypeEntity findByName(String name);

    boolean existsByName(String name);

}
