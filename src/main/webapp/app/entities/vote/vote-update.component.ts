import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { IVote } from 'app/shared/model/vote.model';
import { VoteService } from './vote.service';
import { IUser, UserService } from 'app/core';
import { IProposal } from 'app/shared/model/proposal.model';
import { ProposalService } from 'app/entities/proposal';

@Component({
    selector: 'jhi-vote-update',
    templateUrl: './vote-update.component.html'
})
export class VoteUpdateComponent implements OnInit {
    private _vote: IVote;
    isSaving: boolean;

    users: IUser[];

    proposals: IProposal[];
    creationDate: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private voteService: VoteService,
        private userService: UserService,
        private proposalService: ProposalService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ vote }) => {
            this.vote = vote;
        });
        this.userService.query().subscribe(
            (res: HttpResponse<IUser[]>) => {
                this.users = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.proposalService.query().subscribe(
            (res: HttpResponse<IProposal[]>) => {
                this.proposals = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.vote.creationDate = moment(this.creationDate, DATE_TIME_FORMAT);
        if (this.vote.id !== undefined) {
            this.subscribeToSaveResponse(this.voteService.update(this.vote));
        } else {
            this.subscribeToSaveResponse(this.voteService.create(this.vote));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IVote>>) {
        result.subscribe((res: HttpResponse<IVote>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackUserById(index: number, item: IUser) {
        return item.id;
    }

    trackProposalById(index: number, item: IProposal) {
        return item.id;
    }
    get vote() {
        return this._vote;
    }

    set vote(vote: IVote) {
        this._vote = vote;
        this.creationDate = moment(vote.creationDate).format(DATE_TIME_FORMAT);
    }
}
