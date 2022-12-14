package plugin.atb.booking.repository;

import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface FloorRepository extends JpaRepository<FloorEntity, Long> {

    Page<FloorEntity> findAllByOfficeId(Long officeId, Pageable pageable);

    boolean existsByFloorNumberAndOffice(Integer floorNumber, OfficeEntity office);

}
