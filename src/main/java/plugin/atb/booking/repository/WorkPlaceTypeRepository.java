package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface WorkPlaceTypeRepository extends JpaRepository<WorkPlaceType, Long> {

    Page<WorkPlaceType> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);

}
