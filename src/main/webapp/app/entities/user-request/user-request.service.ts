import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IUserRequest } from 'app/shared/model/user-request.model';

type EntityResponseType = HttpResponse<IUserRequest>;
type EntityArrayResponseType = HttpResponse<IUserRequest[]>;

@Injectable({ providedIn: 'root' })
export class UserRequestService {
    private resourceUrl = SERVER_API_URL + 'api/user-requests';

    constructor(private http: HttpClient) {}

    create(userRequest: IUserRequest): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(userRequest);
        return this.http
            .post<IUserRequest>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    update(userRequest: IUserRequest): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(userRequest);
        return this.http
            .put<IUserRequest>(this.resourceUrl, copy, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http
            .get<IUserRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .map((res: EntityResponseType) => this.convertDateFromServer(res));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IUserRequest[]>(this.resourceUrl, { params: options, observe: 'response' })
            .map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    private convertDateFromClient(userRequest: IUserRequest): IUserRequest {
        const copy: IUserRequest = Object.assign({}, userRequest, {
            createdAt: userRequest.createdAt != null && userRequest.createdAt.isValid() ? userRequest.createdAt.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.createdAt = res.body.createdAt != null ? moment(res.body.createdAt) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((userRequest: IUserRequest) => {
            userRequest.createdAt = userRequest.createdAt != null ? moment(userRequest.createdAt) : null;
        });
        return res;
    }
}
