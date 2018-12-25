import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { BestChoiceCategoryModule } from './category/category.module';
import { BestChoiceQuestionModule } from './question/question.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        BestChoiceCategoryModule,
        BestChoiceQuestionModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BestChoiceEntityModule {}
