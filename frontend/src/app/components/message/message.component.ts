import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Message} from 'src/app/dtos/message';
import {Reaction, ReactionType} from 'src/app/dtos/reaction';
import {ReactionService} from 'src/app/services/reaction.service';
import {SubSink} from 'subsink';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit, OnDestroy {

  @Input() message: Message;
  @Input() canReact: boolean = true; // whether the component shows reaction buttons
  @Input() canDelete: boolean = true; // wether the component shows a delete button
  
  @Output() deleteMessage = new EventEmitter();
  
  author: string = 'Anonymous'; // in Version 3 the user name will be displayed
  reaction: Reaction;

  alreadyReacted = false;

  private subs = new SubSink();

  constructor(private reactionService: ReactionService) {
  }

  ngOnInit(): void {
    this.reaction = new Reaction(null, this.message.id, ReactionType.NEUTRAL);
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  public onUpVote(): void {
    switch (this.reaction.type) {
      case ReactionType.THUMBS_UP: {
        this.reaction.type = ReactionType.NEUTRAL;
        this.deleteReaction(this.reaction);
        break;
      }
      case ReactionType.THUMBS_DOWN: {
        this.change(this.reaction, ReactionType.THUMBS_UP);
        break;
      }
      case ReactionType.NEUTRAL: {
        this.reaction.type = ReactionType.THUMBS_UP;
        this.subs.add(this.reactionService.create(this.reaction).subscribe(result => this.reaction.id = result.id));
        break;
      }
    }
  }

  public onDownVote(): void {
    switch (this.reaction.type) {
      case ReactionType.THUMBS_UP: {
        this.change(this.reaction, ReactionType.THUMBS_DOWN);
        break;
      }
      case ReactionType.THUMBS_DOWN: {
        this.reaction.type = ReactionType.NEUTRAL;
        this.deleteReaction(this.reaction);
        break;
      }
      case ReactionType.NEUTRAL: {
        this.reaction.type = ReactionType.THUMBS_DOWN;
        this.subs.add(this.reactionService.create(this.reaction).subscribe(result => this.reaction.id = result.id));
        break;
      }
    }
  }

  public onDelete(): void {
    this.deleteMessage.emit(this.message);
  }

  public getUpvoteCount(): number {
    // if (this.reaction.type === ReactionType.THUMBS_UP) {
    //   return this.message.upVotes + 1;
    // } else {
    //   return this.message.upVotes;
    // }
    return this.message.upVotes;
  }

  public getDownVoteCount(): number {
    // if (this.reaction.type === ReactionType.THUMBS_DOWN) {
    //   return this.message.downVotes + 1;
    // } else {
    //   return this.message.downVotes;
    // }
    return this.message.downVotes;
  }

  private deleteReaction(reaction: Reaction): void {
    if (reaction.id != null) {
      this.subs.add(this.reactionService.deleteById(this.reaction.id).subscribe());
    }
  }

  private change(reaction: Reaction, newType: ReactionType): void {
    if (reaction.id != null) {
      this.reaction.type = newType;
      this.subs.add(this.reactionService.change(this.reaction).subscribe(result => this.reaction.id = result.id));
    }
  }
}
