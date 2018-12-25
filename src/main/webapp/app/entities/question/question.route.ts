import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable } from 'rxjs';
import { Question } from 'app/shared/model/question.model';
import { QuestionService } from './question.service';
import { QuestionComponent } from './question.component';
import { QuestionDetailComponent } from './question-detail.component';
import { QuestionUpdateComponent } from './question-update.component';
import { QuestionDeletePopupComponent } from './question-delete-dialog.component';
import { IQuestion } from 'app/shared/model/question.model';

@Injectable({ providedIn: 'root' })
export class QuestionResolve implements Resolve<IQuestion> {
    constructor(private service: QuestionService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).map((question: HttpResponse<Question>) => question.body);
        }
        return Observable.of(new Question());
    }
}

export const questionRoute: Routes = [
    {
        path: 'question',
        component: QuestionComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Questions'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'question/:id/view',
        component: QuestionDetailComponent,
        resolve: {
            question: QuestionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Questions'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'question/new',
        component: QuestionUpdateComponent,
        resolve: {
            question: QuestionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Questions'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'question/:id/edit',
        component: QuestionUpdateComponent,
        resolve: {
            question: QuestionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Questions'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const questionPopupRoute: Routes = [
    {
        path: 'question/:id/delete',
        component: QuestionDeletePopupComponent,
        resolve: {
            question: QuestionResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Questions'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];