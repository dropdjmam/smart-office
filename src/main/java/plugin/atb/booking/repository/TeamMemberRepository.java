package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, Long> {

    TeamMemberEntity findTeamMemberByEmployeeAndTeam(EmployeeEntity employee, TeamEntity team);

    TeamMemberEntity findByTeamId(Long teamId);

    TeamMemberEntity findByTeamName(String name);

    Page<TeamMemberEntity> findAllTeamMemberByTeamId(Long teamId, Pageable pageable);

    Page<TeamMemberEntity> findAllTeamMemberByTeamName(String name, Pageable pageable);

    Page<TeamMemberEntity> findAllTeamByEmployeeId(Long employeeId, Pageable pageable);

    boolean existsByEmployeeAndTeam(EmployeeEntity employee, TeamEntity team);

}
