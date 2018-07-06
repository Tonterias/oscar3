package com.oscar3.web.service.dto;

import java.io.Serializable;
import com.oscar3.web.domain.enumeration.ProposalType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import io.github.jhipster.service.filter.InstantFilter;




/**
 * Criteria class for the Proposal entity. This class is used in ProposalResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /proposals?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProposalCriteria implements Serializable {
    /**
     * Class for filtering ProposalType
     */
    public static class ProposalTypeFilter extends Filter<ProposalType> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private InstantFilter creationDate;

    private InstantFilter releaseDate;

    private StringFilter functionality;

    private ProposalTypeFilter proposalType;

    private LongFilter userId;

    private LongFilter postId;

    private LongFilter voteId;

    public ProposalCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(InstantFilter creationDate) {
        this.creationDate = creationDate;
    }

    public InstantFilter getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(InstantFilter releaseDate) {
        this.releaseDate = releaseDate;
    }

    public StringFilter getFunctionality() {
        return functionality;
    }

    public void setFunctionality(StringFilter functionality) {
        this.functionality = functionality;
    }

    public ProposalTypeFilter getProposalType() {
        return proposalType;
    }

    public void setProposalType(ProposalTypeFilter proposalType) {
        this.proposalType = proposalType;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPostId() {
        return postId;
    }

    public void setPostId(LongFilter postId) {
        this.postId = postId;
    }

    public LongFilter getVoteId() {
        return voteId;
    }

    public void setVoteId(LongFilter voteId) {
        this.voteId = voteId;
    }

    @Override
    public String toString() {
        return "ProposalCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (creationDate != null ? "creationDate=" + creationDate + ", " : "") +
                (releaseDate != null ? "releaseDate=" + releaseDate + ", " : "") +
                (functionality != null ? "functionality=" + functionality + ", " : "") +
                (proposalType != null ? "proposalType=" + proposalType + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (postId != null ? "postId=" + postId + ", " : "") +
                (voteId != null ? "voteId=" + voteId + ", " : "") +
            "}";
    }

}
