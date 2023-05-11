package ru.skypro.sockwarehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import ru.skypro.sockwarehouse.model.Socks;

public interface SocksRepository extends JpaRepository<Socks, Long> {
    Socks findByCottonPartAndColor(Integer cottonPart, String color);

    @Query(value = "SELECT SUM(quantity) FROM socks WHERE color = :color AND cotton_part = :cottonPart", nativeQuery = true)
    Integer getTheSameNumberOfSocks(@Param("color") String color, @Param("cottonPart") int cottonPart);

    @Query(value = "SELECT SUM(quantity) FROM socks WHERE color = :color AND cotton_part > :cottonPart", nativeQuery = true)
    Integer getMoreSocks(@Param("color") String color, @Param("cottonPart") int cottonPart);

    @Query(value = "SELECT SUM(quantity) FROM socks WHERE color = :color AND cotton_part < :cottonPart", nativeQuery = true)
    Integer getFewerSocks(@Param("color") String color, @Param("cottonPart") int cottonPart);


}
