package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByFullNameContaining(String fullName, Pageable pageable);

    Employee findByLogin(String login);

    boolean existsEmployeeByLoginOrEmail(String login, String email);

}
