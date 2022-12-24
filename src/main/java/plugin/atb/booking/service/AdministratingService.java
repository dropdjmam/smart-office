package plugin.atb.booking.service;

import java.util.*;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.model.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;
import plugin.atb.booking.utils.*;

@Service
@RequiredArgsConstructor
public class AdministratingService {

    private final AdministratingRepository administratingRepository;

    public void add(Administrating administrating) {
        if (administrating.getOffice() == null) {
            throw new IncorrectArgumentException("Офис не указан");
        }

        if (administrating.getEmployee() == null) {
            throw new IncorrectArgumentException("Сотрудник не указан");
        }

        if (!Objects.equals(administrating.getEmployee().getRole().getName(), "ROLE_ADMIN")) {
            throw new IncorrectArgumentException(
                "Невозможно предоставить доступ к функционалу офиса сотруднику без роли \"Администратор\"");
        }

        boolean exists = administratingRepository.existsByOfficeAndEmployee(
            administrating.getOffice(), administrating.getEmployee());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Доступ сотруднику с id:%s для офиса с id:%s уже предоставлен",
                administrating.getEmployee().getId(), administrating.getOffice().getId()));
        }

        administratingRepository.save(administrating);
    }

    public Page<Administrating> getAll(Pageable pageable) {
        return administratingRepository.findAll(pageable);
    }

    public Page<Administrating> getAllByEmployeeId(Long id, Pageable pageable) {
        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return administratingRepository.findAllByEmployeeId(id, pageable);
    }

    public Page<Administrating> getAllByOfficeId(Long officeId, Pageable pageable) {
        if (officeId == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(officeId);

        return administratingRepository.findAllByOfficeId(officeId, pageable);
    }

    public Administrating getById(Long id) {

        if (id == null) {
            throw new IncorrectArgumentException("Id не указан");
        }

        ValidationUtils.checkId(id);

        return administratingRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format(
                "Не найдена сущность администрирования с id: %s", id));
        }

        administratingRepository.deleteById(id);
    }

}
