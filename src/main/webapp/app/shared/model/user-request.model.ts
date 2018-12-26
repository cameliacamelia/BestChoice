import { Moment } from 'moment';

export interface IUserRequest {
    id?: string;
    request?: string;
    userId?: string;
    createdAt?: Moment;
}

export class UserRequest implements IUserRequest {
    constructor(public id?: string, public request?: string, public userId?: string, public createdAt?: Moment) {}
}
