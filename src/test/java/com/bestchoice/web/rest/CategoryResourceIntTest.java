package com.bestchoice.web.rest;

import com.bestchoice.BestChoiceApp;

import com.bestchoice.domain.Category;
import com.bestchoice.repository.CategoryRepository;
import com.bestchoice.service.CategoryService;
import com.bestchoice.service.dto.CategoryDTO;
import com.bestchoice.service.mapper.CategoryMapper;
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
 * Test class for the CategoryResource REST controller.
 *
 * @see CategoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BestChoiceApp.class)
public class CategoryResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private CategoryMapper categoryMapper;
    

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restCategoryMockMvc;

    private Category category;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CategoryResource categoryResource = new CategoryResource(categoryService);
        this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(categoryResource)
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
    public static Category createEntity() {
        Category category = new Category()
            .name(DEFAULT_NAME)
            .createdAt(DEFAULT_CREATED_AT);
        return category;
    }

    @Before
    public void initTest() {
        categoryRepository.deleteAll();
        category = createEntity();
    }

    @Test
    public void createCategory() throws Exception {
        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);
        restCategoryMockMvc.perform(post("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isCreated());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCategory.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
    }

    @Test
    public void createCategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = categoryRepository.findAll().size();

        // Create the Category with an existing ID
        category.setId("existing_id");
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryMockMvc.perform(post("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllCategories() throws Exception {
        // Initialize the database
        categoryRepository.save(category);

        // Get all the categoryList
        restCategoryMockMvc.perform(get("/api/categories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(sameInstant(DEFAULT_CREATED_AT))));
    }
    

    @Test
    public void getCategory() throws Exception {
        // Initialize the database
        categoryRepository.save(category);

        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", category.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(category.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.createdAt").value(sameInstant(DEFAULT_CREATED_AT)));
    }
    @Test
    public void getNonExistingCategory() throws Exception {
        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCategory() throws Exception {
        // Initialize the database
        categoryRepository.save(category);

        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Update the category
        Category updatedCategory = categoryRepository.findById(category.getId()).get();
        updatedCategory
            .name(UPDATED_NAME)
            .createdAt(UPDATED_CREATED_AT);
        CategoryDTO categoryDTO = categoryMapper.toDto(updatedCategory);

        restCategoryMockMvc.perform(put("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
        Category testCategory = categoryList.get(categoryList.size() - 1);
        assertThat(testCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCategory.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
    }

    @Test
    public void updateNonExistingCategory() throws Exception {
        int databaseSizeBeforeUpdate = categoryRepository.findAll().size();

        // Create the Category
        CategoryDTO categoryDTO = categoryMapper.toDto(category);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCategoryMockMvc.perform(put("/api/categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(categoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Category in the database
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteCategory() throws Exception {
        // Initialize the database
        categoryRepository.save(category);

        int databaseSizeBeforeDelete = categoryRepository.findAll().size();

        // Get the category
        restCategoryMockMvc.perform(delete("/api/categories/{id}", category.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Category> categoryList = categoryRepository.findAll();
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Category.class);
        Category category1 = new Category();
        category1.setId("id1");
        Category category2 = new Category();
        category2.setId(category1.getId());
        assertThat(category1).isEqualTo(category2);
        category2.setId("id2");
        assertThat(category1).isNotEqualTo(category2);
        category1.setId(null);
        assertThat(category1).isNotEqualTo(category2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoryDTO.class);
        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setId("id1");
        CategoryDTO categoryDTO2 = new CategoryDTO();
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
        categoryDTO2.setId(categoryDTO1.getId());
        assertThat(categoryDTO1).isEqualTo(categoryDTO2);
        categoryDTO2.setId("id2");
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
        categoryDTO1.setId(null);
        assertThat(categoryDTO1).isNotEqualTo(categoryDTO2);
    }
}
