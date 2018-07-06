import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Oscar3SharedModule } from 'app/shared';
import { Oscar3AdminModule } from 'app/admin/admin.module';
import {
    VoteComponent,
    VoteDetailComponent,
    VoteUpdateComponent,
    VoteDeletePopupComponent,
    VoteDeleteDialogComponent,
    voteRoute,
    votePopupRoute
} from './';

const ENTITY_STATES = [...voteRoute, ...votePopupRoute];

@NgModule({
    imports: [Oscar3SharedModule, Oscar3AdminModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [VoteComponent, VoteDetailComponent, VoteUpdateComponent, VoteDeleteDialogComponent, VoteDeletePopupComponent],
    entryComponents: [VoteComponent, VoteUpdateComponent, VoteDeleteDialogComponent, VoteDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Oscar3VoteModule {}
