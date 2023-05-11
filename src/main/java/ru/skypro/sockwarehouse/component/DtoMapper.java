package ru.skypro.sockwarehouse.component;


import org.mapstruct.*;
import ru.skypro.sockwarehouse.dto.SocksRecord;
import ru.skypro.sockwarehouse.model.Socks;

@Mapper(componentModel = "spring")
public interface DtoMapper {
    @Mapping(target = "color", expression = "java(socksRecord.getColor().toLowerCase())")
    @Mapping(target = "cottonPart", source = "cottonPart")
    @Mapping(target = "quantity", source = "quantity")
    Socks toSocksEntity(SocksRecord socksRecord);
}
