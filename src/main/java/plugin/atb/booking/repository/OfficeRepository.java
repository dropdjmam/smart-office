package plugin.atb.booking.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {

    Page<Office> findAllByAddressContainingIgnoreCase(String address, Pageable pageable);

    boolean existsByAddress(String address);

}
