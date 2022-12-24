package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface FavPlaceRepository extends JpaRepository<FavPlace, Long> {

    boolean existsByEmployeeAndPlace(Employee employee, WorkPlace place);

    Page<FavPlace> findAllByEmployee(Employee employee, Pageable pageable);

    void deleteByEmployeeAndPlace(Employee employee, WorkPlace place);

}
