import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable } from 'rxjs';
import { Profile } from 'app/shared/model/profile.model';
import { ProfileService } from './profile.service';
import { ProfileComponent } from './profile.component';
import { ProfileDetailComponent } from './profile-detail.component';
import { ProfileUpdateComponent } from './profile-update.component';
import { ProfileDeletePopupComponent } from './profile-delete-dialog.component';
import { IProfile } from 'app/shared/model/profile.model';

@Injectable({ providedIn: 'root' })
export class ProfileResolve implements Resolve<IProfile> {
    constructor(private service: ProfileService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).map((profile: HttpResponse<Profile>) => profile.body);
        }
        return Observable.of(new Profile());
    }
}

export const profileRoute: Routes = [
    {
        path: 'profile',
        component: ProfileComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'Profiles'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'profile/:id/view',
        component: ProfileDetailComponent,
        resolve: {
            profile: ProfileResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Profiles'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'profile/new',
        component: ProfileUpdateComponent,
        resolve: {
            profile: ProfileResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Profiles'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'profile/:id/edit',
        component: ProfileUpdateComponent,
        resolve: {
            profile: ProfileResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Profiles'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const profilePopupRoute: Routes = [
    {
        path: 'profile/:id/delete',
        component: ProfileDeletePopupComponent,
        resolve: {
            profile: ProfileResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Profiles'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
