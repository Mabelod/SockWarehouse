package ru.skypro.sockwarehouse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.skypro.sockwarehouse.component.DtoMapper;
import ru.skypro.sockwarehouse.dto.SocksRecord;
import ru.skypro.sockwarehouse.exception.*;
import ru.skypro.sockwarehouse.model.Condition;
import ru.skypro.sockwarehouse.model.Registration;
import ru.skypro.sockwarehouse.model.Socks;
import ru.skypro.sockwarehouse.repository.RegistrationRepository;
import ru.skypro.sockwarehouse.repository.SocksRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class SocksService {
    private final RegistrationRepository registrationRepository;
    private final SocksRepository socksRepository;
    private final DtoMapper dtoMapper;
    private final Logger logger = LoggerFactory.getLogger(SocksService.class);

    public SocksService(RegistrationRepository registrationRepository, SocksRepository socksRepository, DtoMapper dtoMapper) {
        this.registrationRepository = registrationRepository;
        this.socksRepository = socksRepository;
        this.dtoMapper = dtoMapper;
    }

    /**
     * Метод выводит общее колличество носок на складе
     * @param color цвет носок
     * @param operation операция сравнения носок
     * @param cottonPart значение процента хлопка в составе носок
     * @return возращает обьект типа String
     */
    public String getQuantity(String color, String operation, Integer cottonPart) {
        logger.info("Was invoked method getQuantity");
        if (cottonPart < 0 || cottonPart > 100) throw new InvalidParameterException();
        switch (operation) {
            case "moreThan": {
                Integer counter = socksRepository.getMoreSocks(color, cottonPart);
                if (counter == null) throw new SocksNotFoundException();
                else {
                    return checkingTheNumberOfSocks(counter);
                }
            }

            case "lessThan": {
                Integer counter = socksRepository.getFewerSocks(color, cottonPart);
                if (counter == null) throw new SocksNotFoundException();
                else {
                    return checkingTheNumberOfSocks(counter);
                }
            }

            case "equal": {
                Integer counter = socksRepository.getTheSameNumberOfSocks(color, cottonPart);
                if (counter == null) throw new SocksNotFoundException();
                else {
                    return checkingTheNumberOfSocks(counter);
                }
            }

            default:
                throw new OperationException();
        }
    }

    /**
     * Метод для приема носок на склад.
     * @param socksRecord обьект для хранания параметров приема носок.
     * @return возращает обьект типа String
     */
    public String addIncome(SocksRecord socksRecord) {
        logger.info("Was invoked method addIncome");
        if (!socksRecord.getColor().isEmpty() && socksRecord.getCottonPart() > 0 && socksRecord.getCottonPart() <= 100
                && socksRecord.getQuantity() > 0) {
            Socks socks = socksRepository.findByCottonPartAndColor(socksRecord.getCottonPart(), socksRecord.getColor().toLowerCase());
            Registration registration = new Registration(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                    socksRecord.getQuantity(),
                    Condition.INCOME);
            if (socks != null) {
                socks.setQuantity(socksRecord.getQuantity() + socks.getQuantity());
                registration.setSocks(socksRepository.save(socks));
                registrationRepository.save(registration);
            } else {
                registration.setSocks(socksRepository.save(dtoMapper.toSocksEntity(socksRecord)));
                registrationRepository.save(registration);
            }
            return "Прием носок осуществлен";
        }
        logger.error("Неверный запрос");
        throw new OperationException();
    }

    /**
     * Метод для отправки носок со склада
     * @param socksRecord обьект для хранания параметров отправки носок.
     * @return возращает обьект типа String
     */
    public String addOutcome(SocksRecord socksRecord) {
        logger.info("Was invoked method addOutcome");
        if (!socksRecord.getColor().isEmpty() && socksRecord.getCottonPart() > 0 && socksRecord.getCottonPart() <= 100 && socksRecord.getQuantity() > 0) {
            Socks socks = socksRepository.findByCottonPartAndColor(socksRecord.getCottonPart(), socksRecord.getColor().toLowerCase());
            Registration registration = new Registration(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                    socksRecord.getQuantity(),
                    Condition.OUTCOME);
            if (socks != null) {
                int quantity;
                quantity = socks.getQuantity() - socksRecord.getQuantity();
                if (0 > quantity) {
                    logger.error("На складе нет такого колличества носок");
                    throw new LessThanZeroException();
                }
                socks.setQuantity(quantity);
                registration.setSocks(socksRepository.save(socks));
                registrationRepository.save(registration);
            } else {
                logger.error("Нет носок с такими параметрами");
                throw new SocksNotFoundException();
            }
            return "Отправка носок осуществлена";
        }
        logger.error("Неверный запрос");
        throw new OperationException();
    }

    /**
     * Метод проверки носок на складе.
     * @param counter колличество носок
     * @return возвращает обьект типа String
     */
    private String checkingTheNumberOfSocks(Integer counter) {
        if (counter != null) return "Колличество носок - " + counter;
        else {
            logger.error("На складе нет носок с такими параметрами");
            throw new NotFoundException();
        }
    }
}
