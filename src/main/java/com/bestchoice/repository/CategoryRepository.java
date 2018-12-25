package com.bestchoice.repository;

import com.bestchoice.domain.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

}
