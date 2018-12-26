package com.bestchoice.service.impl;

import com.bestchoice.service.UserRequestService;
import com.bestchoice.domain.UserRequest;
import com.bestchoice.repository.UserRequestRepository;
import com.bestchoice.service.dto.UserRequestDTO;
import com.bestchoice.service.mapper.UserRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.Optional;
/**
 * Service Implementation for managing UserRequest.
 */
@Service
public class UserRequestServiceImpl implements UserRequestService {

    private final Logger log = LoggerFactory.getLogger(UserRequestServiceImpl.class);

    private final UserRequestRepository userRequestRepository;

    private final UserRequestMapper userRequestMapper;

    public UserRequestServiceImpl(UserRequestRepository userRequestRepository, UserRequestMapper userRequestMapper) {
        this.userRequestRepository = userRequestRepository;
        this.userRequestMapper = userRequestMapper;
    }

    /**
     * Save a userRequest.
     *
     * @param userRequestDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public UserRequestDTO save(UserRequestDTO userRequestDTO) {
        log.debug("Request to save UserRequest : {}", userRequestDTO);
        UserRequest userRequest = userRequestMapper.toEntity(userRequestDTO);
        userRequest = userRequestRepository.save(userRequest);
        return userRequestMapper.toDto(userRequest);
    }

    /**
     * Get all the userRequests.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<UserRequestDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserRequests");
        return userRequestRepository.findAll(pageable)
            .map(userRequestMapper::toDto);
    }


    /**
     * Get one userRequest by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<UserRequestDTO> findOne(String id) {
        log.debug("Request to get UserRequest : {}", id);
        return userRequestRepository.findById(id)
            .map(userRequestMapper::toDto);
    }

    /**
     * Delete the userRequest by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete UserRequest : {}", id);
        userRequestRepository.deleteById(id);
    }
}
