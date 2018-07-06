package com.oscar3.web.config;

import java.time.Duration;

import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;

import io.github.jhipster.config.jcache.BeanClassLoaderAwareJCacheRegionFactory;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        BeanClassLoaderAwareJCacheRegionFactory.setBeanClassLoader(this.getClass().getClassLoader());
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(com.oscar3.web.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            cm.createCache(com.oscar3.web.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.User.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Authority.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Profile.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Post.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Post.class.getName() + ".comments", jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Post.class.getName() + ".tags", jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Post.class.getName() + ".topics", jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Comment.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Message.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Proposal.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Proposal.class.getName() + ".votes", jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Vote.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Topic.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Topic.class.getName() + ".posts", jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Tag.class.getName(), jcacheConfiguration);
            cm.createCache(com.oscar3.web.domain.Tag.class.getName() + ".posts", jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
        };
    }
}
