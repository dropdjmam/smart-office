package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    Page<Floor> findAllByOffice(Office office, Pageable pageable);

    boolean existsByFloorNumberAndOffice(Integer floorNumber, Office office);

}
