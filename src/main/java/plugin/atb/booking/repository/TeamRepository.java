package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Page<Team> findAllByName(String name, Pageable pageable);

    Page<Team> findAllByLeaderId(Long leaderId, Pageable pageable);

    boolean existsByNameAndLeader(String name, Employee leader);

}
