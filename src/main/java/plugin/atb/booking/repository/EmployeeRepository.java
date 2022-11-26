package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Page<EmployeeEntity> findByFullNameContainingOrderByFullName(
        String fullName,
        Pageable pageable
    );

    EmployeeEntity findByLogin(String login);

    boolean existsEmployeeByLoginOrEmail(String login, String email);

}
