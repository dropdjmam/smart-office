package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, Long> {

    Page<TeamMemberEntity> findAllByEmployee(EmployeeEntity employee, Pageable pageable);

    Page<TeamMemberEntity> findByTeam(TeamEntity team, Pageable pageable);

    boolean existsByEmployeeAndTeam(EmployeeEntity employee, TeamEntity team);

}
