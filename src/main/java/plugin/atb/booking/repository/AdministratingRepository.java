package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface AdministratingRepository extends JpaRepository<Administrating, Long> {

    Page<Administrating> findAllByEmployeeId(Long id, Pageable pageable);

    Page<Administrating> findAllByOfficeId(Long officeId, Pageable pageable);

    boolean existsByOfficeAndEmployee(Office office, Employee employee);

}
