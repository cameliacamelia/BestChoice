package com.bestchoice.service.mapper;

import com.bestchoice.domain.*;
import com.bestchoice.service.dto.CategoryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Category and its DTO CategoryDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {


}
