
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

    Integer countAllByTypeAndFloor(WorkPlaceTypeEntity type, FloorEntity floor);

    Page<WorkPlaceEntity> findAllByFloorAndType(
        FloorEntity floor,
        WorkPlaceTypeEntity type,
        Pageable pageable
    );

    /**
     * Возвращает список сущностей (занятых мест),
     * для которого выполняется следующие условия:
     * <ul>
     * <li> Рабочее место присутствует в указанном списке (параметр floorPlaces)
     * <li> Бронь по искомому месту актуальна (время окончания брони позже данного момента)
     * <li> Статус об удалении брони - false (не удалена)
     * <li> У данного места есть бронь удовлетворяющая следующим двум условиям:
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
                   "and booking.dateTimeOfEnd > current_timestamp " +
                   "and booking.isDeleted is false " +
                   "and booking.dateTimeOfStart < :end and booking.dateTimeOfEnd > :start " +
                   "group by place.id")
    List<WorkPlaceEntity> findAllBookedInPeriod(
        @Param("floorPlaces") List<WorkPlaceEntity> floorPlaces,
        @Param("start") LocalDateTime startOfPeriod,
        @Param("end") LocalDateTime endOfPeriod
    );

    Page<WorkPlaceEntity> findAllByFloor(FloorEntity floor, Pageable pageable);

}
