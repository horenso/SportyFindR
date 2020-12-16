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
        this.reactionService.deleteById(this.reaction.id).subscribe();
        break;
      }
      case ReactionType.THUMBS_DOWN: {
        this.reaction.type = ReactionType.THUMBS_UP;
        this.reactionService.changeReaction(this.reaction).subscribe(result => this.reaction = result);
      }
      case ReactionType.NEUTRAL: {
        this.reaction.type = ReactionType.THUMBS_UP;
        this.reactionService.createReaction(this.reaction).subscribe(result => this.reaction = result);
      }
    }
  }

  public onDownVote(): void {
    switch(this.reaction.type) {
      case ReactionType.THUMBS_UP: {
        this.reaction.type = ReactionType.THUMBS_DOWN;
        this.reactionService.changeReaction(this.reaction).subscribe(result => this.reaction = result);
        break;
      }
      case ReactionType.THUMBS_DOWN: {
        this.reaction.type = ReactionType.NEUTRAL;
        this.reactionService.deleteById(this.reaction.id).subscribe();
      }
      case ReactionType.NEUTRAL: {
        this.reaction.type = ReactionType.THUMBS_DOWN;
        this.reactionService.createReaction(this.reaction).subscribe(result => this.reaction = result);
      }
    }
  }

  public onDelete(): void {
    this.deleteMessage.emit(this.message);
  }

  public getUpvoteCount(): number {
    if (this.reaction.type === ReactionType.THUMBS_UP) {
      return this.message.upVotes + 1;
    } else {
      return this.message.upVotes;
    }
  }

  public getDownVoteCount(): number {
    if (this.reaction.type === ReactionType.THUMBS_DOWN) {
      return this.message.downVotes + 1;
    } else {
      return this.message.downVotes;
    }
  }
}
