package ru.skypro.sockwarehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.sockwarehouse.model.Registration;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}
