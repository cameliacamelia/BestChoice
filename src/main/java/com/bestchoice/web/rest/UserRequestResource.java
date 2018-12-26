package com.bestchoice.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.bestchoice.service.UserRequestService;
import com.bestchoice.web.rest.errors.BadRequestAlertException;
import com.bestchoice.web.rest.util.HeaderUtil;
import com.bestchoice.web.rest.util.PaginationUtil;
import com.bestchoice.service.dto.UserRequestDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing UserRequest.
 */
@RestController
@RequestMapping("/api")
public class UserRequestResource {

    private final Logger log = LoggerFactory.getLogger(UserRequestResource.class);

    private static final String ENTITY_NAME = "userRequest";

    private final UserRequestService userRequestService;

    public UserRequestResource(UserRequestService userRequestService) {
        this.userRequestService = userRequestService;
    }

    /**
     * POST  /user-requests : Create a new userRequest.
     *
     * @param userRequestDTO the userRequestDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userRequestDTO, or with status 400 (Bad Request) if the userRequest has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-requests")
    @Timed
    public ResponseEntity<UserRequestDTO> createUserRequest(@RequestBody UserRequestDTO userRequestDTO) throws URISyntaxException {
        log.debug("REST request to save UserRequest : {}", userRequestDTO);
        if (userRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new userRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserRequestDTO result = userRequestService.save(userRequestDTO);
        return ResponseEntity.created(new URI("/api/user-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-requests : Updates an existing userRequest.
     *
     * @param userRequestDTO the userRequestDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userRequestDTO,
     * or with status 400 (Bad Request) if the userRequestDTO is not valid,
     * or with status 500 (Internal Server Error) if the userRequestDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-requests")
    @Timed
    public ResponseEntity<UserRequestDTO> updateUserRequest(@RequestBody UserRequestDTO userRequestDTO) throws URISyntaxException {
        log.debug("REST request to update UserRequest : {}", userRequestDTO);
        if (userRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserRequestDTO result = userRequestService.save(userRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userRequestDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-requests : get all the userRequests.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of userRequests in body
     */
    @GetMapping("/user-requests")
    @Timed
    public ResponseEntity<List<UserRequestDTO>> getAllUserRequests(Pageable pageable) {
        log.debug("REST request to get a page of UserRequests");
        Page<UserRequestDTO> page = userRequestService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-requests");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-requests/:id : get the "id" userRequest.
     *
     * @param id the id of the userRequestDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userRequestDTO, or with status 404 (Not Found)
     */
    @GetMapping("/user-requests/{id}")
    @Timed
    public ResponseEntity<UserRequestDTO> getUserRequest(@PathVariable String id) {
        log.debug("REST request to get UserRequest : {}", id);
        Optional<UserRequestDTO> userRequestDTO = userRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userRequestDTO);
    }

    /**
     * DELETE  /user-requests/:id : delete the "id" userRequest.
     *
     * @param id the id of the userRequestDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-requests/{id}")
    @Timed
    public ResponseEntity<Void> deleteUserRequest(@PathVariable String id) {
        log.debug("REST request to delete UserRequest : {}", id);
        userRequestService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
