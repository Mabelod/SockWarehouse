package ru.skypro.sockwarehouse.controller;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.skypro.sockwarehouse.dto.SocksRecord;
import ru.skypro.sockwarehouse.model.Socks;
import ru.skypro.sockwarehouse.repository.RegistrationRepository;
import ru.skypro.sockwarehouse.repository.SocksRepository;


import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class SocksControllerTest {
    @Autowired
    private SocksRepository socksRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RegistrationRepository registrationRepository;

    private final Faker faker = new Faker();
    @LocalServerPort
    private int port;
    @AfterEach
    public void afterEach() {
        registrationRepository.deleteAll();
        socksRepository.deleteAll();
    }

    @Test
    public void addToVerification() {
        addSocks(creatureSocks());
    }

    @Test
    public void addOutcomeTest() {
        SocksRecord socksRecord = addSocks(creatureSocks());

        ResponseEntity<String> socksRecordResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/api/socks/outcome", socksRecord, String.class);
        assertThat(socksRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(socksRecordResponseEntity.getBody()).isEqualTo("Отправка носок осуществлена");
        Socks socks = socksRepository.findByCottonPartAndColor(socksRecord.getCottonPart() ,socksRecord.getColor());
        assertThat(socks).isNotNull();
        assertThat(socks.getQuantity()).isEqualTo(0);
    }
    @ParameterizedTest
    @MethodSource("quantityParameters")
    public void getQuantityTest(String color, String operation, int cotton, int count) {
        SocksRecord socksRecord1 = new SocksRecord();
        socksRecord1.setColor("white");
        socksRecord1.setCottonPart(80);
        socksRecord1.setQuantity(30);
        addSocks(socksRecord1);
        SocksRecord socksRecord2 = new SocksRecord();
        socksRecord2.setColor("red");
        socksRecord2.setCottonPart(30);
        socksRecord2.setQuantity(15);
        addSocks(socksRecord2);
        SocksRecord socksRecord3 = new SocksRecord();
        socksRecord3.setColor("black");
        socksRecord3.setCottonPart(60);
        socksRecord3.setQuantity(16);
        addSocks(socksRecord3);


        ResponseEntity<String> countSocksResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port
                + "/api/socks?color=" + color + "&operation=" + operation + "&cottonPart=" + cotton, String.class);
        assertThat(countSocksResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(countSocksResponseEntity.getBody()).isEqualTo("Колличество носок - " + count);
    }

    @ParameterizedTest
    @MethodSource("negativeParameters")
    public void getQuantityNegativeTest(int cottonPart) {
        SocksRecord socksRecord = addSocks(creatureSocks());
        socksRecord.setCottonPart(cottonPart);

        ResponseEntity<String> countSocksResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port
                + "/api/socks?color=" + socksRecord.getColor() + "&operation=equal&cottonPart=" + socksRecord.getCottonPart(), String.class);
        assertThat(countSocksResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void addOutcomeNegativeTest1() {
        SocksRecord socksRecord = addSocks(creatureSocks());
        socksRecord.setColor("fdr");

        ResponseEntity<String> socksRecordResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/api/socks/outcome", socksRecord, String.class);
        assertThat(socksRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void addOutcomeNegativeTest0() {
        SocksRecord socksRecord = addSocks(creatureSocks());
        socksRecord.setQuantity(socksRecord.getQuantity() + 1);

        ResponseEntity<String> socksRecordResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/api/socks/outcome", socksRecord, String.class);
        assertThat(socksRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addSocksNotNullTest() {
        SocksRecord socksRecord = addSocks(creatureSocks());

        ResponseEntity<String> socksRecordResponseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/api/socks/income", socksRecord, String.class);
        assertThat(socksRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Socks socks = socksRepository.findByCottonPartAndColor(socksRecord.getCottonPart() ,socksRecord.getColor());
        assertThat(socks).isNotNull();
        assertThat(socks.getQuantity()).isEqualTo(socksRecord.getQuantity() * 2);
    }

    @Test
    public void getQuantityNegative2Test() {
        SocksRecord socksRecord = addSocks(creatureSocks());

        ResponseEntity<String> countSocksResponseEntity = testRestTemplate.getForEntity("http://localhost:" + port
                + "/api/socks?color=" + socksRecord.getColor() + "&operation=error&cottonPart=" + socksRecord.getCottonPart(), String.class);
        assertThat(countSocksResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    public static Stream<Arguments> negativeParameters() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(103),
                Arguments.of(-6)
        );
    }
    private SocksRecord creatureSocks() {
        SocksRecord socksRecord = new SocksRecord();
        socksRecord.setColor(faker.color().name());
        socksRecord.setCottonPart(faker.random().nextInt(0, 100));
        socksRecord.setQuantity(faker.random().nextInt(1, 30));
        return socksRecord;
    }

    public static Stream<Arguments> quantityParameters() {
        return Stream.of(
                Arguments.of("black", "moreThan", 50, 16),
                Arguments.of("red", "lessThan", 46, 15),
                Arguments.of("white", "equal", 80, 30)
        );
    }

    private SocksRecord addSocks(SocksRecord socksRecord) {
        ResponseEntity<String> socksRecordResponseEntity = testRestTemplate.postForEntity("http://localhost:" +
                port + "/api/socks/income", socksRecord, String.class);
        assertThat(socksRecordResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(socksRecordResponseEntity.getBody()).isNotNull();
        assertThat(socksRecordResponseEntity.getBody()).isEqualTo("Прием носок осуществлен");
        return socksRecord;
    }

}