package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface FavPlaceRepository extends JpaRepository<FavPlaceEntity, Long> {

    boolean existsByEmployeeAndPlace(EmployeeEntity employee, WorkPlaceEntity place);

    Page<FavPlaceEntity> findAllByEmployee(EmployeeEntity employee, Pageable pageable);

    void deleteByEmployeeAndPlace(EmployeeEntity employee, WorkPlaceEntity place);

}
