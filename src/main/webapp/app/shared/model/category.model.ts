import { Moment } from 'moment';

export interface ICategory {
    id?: string;
    name?: string;
    createdAt?: Moment;
}

export class Category implements ICategory {
    constructor(public id?: string, public name?: string, public createdAt?: Moment) {}
}
