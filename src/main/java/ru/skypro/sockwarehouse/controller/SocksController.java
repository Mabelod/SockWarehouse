package ru.skypro.sockwarehouse.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;
import ru.skypro.sockwarehouse.dto.SocksRecord;
import ru.skypro.sockwarehouse.service.SocksService;


@RestController
@RequestMapping("/api/socks")
public class SocksController {
    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    @Operation(
            summary = "Регистрация прихода носок",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Регистрация прихода носок"
                    )
            }
    )
    @PostMapping("/income")
    private String addIncome(@RequestBody SocksRecord socksRecord ) {
        return socksService.addIncome(socksRecord);
    }
    @Operation(
            summary = "Регистрация отпуска носок",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Регистрация отпуска носок"
                    )
            }
    )
    @PostMapping("/outcome")
    private String addOutcome(@RequestBody SocksRecord socksRecord) {
        return socksService.addOutcome(socksRecord);
    }

    @Operation(
            summary = "Возвращение общего колличества носок по указанным параметрам",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Возвращение общего колличества носок по указанным параметрам"
                    )
            }
    )

    @GetMapping
    private String getQuantity(@Parameter(description = "Введите цвет носок", example = "red")
                               @RequestParam String color,
                               @Parameter(description = "Введите значение количества хлопка в составе носков." +
                                       " moreThan, lessThan, equal", example = "equal")
                               @RequestParam String operation,
                               @Parameter(description = "Введите значение процента хлопка в составе носок", example = "50")
                               @RequestParam Integer cottonPart) {
        return socksService.getQuantity(color, operation, cottonPart);
    }

}
