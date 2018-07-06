import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IProposal } from 'app/shared/model//proposal.model';

export interface IVote {
    id?: number;
    creationDate?: Moment;
    numberOfPoints?: number;
    user?: IUser;
    proposal?: IProposal;
}

export class Vote implements IVote {
    constructor(
        public id?: number,
        public creationDate?: Moment,
        public numberOfPoints?: number,
        public user?: IUser,
        public proposal?: IProposal
    ) {}
}
