import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IPost } from 'app/shared/model//post.model';

export interface IComment {
    id?: number;
    creationDate?: Moment;
    commentText?: string;
    isOffensive?: boolean;
    user?: IUser;
    post?: IPost;
}

export class Comment implements IComment {
    constructor(
        public id?: number,
        public creationDate?: Moment,
        public commentText?: string,
        public isOffensive?: boolean,
        public user?: IUser,
        public post?: IPost
    ) {
        this.isOffensive = false;
    }
}
