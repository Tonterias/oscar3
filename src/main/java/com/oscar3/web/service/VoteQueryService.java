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

import com.oscar3.web.domain.Vote;
import com.oscar3.web.domain.*; // for static metamodels
import com.oscar3.web.repository.VoteRepository;
import com.oscar3.web.service.dto.VoteCriteria;


/**
 * Service for executing complex queries for Vote entities in the database.
 * The main input is a {@link VoteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Vote} or a {@link Page} of {@link Vote} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VoteQueryService extends QueryService<Vote> {

    private final Logger log = LoggerFactory.getLogger(VoteQueryService.class);

    private final VoteRepository voteRepository;

    public VoteQueryService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    /**
     * Return a {@link List} of {@link Vote} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Vote> findByCriteria(VoteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Vote> specification = createSpecification(criteria);
        return voteRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Vote} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Vote> findByCriteria(VoteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Vote> specification = createSpecification(criteria);
        return voteRepository.findAll(specification, page);
    }

    /**
     * Function to convert VoteCriteria to a {@link Specification}
     */
    private Specification<Vote> createSpecification(VoteCriteria criteria) {
        Specification<Vote> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Vote_.id));
            }
            if (criteria.getCreationDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreationDate(), Vote_.creationDate));
            }
            if (criteria.getNumberOfPoints() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumberOfPoints(), Vote_.numberOfPoints));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getUserId(), Vote_.user, User_.id));
            }
            if (criteria.getProposalId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getProposalId(), Vote_.proposal, Proposal_.id));
            }
        }
        return specification;
    }

}
