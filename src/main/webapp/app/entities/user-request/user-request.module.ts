import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BestChoiceSharedModule } from 'app/shared';
import {
    UserRequestComponent,
    UserRequestDetailComponent,
    UserRequestUpdateComponent,
    UserRequestDeletePopupComponent,
    UserRequestDeleteDialogComponent,
    userRequestRoute,
    userRequestPopupRoute
} from './';

const ENTITY_STATES = [...userRequestRoute, ...userRequestPopupRoute];

@NgModule({
    imports: [BestChoiceSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        UserRequestComponent,
        UserRequestDetailComponent,
        UserRequestUpdateComponent,
        UserRequestDeleteDialogComponent,
        UserRequestDeletePopupComponent
    ],
    entryComponents: [UserRequestComponent, UserRequestUpdateComponent, UserRequestDeleteDialogComponent, UserRequestDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BestChoiceUserRequestModule {}
