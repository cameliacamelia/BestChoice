import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IQuestion } from 'app/shared/model/question.model';

type EntityResponseType = HttpResponse<IQuestion>;
type EntityArrayResponseType = HttpResponse<IQuestion[]>;

@Injectable({ providedIn: 'root' })
export class QuestionService {
    private resourceUrl = SERVER_API_URL + 'api/questions';

    constructor(private http: HttpClient) {}

    create(question: IQuestion): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(question);
        return this.http
            .post<IQuestion>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    update(question: IQuestion): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(question);
        return this.http
            .put<IQuestion>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http
            .get<IQuestion>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IQuestion[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    private convertDateFromClient(question: IQuestion): IQuestion {
        const copy: IQuestion = Object.assign({}, question, {
            createdAt: question.createdAt != null && question.createdAt.isValid() ? question.createdAt.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.createdAt = res.body.createdAt != null ? moment(res.body.createdAt) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((question: IQuestion) => {
            question.createdAt = question.createdAt != null ? moment(question.createdAt) : null;
        });
        return res;
    }
}
