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
  
  author: string = ''; // in Version 3 the user name will be displayed
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

  public getUpvoteCountString(): string {
    if (this.message?.upVotes > 0) {
      return this.message.upVotes.toString();
    } else {
      return '';
    }
  }

  public getDownVoteCountString(): string {
    if (this.message?.downVotes > 0) {
      return this.message.downVotes.toString();
    } else {
      return '';
    }
  }

  public getUpVoteButtonClass(): string[] {
    if (this.reaction.type === ReactionType.THUMBS_UP) {
      return ['upVoteButton', 'buttonEnabled'];
    } else {
      return ['upVoteButton', 'buttonDisabled'];
    }
  }

  public getDownVoteButtonClass(): string[] {
    if (this.reaction.type === ReactionType.THUMBS_DOWN) {
      return ['downVoteButton', 'buttonEnabled'];
    } else {
      return ['downVoteButton', 'buttonDisabled'];
    }
  }

  private deleteReaction(reaction: Reaction): void {
    if (reaction.id != null) {
      this.subs.add(this.reactionService.deleteById(this.reaction.id).subscribe());
    }
  }

  private change(reaction: Reaction, newType: ReactionType): void {
    console.log(this.message);
    if (reaction.id != null) {
      this.reaction.type = newType;
      this.subs.add(this.reactionService.change(this.reaction).subscribe(result => this.reaction.id = result.id));
    }
  }
}
