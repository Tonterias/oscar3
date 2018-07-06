import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { Oscar3ProfileModule } from './profile/profile.module';
import { Oscar3PostModule } from './post/post.module';
import { Oscar3CommentModule } from './comment/comment.module';
import { Oscar3MessageModule } from './message/message.module';
import { Oscar3ProposalModule } from './proposal/proposal.module';
import { Oscar3VoteModule } from './vote/vote.module';
import { Oscar3TopicModule } from './topic/topic.module';
import { Oscar3TagModule } from './tag/tag.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        Oscar3ProfileModule,
        Oscar3PostModule,
        Oscar3CommentModule,
        Oscar3MessageModule,
        Oscar3ProposalModule,
        Oscar3VoteModule,
        Oscar3TopicModule,
        Oscar3TagModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Oscar3EntityModule {}
