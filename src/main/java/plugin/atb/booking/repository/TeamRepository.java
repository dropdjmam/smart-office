package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    Page<TeamEntity> findAllByName(String name, Pageable pageable);

    Page<TeamEntity> findAllById(Long id, Pageable pageable);

    TeamEntity findByLeaderId(Long id);

    boolean existsByNameAndLeader(String name, EmployeeEntity leader);

}
