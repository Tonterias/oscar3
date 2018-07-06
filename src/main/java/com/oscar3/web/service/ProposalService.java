package com.oscar3.web.service;

import com.oscar3.web.domain.Proposal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Proposal.
 */
public interface ProposalService {

    /**
     * Save a proposal.
     *
     * @param proposal the entity to save
     * @return the persisted entity
     */
    Proposal save(Proposal proposal);

    /**
     * Get all the proposals.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Proposal> findAll(Pageable pageable);


    /**
     * Get the "id" proposal.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Proposal> findOne(Long id);

    /**
     * Delete the "id" proposal.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
