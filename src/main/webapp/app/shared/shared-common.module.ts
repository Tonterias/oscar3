import { NgModule } from '@angular/core';

import { Oscar3SharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [Oscar3SharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [Oscar3SharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class Oscar3SharedCommonModule {}
