
package plugin.atb.booking.repository;

import java.time.*;
import java.util.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;

@Repository
public interface WorkPlaceRepository extends JpaRepository<WorkPlaceEntity, Long> {

    Page<WorkPlaceEntity> findAllByFloorAndType(
        FloorEntity floor,
        WorkPlaceTypeEntity workPlaceType,
        Pageable pageable
    );

    /**
     * Возвращает список сущностей (свободных мест),
     * для которого выполняется следующие условия:
     * <ul>
     * <li> Рабочее место присутствует в указанном списке (параметр floorPlaces)
     * <li> У данного места нет брони удовлетворяющей следующим двум условиям:
     * <ul>
     *     <li> Начало интервала раньше конца брони из БД (т.е. start < dateTimeOfEnd)
     *     <li> Конец интервала позже начала брони из БД (т.е. end > dateTimeOfStart)
     * </ul>
     * <li>  Данные группируются по id места.
     * </ul>
     *
     * @param floorPlaces список мест конкретного этажа и типа
     * @param startOfPeriod дата/время начала интервала, start
     * @param endOfPeriod дата/время конца интервала, end
     *
     * @return список свободных мест
     */
    @Query(value = "select place " +
                   "from WorkPlaceEntity place " +
                   "left join BookingEntity booking on place.id = booking.workPlace.id " +
                   "where place in :floorPlaces " +
                   "and not (booking.dateTimeOfStart < :end and booking.dateTimeOfEnd > :start) " +
                   "group by place.id")
    List<WorkPlaceEntity> findAllFreeInPeriod(
        @Param("floorPlaces") List<WorkPlaceEntity> floorPlaces,
        @Param("start") LocalDateTime startOfPeriod,
        @Param("end") LocalDateTime endOfPeriod
    );

}
