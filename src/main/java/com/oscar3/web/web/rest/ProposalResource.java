package com.oscar3.web.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.oscar3.web.domain.Proposal;
import com.oscar3.web.service.ProposalService;
import com.oscar3.web.web.rest.errors.BadRequestAlertException;
import com.oscar3.web.web.rest.util.HeaderUtil;
import com.oscar3.web.web.rest.util.PaginationUtil;
import com.oscar3.web.service.dto.ProposalCriteria;
import com.oscar3.web.service.ProposalQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Proposal.
 */
@RestController
@RequestMapping("/api")
public class ProposalResource {

    private final Logger log = LoggerFactory.getLogger(ProposalResource.class);

    private static final String ENTITY_NAME = "proposal";

    private final ProposalService proposalService;

    private final ProposalQueryService proposalQueryService;

    public ProposalResource(ProposalService proposalService, ProposalQueryService proposalQueryService) {
        this.proposalService = proposalService;
        this.proposalQueryService = proposalQueryService;
    }

    /**
     * POST  /proposals : Create a new proposal.
     *
     * @param proposal the proposal to create
     * @return the ResponseEntity with status 201 (Created) and with body the new proposal, or with status 400 (Bad Request) if the proposal has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/proposals")
    @Timed
    public ResponseEntity<Proposal> createProposal(@Valid @RequestBody Proposal proposal) throws URISyntaxException {
        log.debug("REST request to save Proposal : {}", proposal);
        if (proposal.getId() != null) {
            throw new BadRequestAlertException("A new proposal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Proposal result = proposalService.save(proposal);
        return ResponseEntity.created(new URI("/api/proposals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /proposals : Updates an existing proposal.
     *
     * @param proposal the proposal to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated proposal,
     * or with status 400 (Bad Request) if the proposal is not valid,
     * or with status 500 (Internal Server Error) if the proposal couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/proposals")
    @Timed
    public ResponseEntity<Proposal> updateProposal(@Valid @RequestBody Proposal proposal) throws URISyntaxException {
        log.debug("REST request to update Proposal : {}", proposal);
        if (proposal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Proposal result = proposalService.save(proposal);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, proposal.getId().toString()))
            .body(result);
    }

    /**
     * GET  /proposals : get all the proposals.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of proposals in body
     */
    @GetMapping("/proposals")
    @Timed
    public ResponseEntity<List<Proposal>> getAllProposals(ProposalCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Proposals by criteria: {}", criteria);
        Page<Proposal> page = proposalQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/proposals");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /proposals/:id : get the "id" proposal.
     *
     * @param id the id of the proposal to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the proposal, or with status 404 (Not Found)
     */
    @GetMapping("/proposals/{id}")
    @Timed
    public ResponseEntity<Proposal> getProposal(@PathVariable Long id) {
        log.debug("REST request to get Proposal : {}", id);
        Optional<Proposal> proposal = proposalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(proposal);
    }

    /**
     * DELETE  /proposals/:id : delete the "id" proposal.
     *
     * @param id the id of the proposal to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/proposals/{id}")
    @Timed
    public ResponseEntity<Void> deleteProposal(@PathVariable Long id) {
        log.debug("REST request to delete Proposal : {}", id);
        proposalService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
