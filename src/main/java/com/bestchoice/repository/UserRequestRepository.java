package com.bestchoice.repository;

import com.bestchoice.domain.UserRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the UserRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserRequestRepository extends MongoRepository<UserRequest, String> {

}
