import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable } from 'rxjs';
import { UserRequest } from 'app/shared/model/user-request.model';
import { UserRequestService } from './user-request.service';
import { UserRequestComponent } from './user-request.component';
import { UserRequestDetailComponent } from './user-request-detail.component';
import { UserRequestUpdateComponent } from './user-request-update.component';
import { UserRequestDeletePopupComponent } from './user-request-delete-dialog.component';
import { IUserRequest } from 'app/shared/model/user-request.model';

@Injectable({ providedIn: 'root' })
export class UserRequestResolve implements Resolve<IUserRequest> {
    constructor(private service: UserRequestService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).map((userRequest: HttpResponse<UserRequest>) => userRequest.body);
        }
        return Observable.of(new UserRequest());
    }
}

export const userRequestRoute: Routes = [
    {
        path: 'user-request',
        component: UserRequestComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'UserRequests'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'user-request/:id/view',
        component: UserRequestDetailComponent,
        resolve: {
            userRequest: UserRequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'UserRequests'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'user-request/new',
        component: UserRequestUpdateComponent,
        resolve: {
            userRequest: UserRequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'UserRequests'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'user-request/:id/edit',
        component: UserRequestUpdateComponent,
        resolve: {
            userRequest: UserRequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'UserRequests'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const userRequestPopupRoute: Routes = [
    {
        path: 'user-request/:id/delete',
        component: UserRequestDeletePopupComponent,
        resolve: {
            userRequest: UserRequestResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'UserRequests'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
