package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface WorkPlaceTypeRepository extends JpaRepository<WorkPlaceTypeEntity, Long> {

    Page<WorkPlaceTypeEntity> findByName(String name, Pageable pageable);

    boolean existsByName(String name);

}
