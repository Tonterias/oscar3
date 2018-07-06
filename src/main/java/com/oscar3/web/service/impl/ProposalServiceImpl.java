package com.oscar3.web.service.impl;

import com.oscar3.web.service.ProposalService;
import com.oscar3.web.domain.Proposal;
import com.oscar3.web.repository.ProposalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
/**
 * Service Implementation for managing Proposal.
 */
@Service
@Transactional
public class ProposalServiceImpl implements ProposalService {

    private final Logger log = LoggerFactory.getLogger(ProposalServiceImpl.class);

    private final ProposalRepository proposalRepository;

    public ProposalServiceImpl(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    /**
     * Save a proposal.
     *
     * @param proposal the entity to save
     * @return the persisted entity
     */
    @Override
    public Proposal save(Proposal proposal) {
        log.debug("Request to save Proposal : {}", proposal);        return proposalRepository.save(proposal);
    }

    /**
     * Get all the proposals.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Proposal> findAll(Pageable pageable) {
        log.debug("Request to get all Proposals");
        return proposalRepository.findAll(pageable);
    }


    /**
     * Get one proposal by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Proposal> findOne(Long id) {
        log.debug("Request to get Proposal : {}", id);
        return proposalRepository.findById(id);
    }

    /**
     * Delete the proposal by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Proposal : {}", id);
        proposalRepository.deleteById(id);
    }
}
