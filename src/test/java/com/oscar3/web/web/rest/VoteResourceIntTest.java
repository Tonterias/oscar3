package com.oscar3.web.web.rest;

import com.oscar3.web.Oscar3App;

import com.oscar3.web.domain.Vote;
import com.oscar3.web.domain.User;
import com.oscar3.web.domain.Proposal;
import com.oscar3.web.repository.VoteRepository;
import com.oscar3.web.service.VoteService;
import com.oscar3.web.web.rest.errors.ExceptionTranslator;
import com.oscar3.web.service.dto.VoteCriteria;
import com.oscar3.web.service.VoteQueryService;

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

/**
 * Test class for the VoteResource REST controller.
 *
 * @see VoteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Oscar3App.class)
public class VoteResourceIntTest {

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_NUMBER_OF_POINTS = 1;
    private static final Integer UPDATED_NUMBER_OF_POINTS = 2;

    @Autowired
    private VoteRepository voteRepository;

    

    @Autowired
    private VoteService voteService;

    @Autowired
    private VoteQueryService voteQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restVoteMockMvc;

    private Vote vote;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final VoteResource voteResource = new VoteResource(voteService, voteQueryService);
        this.restVoteMockMvc = MockMvcBuilders.standaloneSetup(voteResource)
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
    public static Vote createEntity(EntityManager em) {
        Vote vote = new Vote()
            .creationDate(DEFAULT_CREATION_DATE)
            .numberOfPoints(DEFAULT_NUMBER_OF_POINTS);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        vote.setUser(user);
        // Add required entity
        Proposal proposal = ProposalResourceIntTest.createEntity(em);
        em.persist(proposal);
        em.flush();
        vote.setProposal(proposal);
        return vote;
    }

    @Before
    public void initTest() {
        vote = createEntity(em);
    }

    @Test
    @Transactional
    public void createVote() throws Exception {
        int databaseSizeBeforeCreate = voteRepository.findAll().size();

        // Create the Vote
        restVoteMockMvc.perform(post("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vote)))
            .andExpect(status().isCreated());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeCreate + 1);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testVote.getNumberOfPoints()).isEqualTo(DEFAULT_NUMBER_OF_POINTS);
    }

    @Test
    @Transactional
    public void createVoteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = voteRepository.findAll().size();

        // Create the Vote with an existing ID
        vote.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restVoteMockMvc.perform(post("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vote)))
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = voteRepository.findAll().size();
        // set the field null
        vote.setCreationDate(null);

        // Create the Vote, which fails.

        restVoteMockMvc.perform(post("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vote)))
            .andExpect(status().isBadRequest());

        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllVotes() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList
        restVoteMockMvc.perform(get("/api/votes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].numberOfPoints").value(hasItem(DEFAULT_NUMBER_OF_POINTS)));
    }
    

    @Test
    @Transactional
    public void getVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get the vote
        restVoteMockMvc.perform(get("/api/votes/{id}", vote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(vote.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.numberOfPoints").value(DEFAULT_NUMBER_OF_POINTS));
    }

    @Test
    @Transactional
    public void getAllVotesByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where creationDate equals to DEFAULT_CREATION_DATE
        defaultVoteShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the voteList where creationDate equals to UPDATED_CREATION_DATE
        defaultVoteShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllVotesByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultVoteShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the voteList where creationDate equals to UPDATED_CREATION_DATE
        defaultVoteShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllVotesByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where creationDate is not null
        defaultVoteShouldBeFound("creationDate.specified=true");

        // Get all the voteList where creationDate is null
        defaultVoteShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllVotesByNumberOfPointsIsEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where numberOfPoints equals to DEFAULT_NUMBER_OF_POINTS
        defaultVoteShouldBeFound("numberOfPoints.equals=" + DEFAULT_NUMBER_OF_POINTS);

        // Get all the voteList where numberOfPoints equals to UPDATED_NUMBER_OF_POINTS
        defaultVoteShouldNotBeFound("numberOfPoints.equals=" + UPDATED_NUMBER_OF_POINTS);
    }

    @Test
    @Transactional
    public void getAllVotesByNumberOfPointsIsInShouldWork() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where numberOfPoints in DEFAULT_NUMBER_OF_POINTS or UPDATED_NUMBER_OF_POINTS
        defaultVoteShouldBeFound("numberOfPoints.in=" + DEFAULT_NUMBER_OF_POINTS + "," + UPDATED_NUMBER_OF_POINTS);

        // Get all the voteList where numberOfPoints equals to UPDATED_NUMBER_OF_POINTS
        defaultVoteShouldNotBeFound("numberOfPoints.in=" + UPDATED_NUMBER_OF_POINTS);
    }

    @Test
    @Transactional
    public void getAllVotesByNumberOfPointsIsNullOrNotNull() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where numberOfPoints is not null
        defaultVoteShouldBeFound("numberOfPoints.specified=true");

        // Get all the voteList where numberOfPoints is null
        defaultVoteShouldNotBeFound("numberOfPoints.specified=false");
    }

    @Test
    @Transactional
    public void getAllVotesByNumberOfPointsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where numberOfPoints greater than or equals to DEFAULT_NUMBER_OF_POINTS
        defaultVoteShouldBeFound("numberOfPoints.greaterOrEqualThan=" + DEFAULT_NUMBER_OF_POINTS);

        // Get all the voteList where numberOfPoints greater than or equals to UPDATED_NUMBER_OF_POINTS
        defaultVoteShouldNotBeFound("numberOfPoints.greaterOrEqualThan=" + UPDATED_NUMBER_OF_POINTS);
    }

    @Test
    @Transactional
    public void getAllVotesByNumberOfPointsIsLessThanSomething() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the voteList where numberOfPoints less than or equals to DEFAULT_NUMBER_OF_POINTS
        defaultVoteShouldNotBeFound("numberOfPoints.lessThan=" + DEFAULT_NUMBER_OF_POINTS);

        // Get all the voteList where numberOfPoints less than or equals to UPDATED_NUMBER_OF_POINTS
        defaultVoteShouldBeFound("numberOfPoints.lessThan=" + UPDATED_NUMBER_OF_POINTS);
    }


    @Test
    @Transactional
    public void getAllVotesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        vote.setUser(user);
        voteRepository.saveAndFlush(vote);
        Long userId = user.getId();

        // Get all the voteList where user equals to userId
        defaultVoteShouldBeFound("userId.equals=" + userId);

        // Get all the voteList where user equals to userId + 1
        defaultVoteShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllVotesByProposalIsEqualToSomething() throws Exception {
        // Initialize the database
        Proposal proposal = ProposalResourceIntTest.createEntity(em);
        em.persist(proposal);
        em.flush();
        vote.setProposal(proposal);
        voteRepository.saveAndFlush(vote);
        Long proposalId = proposal.getId();

        // Get all the voteList where proposal equals to proposalId
        defaultVoteShouldBeFound("proposalId.equals=" + proposalId);

        // Get all the voteList where proposal equals to proposalId + 1
        defaultVoteShouldNotBeFound("proposalId.equals=" + (proposalId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultVoteShouldBeFound(String filter) throws Exception {
        restVoteMockMvc.perform(get("/api/votes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].numberOfPoints").value(hasItem(DEFAULT_NUMBER_OF_POINTS)));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultVoteShouldNotBeFound(String filter) throws Exception {
        restVoteMockMvc.perform(get("/api/votes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingVote() throws Exception {
        // Get the vote
        restVoteMockMvc.perform(get("/api/votes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVote() throws Exception {
        // Initialize the database
        voteService.save(vote);

        int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Update the vote
        Vote updatedVote = voteRepository.findById(vote.getId()).get();
        // Disconnect from session so that the updates on updatedVote are not directly saved in db
        em.detach(updatedVote);
        updatedVote
            .creationDate(UPDATED_CREATION_DATE)
            .numberOfPoints(UPDATED_NUMBER_OF_POINTS);

        restVoteMockMvc.perform(put("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedVote)))
            .andExpect(status().isOk());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = voteList.get(voteList.size() - 1);
        assertThat(testVote.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testVote.getNumberOfPoints()).isEqualTo(UPDATED_NUMBER_OF_POINTS);
    }

    @Test
    @Transactional
    public void updateNonExistingVote() throws Exception {
        int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Create the Vote

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restVoteMockMvc.perform(put("/api/votes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(vote)))
            .andExpect(status().isBadRequest());

        // Validate the Vote in the database
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteVote() throws Exception {
        // Initialize the database
        voteService.save(vote);

        int databaseSizeBeforeDelete = voteRepository.findAll().size();

        // Get the vote
        restVoteMockMvc.perform(delete("/api/votes/{id}", vote.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Vote> voteList = voteRepository.findAll();
        assertThat(voteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vote.class);
        Vote vote1 = new Vote();
        vote1.setId(1L);
        Vote vote2 = new Vote();
        vote2.setId(vote1.getId());
        assertThat(vote1).isEqualTo(vote2);
        vote2.setId(2L);
        assertThat(vote1).isNotEqualTo(vote2);
        vote1.setId(null);
        assertThat(vote1).isNotEqualTo(vote2);
    }
}
