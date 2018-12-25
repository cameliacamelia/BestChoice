package com.bestchoice.web.rest;

import com.bestchoice.BestChoiceApp;

import com.bestchoice.domain.Question;
import com.bestchoice.repository.QuestionRepository;
import com.bestchoice.service.QuestionService;
import com.bestchoice.service.dto.QuestionDTO;
import com.bestchoice.service.mapper.QuestionMapper;
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
 * Test class for the QuestionResource REST controller.
 *
 * @see QuestionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BestChoiceApp.class)
public class QuestionResourceIntTest {

    private static final String DEFAULT_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CATEGORY_ID = 1;
    private static final Integer UPDATED_CATEGORY_ID = 2;

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private QuestionRepository questionRepository;


    @Autowired
    private QuestionMapper questionMapper;
    

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restQuestionMockMvc;

    private Question question;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final QuestionResource questionResource = new QuestionResource(questionService);
        this.restQuestionMockMvc = MockMvcBuilders.standaloneSetup(questionResource)
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
    public static Question createEntity() {
        Question question = new Question()
            .question(DEFAULT_QUESTION)
            .categoryId(DEFAULT_CATEGORY_ID)
            .createdAt(DEFAULT_CREATED_AT);
        return question;
    }

    @Before
    public void initTest() {
        questionRepository.deleteAll();
        question = createEntity();
    }

    @Test
    public void createQuestion() throws Exception {
        int databaseSizeBeforeCreate = questionRepository.findAll().size();

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);
        restQuestionMockMvc.perform(post("/api/questions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(questionDTO)))
            .andExpect(status().isCreated());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeCreate + 1);
        Question testQuestion = questionList.get(questionList.size() - 1);
        assertThat(testQuestion.getQuestion()).isEqualTo(DEFAULT_QUESTION);
        assertThat(testQuestion.getCategoryId()).isEqualTo(DEFAULT_CATEGORY_ID);
        assertThat(testQuestion.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    public void createQuestionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = questionRepository.findAll().size();

        // Create the Question with an existing ID
        question.setId("existing_id");
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuestionMockMvc.perform(post("/api/questions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllQuestions() throws Exception {
        // Initialize the database
        questionRepository.save(question);

        // Get all the questionList
        restQuestionMockMvc.perform(get("/api/questions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(question.getId())))
            .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION.toString())))
            .andExpect(jsonPath("$.[*].categoryId").value(hasItem(DEFAULT_CATEGORY_ID)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }
    

    @Test
    public void getQuestion() throws Exception {
        // Initialize the database
        questionRepository.save(question);

        // Get the question
        restQuestionMockMvc.perform(get("/api/questions/{id}", question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(question.getId()))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION.toString()))
            .andExpect(jsonPath("$.categoryId").value(DEFAULT_CATEGORY_ID))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }
    @Test
    public void getNonExistingQuestion() throws Exception {
        // Get the question
        restQuestionMockMvc.perform(get("/api/questions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateQuestion() throws Exception {
        // Initialize the database
        questionRepository.save(question);

        int databaseSizeBeforeUpdate = questionRepository.findAll().size();

        // Update the question
        Question updatedQuestion = questionRepository.findById(question.getId()).get();
        updatedQuestion
            .question(UPDATED_QUESTION)
            .categoryId(UPDATED_CATEGORY_ID)
            .createdAt(UPDATED_CREATED_AT);
        QuestionDTO questionDTO = questionMapper.toDto(updatedQuestion);

        restQuestionMockMvc.perform(put("/api/questions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(questionDTO)))
            .andExpect(status().isOk());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
        Question testQuestion = questionList.get(questionList.size() - 1);
        assertThat(testQuestion.getQuestion()).isEqualTo(UPDATED_QUESTION);
        assertThat(testQuestion.getCategoryId()).isEqualTo(UPDATED_CATEGORY_ID);
        assertThat(testQuestion.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    public void updateNonExistingQuestion() throws Exception {
        int databaseSizeBeforeUpdate = questionRepository.findAll().size();

        // Create the Question
        QuestionDTO questionDTO = questionMapper.toDto(question);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restQuestionMockMvc.perform(put("/api/questions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(questionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Question in the database
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteQuestion() throws Exception {
        // Initialize the database
        questionRepository.save(question);

        int databaseSizeBeforeDelete = questionRepository.findAll().size();

        // Get the question
        restQuestionMockMvc.perform(delete("/api/questions/{id}", question.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Question> questionList = questionRepository.findAll();
        assertThat(questionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = new Question();
        question1.setId("id1");
        Question question2 = new Question();
        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);
        question2.setId("id2");
        assertThat(question1).isNotEqualTo(question2);
        question1.setId(null);
        assertThat(question1).isNotEqualTo(question2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionDTO.class);
        QuestionDTO questionDTO1 = new QuestionDTO();
        questionDTO1.setId("id1");
        QuestionDTO questionDTO2 = new QuestionDTO();
        assertThat(questionDTO1).isNotEqualTo(questionDTO2);
        questionDTO2.setId(questionDTO1.getId());
        assertThat(questionDTO1).isEqualTo(questionDTO2);
        questionDTO2.setId("id2");
        assertThat(questionDTO1).isNotEqualTo(questionDTO2);
        questionDTO1.setId(null);
        assertThat(questionDTO1).isNotEqualTo(questionDTO2);
    }
}
