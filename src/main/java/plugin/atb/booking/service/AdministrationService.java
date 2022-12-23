package plugin.atb.booking.service;

import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import plugin.atb.booking.entity.*;
import plugin.atb.booking.exception.*;
import plugin.atb.booking.repository.*;

@Service
@RequiredArgsConstructor
public class AdministrationService {

    private final AdministrationRepository administrationRepository;

    public void add(AdministratingEntity admin) {

        boolean exists = administrationRepository.existsByOfficeAndEmployee(
            admin.getOffice(), admin.getEmployee());
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Администратор с id:%s для офиса с id:%s уже закреплен.",
                admin.getEmployee().getId(), admin.getOffice().getId()));
        }

        if (admin.getOffice() == null) {
            throw new NotFoundException("Офис не найден.");
        }

        if (admin.getEmployee() == null) {
            throw new NotFoundException("Сотрудник не найден.");
        }

        administrationRepository.save(admin);
    }

    public Page<AdministratingEntity> getAll(Pageable pageable) {

        return administrationRepository.findAll(pageable);
    }

    public Page<AdministratingEntity> getAllOfficeById(Long id, Pageable pageable) {

        return administrationRepository.findAllOfficeById(id, pageable);
    }

    public Page<AdministratingEntity> getAllAdministrationByOfficeId(
        Long officeId,
        Pageable pageable
    ) {

        return administrationRepository.findAllAdministrationByOfficeId(officeId, pageable);
    }

    public AdministratingEntity getById(Long id) {
        return administrationRepository.findById(id).orElse(null);
    }

    public void update(AdministratingEntity admin) {

        if (getById(admin.getId()) == null) {
            throw new NotFoundException("Не найден администратор с id: " + admin.getId());
        }

        var newEmployee = admin.getEmployee();
        var newOffice = admin.getOffice();

        boolean exists = administrationRepository.existsByOfficeAndEmployee(newOffice, newEmployee);
        if (exists) {
            throw new AlreadyExistsException(String.format(
                "Администратор уже является администратором офиса с id: %s",
                admin.getOffice().getId()));
        }

        administrationRepository.save(admin);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format(
                "Администратор с id:%s не найден.", id));
        }

        administrationRepository.deleteById(id);
    }

}
