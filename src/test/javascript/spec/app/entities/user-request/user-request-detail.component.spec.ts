/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BestChoiceTestModule } from '../../../test.module';
import { UserRequestDetailComponent } from 'app/entities/user-request/user-request-detail.component';
import { UserRequest } from 'app/shared/model/user-request.model';

describe('Component Tests', () => {
    describe('UserRequest Management Detail Component', () => {
        let comp: UserRequestDetailComponent;
        let fixture: ComponentFixture<UserRequestDetailComponent>;
        const route = ({ data: of({ userRequest: new UserRequest('123') }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [BestChoiceTestModule],
                declarations: [UserRequestDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(UserRequestDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(UserRequestDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.userRequest).toEqual(jasmine.objectContaining({ id: '123' }));
            });
        });
    });
});
