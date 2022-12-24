package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    TeamMember findTeamMemberByEmployeeAndTeam(Employee employee, Team team);

    TeamMember findByTeamId(Long teamId);

    TeamMember findByTeamName(String name);

    Page<TeamMember> findAllTeamMemberByTeamId(Long teamId, Pageable pageable);

    Page<TeamMember> findAllTeamMemberByTeamName(String name, Pageable pageable);

    Page<TeamMember> findAllTeamByEmployee(Employee employee, Pageable pageable);

    boolean existsByEmployeeAndTeam(Employee employee, Team team);

    void deleteAllByTeam(Team team);

}
