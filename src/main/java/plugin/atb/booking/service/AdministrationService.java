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
                "Администратор для данного офиса закреплен: %s, %s",
                admin.getOffice(), admin.getEmployee()));
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
        AdministratingEntity updateAdmin = getById(admin.getId());

        if (updateAdmin == null) {
            throw new NotFoundException(String.format(
                "Администрация не найдена: %s", admin.getEmployee().getFullName()));
        }

        if (admin.getEmployee() != null) {
            updateAdmin.setEmployee(admin.getEmployee());
        }

        if (admin.getOffice() != null) {
            updateAdmin.setOffice(admin.getOffice());
        }

        administrationRepository.save(admin);
    }

    public void delete(Long id) {

        if (getById(id) == null) {
            throw new NotFoundException(String.format(
                "Команда не найдена: %s", id));
        }

        administrationRepository.deleteById(id);
    }

}
