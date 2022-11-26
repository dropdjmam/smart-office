package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface WorkPlaceRepository extends JpaRepository<WorkPlaceEntity, Long> {

    Page<WorkPlaceEntity> findAllByFloorOrderByFloor(Pageable request, FloorEntity floor);

}
