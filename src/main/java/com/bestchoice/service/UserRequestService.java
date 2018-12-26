package com.bestchoice.service;

import com.bestchoice.service.dto.UserRequestDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing UserRequest.
 */
public interface UserRequestService {

    /**
     * Save a userRequest.
     *
     * @param userRequestDTO the entity to save
     * @return the persisted entity
     */
    UserRequestDTO save(UserRequestDTO userRequestDTO);

    /**
     * Get all the userRequests.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<UserRequestDTO> findAll(Pageable pageable);


    /**
     * Get the "id" userRequest.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<UserRequestDTO> findOne(String id);

    /**
     * Delete the "id" userRequest.
     *
     * @param id the id of the entity
     */
    void delete(String id);
}
