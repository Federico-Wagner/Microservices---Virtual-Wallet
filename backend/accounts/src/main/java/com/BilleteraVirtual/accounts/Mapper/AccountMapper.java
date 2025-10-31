package com.BilleteraVirtual.accounts.Mapper;

import com.BilleteraVirtual.accounts.dto.AccountDTO;
import com.BilleteraVirtual.accounts.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDTO toDto(Account user);

    Account toEntity(AccountDTO dto);
}
