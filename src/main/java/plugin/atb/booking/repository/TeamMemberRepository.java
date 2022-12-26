package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    TeamMember findByEmployeeAndTeam(Employee employee, Team team);

    Page<TeamMember> findAllByTeam(Team team, Pageable pageable);

    Page<TeamMember> findAllByEmployee(Employee employee, Pageable pageable);

    boolean existsByEmployeeAndTeam(Employee employee, Team team);

    void deleteAllByTeam(Team team);

}
