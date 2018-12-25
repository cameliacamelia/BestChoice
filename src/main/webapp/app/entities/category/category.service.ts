import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICategory } from 'app/shared/model/category.model';

type EntityResponseType = HttpResponse<ICategory>;
type EntityArrayResponseType = HttpResponse<ICategory[]>;

@Injectable({ providedIn: 'root' })
export class CategoryService {
    private resourceUrl = SERVER_API_URL + 'api/categories';

    constructor(private http: HttpClient) {}

    create(category: ICategory): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(category);
        return this.http
            .post<ICategory>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    update(category: ICategory): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(category);
        return this.http
            .put<ICategory>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http
            .get<ICategory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<ICategory[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    private convertDateFromClient(category: ICategory): ICategory {
        const copy: ICategory = Object.assign({}, category, {
            createdAt: category.createdAt != null && category.createdAt.isValid() ? category.createdAt.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.createdAt = res.body.createdAt != null ? moment(res.body.createdAt) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((category: ICategory) => {
            category.createdAt = category.createdAt != null ? moment(category.createdAt) : null;
        });
        return res;
    }
}
