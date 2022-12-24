package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface AdministrationRepository extends JpaRepository<AdministratingEntity, Long> {

    Page<AdministratingEntity> findAllByEmployeeId(Long id, Pageable pageable);

    Page<AdministratingEntity> findAllAdministrationByOfficeId(Long officeId, Pageable pageable);

    boolean existsByOfficeAndEmployee(OfficeEntity office, EmployeeEntity employee);

}
