import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Message} from 'src/app/dtos/message';
import {Reaction, ReactionType} from 'src/app/dtos/reaction';
import {ReactionService} from 'src/app/services/reaction.service';
import {SubSink} from 'subsink';
import {AuthService} from '../../services/auth.service';
import {SpotService} from '../../services/spot.service';
import {Spot} from '../../dtos/spot';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit, OnDestroy {

  @Input() message: Message;
  @Input() canReact: boolean = true; // whether the component shows reaction buttons
  @Input() canDelete: boolean = true; // whether the component shows a delete button
  @Input() filteredMessage: boolean = false; // whether the component can navigate to the containing location

  @Output() deleteMessage = new EventEmitter();

  author: string = ''; // in Version 3 the user name will be displayed
  reaction: Reaction;

  spot: Spot;

  private subs = new SubSink();

  constructor(private reactionService: ReactionService,
              public authService: AuthService,
              private spotService: SpotService,
              private router: Router) {
  }

  ngOnInit(): void {
    console.log(this.canDelete);
    
    if (this.message.ownerReaction == null) {
      this.message.ownerReaction = ReactionType.NEUTRAL;
    }
    this.reaction = new Reaction(this.message.ownerReactionId, this.message.id, this.message.ownerReaction, null);
    this.subs.add(this.spotService.getSpotById(this.message.spotId).subscribe(spot => this.spot = spot));
  }

  ngOnDestroy(): void {
    this.subs?.unsubscribe();
  }

  public onSpot() {
    this.subs.add(this.spotService.getSpotById(this.message.spotId).subscribe(spot => this.spot = spot));
    this.router.navigate(['locations', this.spot.location.id, 'spots', this.spot.id]);
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
    console.log('on delete message in message');

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
