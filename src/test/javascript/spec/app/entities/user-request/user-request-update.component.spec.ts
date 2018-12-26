/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { BestChoiceTestModule } from '../../../test.module';
import { UserRequestUpdateComponent } from 'app/entities/user-request/user-request-update.component';
import { UserRequestService } from 'app/entities/user-request/user-request.service';
import { UserRequest } from 'app/shared/model/user-request.model';

describe('Component Tests', () => {
    describe('UserRequest Management Update Component', () => {
        let comp: UserRequestUpdateComponent;
        let fixture: ComponentFixture<UserRequestUpdateComponent>;
        let service: UserRequestService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [BestChoiceTestModule],
                declarations: [UserRequestUpdateComponent]
            })
                .overrideTemplate(UserRequestUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(UserRequestUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(UserRequestService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new UserRequest('123');
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.userRequest = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new UserRequest();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.userRequest = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
