package plugin.atb.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plugin.atb.booking.entity.*;

@Repository
public interface FloorRepository extends JpaRepository<FloorEntity, Long> {

}
