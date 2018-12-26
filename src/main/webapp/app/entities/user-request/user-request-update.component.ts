import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IUserRequest } from 'app/shared/model/user-request.model';
import { UserRequestService } from './user-request.service';

@Component({
    selector: 'jhi-user-request-update',
    templateUrl: './user-request-update.component.html'
})
export class UserRequestUpdateComponent implements OnInit {
    private _userRequest: IUserRequest;
    isSaving: boolean;
    createdAt: string;

    constructor(private userRequestService: UserRequestService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ userRequest }) => {
            this.userRequest = userRequest;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.userRequest.createdAt = moment(this.createdAt, DATE_TIME_FORMAT);
        if (this.userRequest.id !== undefined) {
            this.subscribeToSaveResponse(this.userRequestService.update(this.userRequest));
        } else {
            this.subscribeToSaveResponse(this.userRequestService.create(this.userRequest));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IUserRequest>>) {
        result.subscribe((res: HttpResponse<IUserRequest>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    get userRequest() {
        return this._userRequest;
    }

    set userRequest(userRequest: IUserRequest) {
        this._userRequest = userRequest;
        this.createdAt = moment(userRequest.createdAt).format(DATE_TIME_FORMAT);
    }
}
