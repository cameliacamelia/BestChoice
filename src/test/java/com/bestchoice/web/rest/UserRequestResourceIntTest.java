package com.bestchoice.web.rest;

import com.bestchoice.BestChoiceApp;

import com.bestchoice.domain.UserRequest;
import com.bestchoice.repository.UserRequestRepository;
import com.bestchoice.service.UserRequestService;
import com.bestchoice.service.dto.UserRequestDTO;
import com.bestchoice.service.mapper.UserRequestMapper;
import com.bestchoice.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;


import static com.bestchoice.web.rest.TestUtil.sameInstant;
import static com.bestchoice.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserRequestResource REST controller.
 *
 * @see UserRequestResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BestChoiceApp.class)
public class UserRequestResourceIntTest {

    private static final String DEFAULT_REQUEST = "AAAAAAAAAA";
    private static final String UPDATED_REQUEST = "BBBBBBBBBB";

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private UserRequestRepository userRequestRepository;


    @Autowired
    private UserRequestMapper userRequestMapper;
    

    @Autowired
    private UserRequestService userRequestService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restUserRequestMockMvc;

    private UserRequest userRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserRequestResource userRequestResource = new UserRequestResource(userRequestService);
        this.restUserRequestMockMvc = MockMvcBuilders.standaloneSetup(userRequestResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserRequest createEntity() {
        UserRequest userRequest = new UserRequest()
            .request(DEFAULT_REQUEST)
            .userId(DEFAULT_USER_ID)
            .createdAt(DEFAULT_CREATED_AT);
        return userRequest;
    }

    @Before
    public void initTest() {
        userRequestRepository.deleteAll();
        userRequest = createEntity();
    }

    @Test
    public void createUserRequest() throws Exception {
        int databaseSizeBeforeCreate = userRequestRepository.findAll().size();

        // Create the UserRequest
        UserRequestDTO userRequestDTO = userRequestMapper.toDto(userRequest);
        restUserRequestMockMvc.perform(post("/api/user-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userRequestDTO)))
            .andExpect(status().isCreated());

        // Validate the UserRequest in the database
        List<UserRequest> userRequestList = userRequestRepository.findAll();
        assertThat(userRequestList).hasSize(databaseSizeBeforeCreate + 1);
        UserRequest testUserRequest = userRequestList.get(userRequestList.size() - 1);
        assertThat(testUserRequest.getRequest()).isEqualTo(DEFAULT_REQUEST);
        assertThat(testUserRequest.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserRequest.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    public void createUserRequestWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRequestRepository.findAll().size();

        // Create the UserRequest with an existing ID
        userRequest.setId("existing_id");
        UserRequestDTO userRequestDTO = userRequestMapper.toDto(userRequest);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserRequestMockMvc.perform(post("/api/user-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserRequest in the database
        List<UserRequest> userRequestList = userRequestRepository.findAll();
        assertThat(userRequestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllUserRequests() throws Exception {
        // Initialize the database
        userRequestRepository.save(userRequest);

        // Get all the userRequestList
        restUserRequestMockMvc.perform(get("/api/user-requests?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userRequest.getId())))
            .andExpect(jsonPath("$.[*].request").value(hasItem(DEFAULT_REQUEST.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }
    

    @Test
    public void getUserRequest() throws Exception {
        // Initialize the database
        userRequestRepository.save(userRequest);

        // Get the userRequest
        restUserRequestMockMvc.perform(get("/api/user-requests/{id}", userRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userRequest.getId()))
            .andExpect(jsonPath("$.request").value(DEFAULT_REQUEST.toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }
    @Test
    public void getNonExistingUserRequest() throws Exception {
        // Get the userRequest
        restUserRequestMockMvc.perform(get("/api/user-requests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateUserRequest() throws Exception {
        // Initialize the database
        userRequestRepository.save(userRequest);

        int databaseSizeBeforeUpdate = userRequestRepository.findAll().size();

        // Update the userRequest
        UserRequest updatedUserRequest = userRequestRepository.findById(userRequest.getId()).get();
        updatedUserRequest
            .request(UPDATED_REQUEST)
            .userId(UPDATED_USER_ID)
            .createdAt(UPDATED_CREATED_AT);
        UserRequestDTO userRequestDTO = userRequestMapper.toDto(updatedUserRequest);

        restUserRequestMockMvc.perform(put("/api/user-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userRequestDTO)))
            .andExpect(status().isOk());

        // Validate the UserRequest in the database
        List<UserRequest> userRequestList = userRequestRepository.findAll();
        assertThat(userRequestList).hasSize(databaseSizeBeforeUpdate);
        UserRequest testUserRequest = userRequestList.get(userRequestList.size() - 1);
        assertThat(testUserRequest.getRequest()).isEqualTo(UPDATED_REQUEST);
        assertThat(testUserRequest.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserRequest.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    public void updateNonExistingUserRequest() throws Exception {
        int databaseSizeBeforeUpdate = userRequestRepository.findAll().size();

        // Create the UserRequest
        UserRequestDTO userRequestDTO = userRequestMapper.toDto(userRequest);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserRequestMockMvc.perform(put("/api/user-requests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserRequest in the database
        List<UserRequest> userRequestList = userRequestRepository.findAll();
        assertThat(userRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteUserRequest() throws Exception {
        // Initialize the database
        userRequestRepository.save(userRequest);

        int databaseSizeBeforeDelete = userRequestRepository.findAll().size();

        // Get the userRequest
        restUserRequestMockMvc.perform(delete("/api/user-requests/{id}", userRequest.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserRequest> userRequestList = userRequestRepository.findAll();
        assertThat(userRequestList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserRequest.class);
        UserRequest userRequest1 = new UserRequest();
        userRequest1.setId("id1");
        UserRequest userRequest2 = new UserRequest();
        userRequest2.setId(userRequest1.getId());
        assertThat(userRequest1).isEqualTo(userRequest2);
        userRequest2.setId("id2");
        assertThat(userRequest1).isNotEqualTo(userRequest2);
        userRequest1.setId(null);
        assertThat(userRequest1).isNotEqualTo(userRequest2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserRequestDTO.class);
        UserRequestDTO userRequestDTO1 = new UserRequestDTO();
        userRequestDTO1.setId("id1");
        UserRequestDTO userRequestDTO2 = new UserRequestDTO();
        assertThat(userRequestDTO1).isNotEqualTo(userRequestDTO2);
        userRequestDTO2.setId(userRequestDTO1.getId());
        assertThat(userRequestDTO1).isEqualTo(userRequestDTO2);
        userRequestDTO2.setId("id2");
        assertThat(userRequestDTO1).isNotEqualTo(userRequestDTO2);
        userRequestDTO1.setId(null);
        assertThat(userRequestDTO1).isNotEqualTo(userRequestDTO2);
    }
}
