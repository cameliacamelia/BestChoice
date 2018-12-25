import { Moment } from 'moment';

export interface IQuestion {
    id?: string;
    question?: string;
    categoryId?: number;
    createdAt?: Moment;
}

export class Question implements IQuestion {
    constructor(public id?: string, public question?: string, public categoryId?: number, public createdAt?: Moment) {}
}
