package com.billeteraVirtual.transacciones.Mapper;

import com.billeteraVirtual.transacciones.dto.TransactionDTO;

import com.billeteraVirtual.transacciones.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionDTO toDto(Transaction user);

    Transaction toEntity(TransactionDTO dto);

}
