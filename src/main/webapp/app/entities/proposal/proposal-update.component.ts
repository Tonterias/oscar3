import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { IProposal } from 'app/shared/model/proposal.model';
import { ProposalService } from './proposal.service';
import { IUser, UserService } from 'app/core';
import { IPost } from 'app/shared/model/post.model';
import { PostService } from 'app/entities/post';

@Component({
    selector: 'jhi-proposal-update',
    templateUrl: './proposal-update.component.html'
})
export class ProposalUpdateComponent implements OnInit {
    private _proposal: IProposal;
    isSaving: boolean;

    users: IUser[];

    posts: IPost[];
    creationDate: string;
    releaseDate: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private proposalService: ProposalService,
        private userService: UserService,
        private postService: PostService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ proposal }) => {
            this.proposal = proposal;
        });
        this.userService.query().subscribe(
            (res: HttpResponse<IUser[]>) => {
                this.users = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.postService.query().subscribe(
            (res: HttpResponse<IPost[]>) => {
                this.posts = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.proposal.creationDate = moment(this.creationDate, DATE_TIME_FORMAT);
        this.proposal.releaseDate = moment(this.releaseDate, DATE_TIME_FORMAT);
        if (this.proposal.id !== undefined) {
            this.subscribeToSaveResponse(this.proposalService.update(this.proposal));
        } else {
            this.subscribeToSaveResponse(this.proposalService.create(this.proposal));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IProposal>>) {
        result.subscribe((res: HttpResponse<IProposal>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackPostById(index: number, item: IPost) {
        return item.id;
    }
    get proposal() {
        return this._proposal;
    }

    set proposal(proposal: IProposal) {
        this._proposal = proposal;
        this.creationDate = moment(proposal.creationDate).format(DATE_TIME_FORMAT);
        this.releaseDate = moment(proposal.releaseDate).format(DATE_TIME_FORMAT);
    }
}
