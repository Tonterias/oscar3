package com.oscar3.web.web.rest;

import com.oscar3.web.Oscar3App;

import com.oscar3.web.domain.Proposal;
import com.oscar3.web.domain.User;
import com.oscar3.web.domain.Post;
import com.oscar3.web.domain.Vote;
import com.oscar3.web.repository.ProposalRepository;
import com.oscar3.web.service.ProposalService;
import com.oscar3.web.web.rest.errors.ExceptionTranslator;
import com.oscar3.web.service.dto.ProposalCriteria;
import com.oscar3.web.service.ProposalQueryService;

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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static com.oscar3.web.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.oscar3.web.domain.enumeration.ProposalType;
/**
 * Test class for the ProposalResource REST controller.
 *
 * @see ProposalResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Oscar3App.class)
public class ProposalResourceIntTest {

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RELEASE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RELEASE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_FUNCTIONALITY = "AAAAAAAAAA";
    private static final String UPDATED_FUNCTIONALITY = "BBBBBBBBBB";

    private static final ProposalType DEFAULT_PROPOSAL_TYPE = ProposalType.UNDER_STUDY;
    private static final ProposalType UPDATED_PROPOSAL_TYPE = ProposalType.APPROVED;

    @Autowired
    private ProposalRepository proposalRepository;

    

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private ProposalQueryService proposalQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProposalMockMvc;

    private Proposal proposal;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProposalResource proposalResource = new ProposalResource(proposalService, proposalQueryService);
        this.restProposalMockMvc = MockMvcBuilders.standaloneSetup(proposalResource)
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
    public static Proposal createEntity(EntityManager em) {
        Proposal proposal = new Proposal()
            .creationDate(DEFAULT_CREATION_DATE)
            .releaseDate(DEFAULT_RELEASE_DATE)
            .functionality(DEFAULT_FUNCTIONALITY)
            .proposalType(DEFAULT_PROPOSAL_TYPE);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        proposal.setUser(user);
        // Add required entity
        Post post = PostResourceIntTest.createEntity(em);
        em.persist(post);
        em.flush();
        proposal.setPost(post);
        return proposal;
    }

    @Before
    public void initTest() {
        proposal = createEntity(em);
    }

    @Test
    @Transactional
    public void createProposal() throws Exception {
        int databaseSizeBeforeCreate = proposalRepository.findAll().size();

        // Create the Proposal
        restProposalMockMvc.perform(post("/api/proposals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proposal)))
            .andExpect(status().isCreated());

        // Validate the Proposal in the database
        List<Proposal> proposalList = proposalRepository.findAll();
        assertThat(proposalList).hasSize(databaseSizeBeforeCreate + 1);
        Proposal testProposal = proposalList.get(proposalList.size() - 1);
        assertThat(testProposal.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testProposal.getReleaseDate()).isEqualTo(DEFAULT_RELEASE_DATE);
        assertThat(testProposal.getFunctionality()).isEqualTo(DEFAULT_FUNCTIONALITY);
        assertThat(testProposal.getProposalType()).isEqualTo(DEFAULT_PROPOSAL_TYPE);
    }

    @Test
    @Transactional
    public void createProposalWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = proposalRepository.findAll().size();

        // Create the Proposal with an existing ID
        proposal.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProposalMockMvc.perform(post("/api/proposals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proposal)))
            .andExpect(status().isBadRequest());

        // Validate the Proposal in the database
        List<Proposal> proposalList = proposalRepository.findAll();
        assertThat(proposalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = proposalRepository.findAll().size();
        // set the field null
        proposal.setCreationDate(null);

        // Create the Proposal, which fails.

        restProposalMockMvc.perform(post("/api/proposals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proposal)))
            .andExpect(status().isBadRequest());

        List<Proposal> proposalList = proposalRepository.findAll();
        assertThat(proposalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFunctionalityIsRequired() throws Exception {
        int databaseSizeBeforeTest = proposalRepository.findAll().size();
        // set the field null
        proposal.setFunctionality(null);

        // Create the Proposal, which fails.

        restProposalMockMvc.perform(post("/api/proposals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proposal)))
            .andExpect(status().isBadRequest());

        List<Proposal> proposalList = proposalRepository.findAll();
        assertThat(proposalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkProposalTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = proposalRepository.findAll().size();
        // set the field null
        proposal.setProposalType(null);

        // Create the Proposal, which fails.

        restProposalMockMvc.perform(post("/api/proposals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proposal)))
            .andExpect(status().isBadRequest());

        List<Proposal> proposalList = proposalRepository.findAll();
        assertThat(proposalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProposals() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList
        restProposalMockMvc.perform(get("/api/proposals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proposal.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE.toString())))
            .andExpect(jsonPath("$.[*].functionality").value(hasItem(DEFAULT_FUNCTIONALITY.toString())))
            .andExpect(jsonPath("$.[*].proposalType").value(hasItem(DEFAULT_PROPOSAL_TYPE.toString())));
    }
    

    @Test
    @Transactional
    public void getProposal() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get the proposal
        restProposalMockMvc.perform(get("/api/proposals/{id}", proposal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(proposal.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.releaseDate").value(DEFAULT_RELEASE_DATE.toString()))
            .andExpect(jsonPath("$.functionality").value(DEFAULT_FUNCTIONALITY.toString()))
            .andExpect(jsonPath("$.proposalType").value(DEFAULT_PROPOSAL_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getAllProposalsByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where creationDate equals to DEFAULT_CREATION_DATE
        defaultProposalShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the proposalList where creationDate equals to UPDATED_CREATION_DATE
        defaultProposalShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllProposalsByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultProposalShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the proposalList where creationDate equals to UPDATED_CREATION_DATE
        defaultProposalShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllProposalsByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where creationDate is not null
        defaultProposalShouldBeFound("creationDate.specified=true");

        // Get all the proposalList where creationDate is null
        defaultProposalShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllProposalsByReleaseDateIsEqualToSomething() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where releaseDate equals to DEFAULT_RELEASE_DATE
        defaultProposalShouldBeFound("releaseDate.equals=" + DEFAULT_RELEASE_DATE);

        // Get all the proposalList where releaseDate equals to UPDATED_RELEASE_DATE
        defaultProposalShouldNotBeFound("releaseDate.equals=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllProposalsByReleaseDateIsInShouldWork() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where releaseDate in DEFAULT_RELEASE_DATE or UPDATED_RELEASE_DATE
        defaultProposalShouldBeFound("releaseDate.in=" + DEFAULT_RELEASE_DATE + "," + UPDATED_RELEASE_DATE);

        // Get all the proposalList where releaseDate equals to UPDATED_RELEASE_DATE
        defaultProposalShouldNotBeFound("releaseDate.in=" + UPDATED_RELEASE_DATE);
    }

    @Test
    @Transactional
    public void getAllProposalsByReleaseDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where releaseDate is not null
        defaultProposalShouldBeFound("releaseDate.specified=true");

        // Get all the proposalList where releaseDate is null
        defaultProposalShouldNotBeFound("releaseDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllProposalsByFunctionalityIsEqualToSomething() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where functionality equals to DEFAULT_FUNCTIONALITY
        defaultProposalShouldBeFound("functionality.equals=" + DEFAULT_FUNCTIONALITY);

        // Get all the proposalList where functionality equals to UPDATED_FUNCTIONALITY
        defaultProposalShouldNotBeFound("functionality.equals=" + UPDATED_FUNCTIONALITY);
    }

    @Test
    @Transactional
    public void getAllProposalsByFunctionalityIsInShouldWork() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where functionality in DEFAULT_FUNCTIONALITY or UPDATED_FUNCTIONALITY
        defaultProposalShouldBeFound("functionality.in=" + DEFAULT_FUNCTIONALITY + "," + UPDATED_FUNCTIONALITY);

        // Get all the proposalList where functionality equals to UPDATED_FUNCTIONALITY
        defaultProposalShouldNotBeFound("functionality.in=" + UPDATED_FUNCTIONALITY);
    }

    @Test
    @Transactional
    public void getAllProposalsByFunctionalityIsNullOrNotNull() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where functionality is not null
        defaultProposalShouldBeFound("functionality.specified=true");

        // Get all the proposalList where functionality is null
        defaultProposalShouldNotBeFound("functionality.specified=false");
    }

    @Test
    @Transactional
    public void getAllProposalsByProposalTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where proposalType equals to DEFAULT_PROPOSAL_TYPE
        defaultProposalShouldBeFound("proposalType.equals=" + DEFAULT_PROPOSAL_TYPE);

        // Get all the proposalList where proposalType equals to UPDATED_PROPOSAL_TYPE
        defaultProposalShouldNotBeFound("proposalType.equals=" + UPDATED_PROPOSAL_TYPE);
    }

    @Test
    @Transactional
    public void getAllProposalsByProposalTypeIsInShouldWork() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where proposalType in DEFAULT_PROPOSAL_TYPE or UPDATED_PROPOSAL_TYPE
        defaultProposalShouldBeFound("proposalType.in=" + DEFAULT_PROPOSAL_TYPE + "," + UPDATED_PROPOSAL_TYPE);

        // Get all the proposalList where proposalType equals to UPDATED_PROPOSAL_TYPE
        defaultProposalShouldNotBeFound("proposalType.in=" + UPDATED_PROPOSAL_TYPE);
    }

    @Test
    @Transactional
    public void getAllProposalsByProposalTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        proposalRepository.saveAndFlush(proposal);

        // Get all the proposalList where proposalType is not null
        defaultProposalShouldBeFound("proposalType.specified=true");

        // Get all the proposalList where proposalType is null
        defaultProposalShouldNotBeFound("proposalType.specified=false");
    }

    @Test
    @Transactional
    public void getAllProposalsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        proposal.setUser(user);
        proposalRepository.saveAndFlush(proposal);
        Long userId = user.getId();

        // Get all the proposalList where user equals to userId
        defaultProposalShouldBeFound("userId.equals=" + userId);

        // Get all the proposalList where user equals to userId + 1
        defaultProposalShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllProposalsByPostIsEqualToSomething() throws Exception {
        // Initialize the database
        Post post = PostResourceIntTest.createEntity(em);
        em.persist(post);
        em.flush();
        proposal.setPost(post);
        proposalRepository.saveAndFlush(proposal);
        Long postId = post.getId();

        // Get all the proposalList where post equals to postId
        defaultProposalShouldBeFound("postId.equals=" + postId);

        // Get all the proposalList where post equals to postId + 1
        defaultProposalShouldNotBeFound("postId.equals=" + (postId + 1));
    }


    @Test
    @Transactional
    public void getAllProposalsByVoteIsEqualToSomething() throws Exception {
        // Initialize the database
        Vote vote = VoteResourceIntTest.createEntity(em);
        em.persist(vote);
        em.flush();
        proposal.addVote(vote);
        proposalRepository.saveAndFlush(proposal);
        Long voteId = vote.getId();

        // Get all the proposalList where vote equals to voteId
        defaultProposalShouldBeFound("voteId.equals=" + voteId);

        // Get all the proposalList where vote equals to voteId + 1
        defaultProposalShouldNotBeFound("voteId.equals=" + (voteId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultProposalShouldBeFound(String filter) throws Exception {
        restProposalMockMvc.perform(get("/api/proposals?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(proposal.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE.toString())))
            .andExpect(jsonPath("$.[*].functionality").value(hasItem(DEFAULT_FUNCTIONALITY.toString())))
            .andExpect(jsonPath("$.[*].proposalType").value(hasItem(DEFAULT_PROPOSAL_TYPE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultProposalShouldNotBeFound(String filter) throws Exception {
        restProposalMockMvc.perform(get("/api/proposals?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingProposal() throws Exception {
        // Get the proposal
        restProposalMockMvc.perform(get("/api/proposals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProposal() throws Exception {
        // Initialize the database
        proposalService.save(proposal);

        int databaseSizeBeforeUpdate = proposalRepository.findAll().size();

        // Update the proposal
        Proposal updatedProposal = proposalRepository.findById(proposal.getId()).get();
        // Disconnect from session so that the updates on updatedProposal are not directly saved in db
        em.detach(updatedProposal);
        updatedProposal
            .creationDate(UPDATED_CREATION_DATE)
            .releaseDate(UPDATED_RELEASE_DATE)
            .functionality(UPDATED_FUNCTIONALITY)
            .proposalType(UPDATED_PROPOSAL_TYPE);

        restProposalMockMvc.perform(put("/api/proposals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProposal)))
            .andExpect(status().isOk());

        // Validate the Proposal in the database
        List<Proposal> proposalList = proposalRepository.findAll();
        assertThat(proposalList).hasSize(databaseSizeBeforeUpdate);
        Proposal testProposal = proposalList.get(proposalList.size() - 1);
        assertThat(testProposal.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testProposal.getReleaseDate()).isEqualTo(UPDATED_RELEASE_DATE);
        assertThat(testProposal.getFunctionality()).isEqualTo(UPDATED_FUNCTIONALITY);
        assertThat(testProposal.getProposalType()).isEqualTo(UPDATED_PROPOSAL_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingProposal() throws Exception {
        int databaseSizeBeforeUpdate = proposalRepository.findAll().size();

        // Create the Proposal

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProposalMockMvc.perform(put("/api/proposals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(proposal)))
            .andExpect(status().isBadRequest());

        // Validate the Proposal in the database
        List<Proposal> proposalList = proposalRepository.findAll();
        assertThat(proposalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProposal() throws Exception {
        // Initialize the database
        proposalService.save(proposal);

        int databaseSizeBeforeDelete = proposalRepository.findAll().size();

        // Get the proposal
        restProposalMockMvc.perform(delete("/api/proposals/{id}", proposal.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Proposal> proposalList = proposalRepository.findAll();
        assertThat(proposalList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Proposal.class);
        Proposal proposal1 = new Proposal();
        proposal1.setId(1L);
        Proposal proposal2 = new Proposal();
        proposal2.setId(proposal1.getId());
        assertThat(proposal1).isEqualTo(proposal2);
        proposal2.setId(2L);
        assertThat(proposal1).isNotEqualTo(proposal2);
        proposal1.setId(null);
        assertThat(proposal1).isNotEqualTo(proposal2);
    }
}
