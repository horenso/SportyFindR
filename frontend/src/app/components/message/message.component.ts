import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Message } from 'src/app/dtos/message';
import { Reaction, ReactionType } from 'src/app/dtos/reaction';
import { faTrash, faArrowUp, faArrowDown, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { ReactionService } from 'src/app/services/reaction.service';
import { ThisReceiver } from '@angular/compiler';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  author: string = 'Anonymous'; // in Version 3 the user name will be displayed
  @Input() message: Message;
  @Input() canReact: boolean = true; // whether the component shows reaction buttons
  @Input() canDelete: boolean = true; // wether the component shows a delete button 

  @Output() deleteMessage = new EventEmitter();

  reaction: Reaction;

  alreadyReacted = false;

  deleteSymbol: IconDefinition = faTrash;
  upVoteSymbol: IconDefinition = faArrowUp;
  downVoteSymbol: IconDefinition = faArrowDown

  constructor(private reactionService: ReactionService) { }

  ngOnInit(): void {
    this.reaction = new Reaction(null, this.message.id, ReactionType.NEUTRAL);
  }

  public getDateString(): string {
    return 'hi';
  }

  public onUpVote(): void {
    switch(this.reaction.type) {
      case ReactionType.THUMBS_UP: {
        this.reaction.type = ReactionType.NEUTRAL;
        this.deleteReation(this.reaction);
        break;
      }
      case ReactionType.THUMBS_DOWN: {
        this.changeReation(this.reaction, ReactionType.THUMBS_UP);
        break;
      }
      case ReactionType.NEUTRAL: {
        this.reaction.type = ReactionType.THUMBS_UP;
        this.reactionService.createReaction(this.reaction).subscribe(result => this.reaction.id = result.id);
        break;
      }
    }
  }

  public onDownVote(): void {
    switch(this.reaction.type) {
      case ReactionType.THUMBS_UP: {
        this.changeReation(this.reaction, ReactionType.THUMBS_DOWN);
        break;
      }
      case ReactionType.THUMBS_DOWN: {
        this.reaction.type = ReactionType.NEUTRAL;
        this.deleteReation(this.reaction)
        break;
      }
      case ReactionType.NEUTRAL: {
        this.reaction.type = ReactionType.THUMBS_DOWN;
        this.reactionService.createReaction(this.reaction).subscribe(result => this.reaction.id = result.id);
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

  private deleteReation(reaction: Reaction): void {
    if (reaction.id != null) {
      this.reactionService.deleteById(this.reaction.id).subscribe();
    } 
  }

  private changeReation(reaction: Reaction, newType: ReactionType): void {
    if (reaction.id != null) {
      this.reaction.type = newType;
      this.reactionService.changeReaction(this.reaction).subscribe(result => this.reaction.id = result.id);
    }
  }

  private createReation(reactionType: ReactionType): void {
    this.reaction.type = reactionType;
    this.reactionService.createReaction(this.reaction).subscribe(result => this.reaction.id = result.id);
  }
}
