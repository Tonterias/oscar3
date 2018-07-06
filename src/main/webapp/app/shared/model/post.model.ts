import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IComment } from 'app/shared/model//comment.model';
import { ITag } from 'app/shared/model//tag.model';
import { ITopic } from 'app/shared/model//topic.model';

export interface IPost {
    id?: number;
    creationDate?: Moment;
    publicationDate?: Moment;
    headline?: string;
    bodytext?: string;
    imageContentType?: string;
    image?: any;
    image2ContentType?: string;
    image2?: any;
    user?: IUser;
    comments?: IComment[];
    tags?: ITag[];
    topics?: ITopic[];
}

export class Post implements IPost {
    constructor(
        public id?: number,
        public creationDate?: Moment,
        public publicationDate?: Moment,
        public headline?: string,
        public bodytext?: string,
        public imageContentType?: string,
        public image?: any,
        public image2ContentType?: string,
        public image2?: any,
        public user?: IUser,
        public comments?: IComment[],
        public tags?: ITag[],
        public topics?: ITopic[]
    ) {}
}
