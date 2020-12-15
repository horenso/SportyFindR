import { Component, OnInit } from '@angular/core';
import {Message} from '../../dtos/message';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MessageService} from '../../services/message.service';
import {ActivatedRoute} from '@angular/router';
import {ReactionService} from '../../services/reaction.service';
import {Reaction} from '../../dtos/reaction';

@Component({
  selector: 'app-reaction-message',
  templateUrl: './reaction-message.component.html',
  styleUrls: ['./reaction-message.component.scss']
})
export class ReactionMessageComponent implements OnInit {
  currentMessage: number;
  idMessage: string;
  reactionsList: Array<Reaction> = [];
  message: Message = null;
  upvotes: number = 0;
  downvotes: number = 0;

  messageForm: FormGroup;
  private reaction: Reaction;

  constructor(
    private messageService: MessageService,
    private reactionService: ReactionService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe( params => {
      this.idMessage = params['id'];
      this.currentMessage = +this.idMessage;
      if (Number.isInteger(this.currentMessage)) {
        this.reactionService.getReactionsByMessage(this.currentMessage).subscribe(
          (result) => {
            this.reactionsList = result;
            this.reactionsList.forEach(value => {
              if (value.type === 'THUMBS_UP'){
                this.upvotes++;
              } else if (value.type === 'THUMBS_DOWN'){
                this.downvotes++;
              }
            });
            console.log(this.reactionsList);
            console.log('Upvotes: ' + this.upvotes);
            console.log('Downvotes: ' + this.downvotes);
          }
        );
        this.messageService.getMessageById(this.currentMessage).subscribe(
          result => {
            this.message = result;
            console.log(this.message);
          }
        );
      }
    });

    this.messageForm = this.formBuilder.group({
      content: [null, [Validators.required, Validators.minLength(1)]],
    });
  }

  downvote() {
    const downvote = new Reaction(null, null, 'THUMBS_DOWN', this.currentMessage);
    this.reactionService.createReaction(downvote).subscribe(result => {
      this.downvotes++;
      console.log(result);
    });
  }

  upvote() {
    const upvote = new Reaction(null, null, 'THUMBS_UP', this.currentMessage);
    this.reactionService.createReaction(upvote).subscribe(result => {
      this.upvotes++;
      console.log(result);
    });
  }
}
