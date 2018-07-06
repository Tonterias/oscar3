package com.oscar3.web.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.oscar3.web.domain.Proposal;
import com.oscar3.web.domain.*; // for static metamodels
import com.oscar3.web.repository.ProposalRepository;
import com.oscar3.web.service.dto.ProposalCriteria;


/**
 * Service for executing complex queries for Proposal entities in the database.
 * The main input is a {@link ProposalCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Proposal} or a {@link Page} of {@link Proposal} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProposalQueryService extends QueryService<Proposal> {

    private final Logger log = LoggerFactory.getLogger(ProposalQueryService.class);

    private final ProposalRepository proposalRepository;

    public ProposalQueryService(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    /**
     * Return a {@link List} of {@link Proposal} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Proposal> findByCriteria(ProposalCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Proposal> specification = createSpecification(criteria);
        return proposalRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Proposal} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Proposal> findByCriteria(ProposalCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Proposal> specification = createSpecification(criteria);
        return proposalRepository.findAll(specification, page);
    }

    /**
     * Function to convert ProposalCriteria to a {@link Specification}
     */
    private Specification<Proposal> createSpecification(ProposalCriteria criteria) {
        Specification<Proposal> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Proposal_.id));
            }
            if (criteria.getCreationDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreationDate(), Proposal_.creationDate));
            }
            if (criteria.getReleaseDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReleaseDate(), Proposal_.releaseDate));
            }
            if (criteria.getFunctionality() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFunctionality(), Proposal_.functionality));
            }
            if (criteria.getProposalType() != null) {
                specification = specification.and(buildSpecification(criteria.getProposalType(), Proposal_.proposalType));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getUserId(), Proposal_.user, User_.id));
            }
            if (criteria.getPostId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getPostId(), Proposal_.post, Post_.id));
            }
            if (criteria.getVoteId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getVoteId(), Proposal_.votes, Vote_.id));
            }
        }
        return specification;
    }

}
