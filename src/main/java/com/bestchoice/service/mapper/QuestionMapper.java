package com.bestchoice.service.mapper;

import com.bestchoice.domain.*;
import com.bestchoice.service.dto.QuestionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Question and its DTO QuestionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface QuestionMapper extends EntityMapper<QuestionDTO, Question> {


}
