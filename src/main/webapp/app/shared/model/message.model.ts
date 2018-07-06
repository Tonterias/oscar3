import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';

export interface IMessage {
    id?: number;
    creationDate?: Moment;
    messageText?: string;
    isDeliverd?: boolean;
    user?: IUser;
}

export class Message implements IMessage {
    constructor(
        public id?: number,
        public creationDate?: Moment,
        public messageText?: string,
        public isDeliverd?: boolean,
        public user?: IUser
    ) {
        this.isDeliverd = false;
    }
}
