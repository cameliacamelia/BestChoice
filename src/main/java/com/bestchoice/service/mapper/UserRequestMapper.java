package com.bestchoice.service.mapper;

import com.bestchoice.domain.*;
import com.bestchoice.service.dto.UserRequestDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UserRequest and its DTO UserRequestDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserRequestMapper extends EntityMapper<UserRequestDTO, UserRequest> {


}
