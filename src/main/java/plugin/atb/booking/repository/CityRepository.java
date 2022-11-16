package plugin.atb.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plugin.atb.booking.entity.CityEntity;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

}
