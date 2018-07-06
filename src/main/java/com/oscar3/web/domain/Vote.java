package com.oscar3.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Vote.
 */
@Entity
@Table(name = "vote")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "number_of_points")
    private Integer numberOfPoints;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("")
    private User user;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("votes")
    private Proposal proposal;

//  @Formula("(SELECT SUM(VOTE.NUMBER_OF_POINTS) FROM VOTE WHERE VOTE.USER_ID = USER_ID)")
    @Formula("(SELECT SUM(VOTE.NUMBER_OF_POINTS) FROM VOTE WHERE VOTE.PROPOSAL_ID = PROPOSAL_ID)")
    private int totalProposalVotes;

    @Formula("(SELECT PROFILE.USER_POINTS FROM PROFILE WHERE PROFILE.USER_ID = USER_ID)")
    private Long userVotes;

  // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Vote creationDate(Instant creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }

    public Vote numberOfPoints(Integer numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
        return this;
    }

    public void setNumberOfPoints(Integer numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public User getUser() {
        return user;
    }

    public Vote user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public Vote proposal(Proposal proposal) {
        this.proposal = proposal;
        return this;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public int getTotalProposalVotes() {
		return totalProposalVotes;
	}

	public void setTotalProposalVotes(int totalProposalVotes) {
		this.totalProposalVotes = totalProposalVotes;
	}

	public Long getUserVotes() {
		return userVotes;
	}

	public void setUserVotes(Long userVotes) {
		this.userVotes = userVotes;
	}
    
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vote vote = (Vote) o;
        if (vote.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vote.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Vote{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            ", numberOfPoints=" + getNumberOfPoints() +
            "}";
    }
}
