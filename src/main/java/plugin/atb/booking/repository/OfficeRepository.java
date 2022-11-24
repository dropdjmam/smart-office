package plugin.atb.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plugin.atb.booking.entity.*;

@Repository
public interface OfficeRepository extends JpaRepository<OfficeEntity, Long> {

    OfficeEntity findByAddress(String address);

    OfficeEntity findByWorkNumber(String workNumber);

    boolean existsByAddress(String address);

}
