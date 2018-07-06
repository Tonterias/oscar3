import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface IProfile {
    id?: number;
    creationDate?: Moment;
    imageContentType?: string;
    image?: any;
    userPoints?: number;
    user?: IUser;
}

export class Profile implements IProfile {
    constructor(
        public id?: number,
        public creationDate?: Moment,
        public imageContentType?: string,
        public image?: any,
        public userPoints?: number,
        public user?: IUser
    ) {}
}
