import { NgModule } from '@angular/core';

import { BestChoiceSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [BestChoiceSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [BestChoiceSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class BestChoiceSharedCommonModule {}
