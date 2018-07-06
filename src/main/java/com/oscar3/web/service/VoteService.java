package com.oscar3.web.service;

import com.oscar3.web.domain.Vote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Vote.
 */
public interface VoteService {

    /**
     * Save a vote.
     *
     * @param vote the entity to save
     * @return the persisted entity
     */
    Vote save(Vote vote);

    /**
     * Get all the votes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Vote> findAll(Pageable pageable);


    /**
     * Get the "id" vote.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Vote> findOne(Long id);

    /**
     * Delete the "id" vote.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
