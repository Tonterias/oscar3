import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Oscar3SharedModule } from 'app/shared';
import { Oscar3AdminModule } from 'app/admin/admin.module';
import {
    ProposalComponent,
    ProposalDetailComponent,
    ProposalUpdateComponent,
    ProposalDeletePopupComponent,
    ProposalDeleteDialogComponent,
    proposalRoute,
    proposalPopupRoute
} from './';

const ENTITY_STATES = [...proposalRoute, ...proposalPopupRoute];

@NgModule({
    imports: [Oscar3SharedModule, Oscar3AdminModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        ProposalComponent,
        ProposalDetailComponent,
        ProposalUpdateComponent,
        ProposalDeleteDialogComponent,
        ProposalDeletePopupComponent
    ],
    entryComponents: [ProposalComponent, ProposalUpdateComponent, ProposalDeleteDialogComponent, ProposalDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Oscar3ProposalModule {}
