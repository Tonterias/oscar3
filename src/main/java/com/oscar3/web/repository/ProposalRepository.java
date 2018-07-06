package com.oscar3.web.repository;

import com.oscar3.web.domain.Proposal;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Proposal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long>, JpaSpecificationExecutor<Proposal> {

    @Query("select proposal from Proposal proposal where proposal.user.login = ?#{principal.username}")
    List<Proposal> findByUserIsCurrentUser();

}
