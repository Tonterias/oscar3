import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IPost } from 'app/shared/model//post.model';
import { IVote } from 'app/shared/model//vote.model';

export const enum ProposalType {
    UNDER_STUDY = 'UNDER_STUDY',
    APPROVED = 'APPROVED',
    DEVELOPMENT = 'DEVELOPMENT',
    PRODUCTION = 'PRODUCTION'
}

export interface IProposal {
    id?: number;
    creationDate?: Moment;
    releaseDate?: Moment;
    functionality?: string;
    proposalType?: ProposalType;
    user?: IUser;
    post?: IPost;
    votes?: IVote[];
}

export class Proposal implements IProposal {
    constructor(
        public id?: number,
        public creationDate?: Moment,
        public releaseDate?: Moment,
        public functionality?: string,
        public proposalType?: ProposalType,
        public user?: IUser,
        public post?: IPost,
        public votes?: IVote[]
    ) {}
}
