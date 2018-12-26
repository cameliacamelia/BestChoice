import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserRequest } from 'app/shared/model/user-request.model';

@Component({
    selector: 'jhi-user-request-detail',
    templateUrl: './user-request-detail.component.html'
})
export class UserRequestDetailComponent implements OnInit {
    userRequest: IUserRequest;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ userRequest }) => {
            this.userRequest = userRequest;
        });
    }

    previousState() {
        window.history.back();
    }
}
