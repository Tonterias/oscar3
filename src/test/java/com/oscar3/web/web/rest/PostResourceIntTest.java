package com.oscar3.web.web.rest;

import com.oscar3.web.Oscar3App;

import com.oscar3.web.domain.Post;
import com.oscar3.web.domain.User;
import com.oscar3.web.domain.Comment;
import com.oscar3.web.domain.Tag;
import com.oscar3.web.domain.Topic;
import com.oscar3.web.repository.PostRepository;
import com.oscar3.web.service.PostService;
import com.oscar3.web.web.rest.errors.ExceptionTranslator;
import com.oscar3.web.service.dto.PostCriteria;
import com.oscar3.web.service.PostQueryService;

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
import org.springframework.util.Base64Utils;

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
 * Test class for the PostResource REST controller.
 *
 * @see PostResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Oscar3App.class)
public class PostResourceIntTest {

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_PUBLICATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLICATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_HEADLINE = "AAAAAAAAAA";
    private static final String UPDATED_HEADLINE = "BBBBBBBBBB";

    private static final String DEFAULT_BODYTEXT = "AAAAAAAAAA";
    private static final String UPDATED_BODYTEXT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_IMAGE_2 = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE_2 = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_2_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_2_CONTENT_TYPE = "image/png";

    @Autowired
    private PostRepository postRepository;

    

    @Autowired
    private PostService postService;

    @Autowired
    private PostQueryService postQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPostMockMvc;

    private Post post;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PostResource postResource = new PostResource(postService, postQueryService);
        this.restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource)
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
    public static Post createEntity(EntityManager em) {
        Post post = new Post()
            .creationDate(DEFAULT_CREATION_DATE)
            .publicationDate(DEFAULT_PUBLICATION_DATE)
            .headline(DEFAULT_HEADLINE)
            .bodytext(DEFAULT_BODYTEXT)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .image2(DEFAULT_IMAGE_2)
            .image2ContentType(DEFAULT_IMAGE_2_CONTENT_TYPE);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        post.setUser(user);
        return post;
    }

    @Before
    public void initTest() {
        post = createEntity(em);
    }

    @Test
    @Transactional
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testPost.getPublicationDate()).isEqualTo(DEFAULT_PUBLICATION_DATE);
        assertThat(testPost.getHeadline()).isEqualTo(DEFAULT_HEADLINE);
        assertThat(testPost.getBodytext()).isEqualTo(DEFAULT_BODYTEXT);
        assertThat(testPost.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testPost.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testPost.getImage2()).isEqualTo(DEFAULT_IMAGE_2);
        assertThat(testPost.getImage2ContentType()).isEqualTo(DEFAULT_IMAGE_2_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post with an existing ID
        post.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setCreationDate(null);

        // Create the Post, which fails.

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHeadlineIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setHeadline(null);

        // Create the Post, which fails.

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBodytextIsRequired() throws Exception {
        int databaseSizeBeforeTest = postRepository.findAll().size();
        // set the field null
        post.setBodytext(null);

        // Create the Post, which fails.

        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList
        restPostMockMvc.perform(get("/api/posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].publicationDate").value(hasItem(DEFAULT_PUBLICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].headline").value(hasItem(DEFAULT_HEADLINE.toString())))
            .andExpect(jsonPath("$.[*].bodytext").value(hasItem(DEFAULT_BODYTEXT.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].image2ContentType").value(hasItem(DEFAULT_IMAGE_2_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image2").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_2))));
    }
    

    @Test
    @Transactional
    public void getPost() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.publicationDate").value(DEFAULT_PUBLICATION_DATE.toString()))
            .andExpect(jsonPath("$.headline").value(DEFAULT_HEADLINE.toString()))
            .andExpect(jsonPath("$.bodytext").value(DEFAULT_BODYTEXT.toString()))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.image2ContentType").value(DEFAULT_IMAGE_2_CONTENT_TYPE))
            .andExpect(jsonPath("$.image2").value(Base64Utils.encodeToString(DEFAULT_IMAGE_2)));
    }

    @Test
    @Transactional
    public void getAllPostsByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where creationDate equals to DEFAULT_CREATION_DATE
        defaultPostShouldBeFound("creationDate.equals=" + DEFAULT_CREATION_DATE);

        // Get all the postList where creationDate equals to UPDATED_CREATION_DATE
        defaultPostShouldNotBeFound("creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllPostsByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where creationDate in DEFAULT_CREATION_DATE or UPDATED_CREATION_DATE
        defaultPostShouldBeFound("creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE);

        // Get all the postList where creationDate equals to UPDATED_CREATION_DATE
        defaultPostShouldNotBeFound("creationDate.in=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void getAllPostsByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where creationDate is not null
        defaultPostShouldBeFound("creationDate.specified=true");

        // Get all the postList where creationDate is null
        defaultPostShouldNotBeFound("creationDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByPublicationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publicationDate equals to DEFAULT_PUBLICATION_DATE
        defaultPostShouldBeFound("publicationDate.equals=" + DEFAULT_PUBLICATION_DATE);

        // Get all the postList where publicationDate equals to UPDATED_PUBLICATION_DATE
        defaultPostShouldNotBeFound("publicationDate.equals=" + UPDATED_PUBLICATION_DATE);
    }

    @Test
    @Transactional
    public void getAllPostsByPublicationDateIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publicationDate in DEFAULT_PUBLICATION_DATE or UPDATED_PUBLICATION_DATE
        defaultPostShouldBeFound("publicationDate.in=" + DEFAULT_PUBLICATION_DATE + "," + UPDATED_PUBLICATION_DATE);

        // Get all the postList where publicationDate equals to UPDATED_PUBLICATION_DATE
        defaultPostShouldNotBeFound("publicationDate.in=" + UPDATED_PUBLICATION_DATE);
    }

    @Test
    @Transactional
    public void getAllPostsByPublicationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where publicationDate is not null
        defaultPostShouldBeFound("publicationDate.specified=true");

        // Get all the postList where publicationDate is null
        defaultPostShouldNotBeFound("publicationDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByHeadlineIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where headline equals to DEFAULT_HEADLINE
        defaultPostShouldBeFound("headline.equals=" + DEFAULT_HEADLINE);

        // Get all the postList where headline equals to UPDATED_HEADLINE
        defaultPostShouldNotBeFound("headline.equals=" + UPDATED_HEADLINE);
    }

    @Test
    @Transactional
    public void getAllPostsByHeadlineIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where headline in DEFAULT_HEADLINE or UPDATED_HEADLINE
        defaultPostShouldBeFound("headline.in=" + DEFAULT_HEADLINE + "," + UPDATED_HEADLINE);

        // Get all the postList where headline equals to UPDATED_HEADLINE
        defaultPostShouldNotBeFound("headline.in=" + UPDATED_HEADLINE);
    }

    @Test
    @Transactional
    public void getAllPostsByHeadlineIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where headline is not null
        defaultPostShouldBeFound("headline.specified=true");

        // Get all the postList where headline is null
        defaultPostShouldNotBeFound("headline.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByBodytextIsEqualToSomething() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where bodytext equals to DEFAULT_BODYTEXT
        defaultPostShouldBeFound("bodytext.equals=" + DEFAULT_BODYTEXT);

        // Get all the postList where bodytext equals to UPDATED_BODYTEXT
        defaultPostShouldNotBeFound("bodytext.equals=" + UPDATED_BODYTEXT);
    }

    @Test
    @Transactional
    public void getAllPostsByBodytextIsInShouldWork() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where bodytext in DEFAULT_BODYTEXT or UPDATED_BODYTEXT
        defaultPostShouldBeFound("bodytext.in=" + DEFAULT_BODYTEXT + "," + UPDATED_BODYTEXT);

        // Get all the postList where bodytext equals to UPDATED_BODYTEXT
        defaultPostShouldNotBeFound("bodytext.in=" + UPDATED_BODYTEXT);
    }

    @Test
    @Transactional
    public void getAllPostsByBodytextIsNullOrNotNull() throws Exception {
        // Initialize the database
        postRepository.saveAndFlush(post);

        // Get all the postList where bodytext is not null
        defaultPostShouldBeFound("bodytext.specified=true");

        // Get all the postList where bodytext is null
        defaultPostShouldNotBeFound("bodytext.specified=false");
    }

    @Test
    @Transactional
    public void getAllPostsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        post.setUser(user);
        postRepository.saveAndFlush(post);
        Long userId = user.getId();

        // Get all the postList where user equals to userId
        defaultPostShouldBeFound("userId.equals=" + userId);

        // Get all the postList where user equals to userId + 1
        defaultPostShouldNotBeFound("userId.equals=" + (userId + 1));
    }


    @Test
    @Transactional
    public void getAllPostsByCommentIsEqualToSomething() throws Exception {
        // Initialize the database
        Comment comment = CommentResourceIntTest.createEntity(em);
        em.persist(comment);
        em.flush();
        post.addComment(comment);
        postRepository.saveAndFlush(post);
        Long commentId = comment.getId();

        // Get all the postList where comment equals to commentId
        defaultPostShouldBeFound("commentId.equals=" + commentId);

        // Get all the postList where comment equals to commentId + 1
        defaultPostShouldNotBeFound("commentId.equals=" + (commentId + 1));
    }


    @Test
    @Transactional
    public void getAllPostsByTagIsEqualToSomething() throws Exception {
        // Initialize the database
        Tag tag = TagResourceIntTest.createEntity(em);
        em.persist(tag);
        em.flush();
        post.addTag(tag);
        postRepository.saveAndFlush(post);
        Long tagId = tag.getId();

        // Get all the postList where tag equals to tagId
        defaultPostShouldBeFound("tagId.equals=" + tagId);

        // Get all the postList where tag equals to tagId + 1
        defaultPostShouldNotBeFound("tagId.equals=" + (tagId + 1));
    }


    @Test
    @Transactional
    public void getAllPostsByTopicIsEqualToSomething() throws Exception {
        // Initialize the database
        Topic topic = TopicResourceIntTest.createEntity(em);
        em.persist(topic);
        em.flush();
        post.addTopic(topic);
        postRepository.saveAndFlush(post);
        Long topicId = topic.getId();

        // Get all the postList where topic equals to topicId
        defaultPostShouldBeFound("topicId.equals=" + topicId);

        // Get all the postList where topic equals to topicId + 1
        defaultPostShouldNotBeFound("topicId.equals=" + (topicId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPostShouldBeFound(String filter) throws Exception {
        restPostMockMvc.perform(get("/api/posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].publicationDate").value(hasItem(DEFAULT_PUBLICATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].headline").value(hasItem(DEFAULT_HEADLINE.toString())))
            .andExpect(jsonPath("$.[*].bodytext").value(hasItem(DEFAULT_BODYTEXT.toString())))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].image2ContentType").value(hasItem(DEFAULT_IMAGE_2_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image2").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE_2))));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPostShouldNotBeFound(String filter) throws Exception {
        restPostMockMvc.perform(get("/api/posts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePost() throws Exception {
        // Initialize the database
        postService.save(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).get();
        // Disconnect from session so that the updates on updatedPost are not directly saved in db
        em.detach(updatedPost);
        updatedPost
            .creationDate(UPDATED_CREATION_DATE)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .headline(UPDATED_HEADLINE)
            .bodytext(UPDATED_BODYTEXT)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .image2(UPDATED_IMAGE_2)
            .image2ContentType(UPDATED_IMAGE_2_CONTENT_TYPE);

        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testPost.getPublicationDate()).isEqualTo(UPDATED_PUBLICATION_DATE);
        assertThat(testPost.getHeadline()).isEqualTo(UPDATED_HEADLINE);
        assertThat(testPost.getBodytext()).isEqualTo(UPDATED_BODYTEXT);
        assertThat(testPost.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testPost.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testPost.getImage2()).isEqualTo(UPDATED_IMAGE_2);
        assertThat(testPost.getImage2ContentType()).isEqualTo(UPDATED_IMAGE_2_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Create the Post

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePost() throws Exception {
        // Initialize the database
        postService.save(post);

        int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Get the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Post.class);
        Post post1 = new Post();
        post1.setId(1L);
        Post post2 = new Post();
        post2.setId(post1.getId());
        assertThat(post1).isEqualTo(post2);
        post2.setId(2L);
        assertThat(post1).isNotEqualTo(post2);
        post1.setId(null);
        assertThat(post1).isNotEqualTo(post2);
    }
}
