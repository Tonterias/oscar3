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

import com.oscar3.web.domain.Topic;
import com.oscar3.web.domain.*; // for static metamodels
import com.oscar3.web.repository.TopicRepository;
import com.oscar3.web.service.dto.TopicCriteria;


/**
 * Service for executing complex queries for Topic entities in the database.
 * The main input is a {@link TopicCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Topic} or a {@link Page} of {@link Topic} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TopicQueryService extends QueryService<Topic> {

    private final Logger log = LoggerFactory.getLogger(TopicQueryService.class);

    private final TopicRepository topicRepository;

    public TopicQueryService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    /**
     * Return a {@link List} of {@link Topic} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Topic> findByCriteria(TopicCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Topic> specification = createSpecification(criteria);
        return topicRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Topic} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Topic> findByCriteria(TopicCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Topic> specification = createSpecification(criteria);
        return topicRepository.findAll(specification, page);
    }

    /**
     * Function to convert TopicCriteria to a {@link Specification}
     */
    private Specification<Topic> createSpecification(TopicCriteria criteria) {
        Specification<Topic> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Topic_.id));
            }
            if (criteria.getTopicName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTopicName(), Topic_.topicName));
            }
            if (criteria.getPostId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getPostId(), Topic_.posts, Post_.id));
            }
        }
        return specification;
    }

}
