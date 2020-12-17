import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { result } from 'lodash';
import { Message } from 'src/app/dtos/message';
import { Reaction } from 'src/app/dtos/reaction';
import { AuthService } from 'src/app/services/auth.service';
import { MessageService } from 'src/app/services/message.service';
import { ReactionService } from 'src/app/services/reaction.service';
import { SpotService } from 'src/app/services/spot.service';

@Component({
  selector: 'app-spot-messages',
  templateUrl: './spot-messages.component.html',
  styleUrls: ['./spot-messages.component.scss']
})
export class SpotMessagesComponent implements OnInit {

  messageList: Message[] = [];
  lastSeenMessageId: number = -1;
  messageForm: FormGroup;

  spotIdString: string;
  spotId: number;

  constructor(
    private messageService: MessageService,
    private reactionService: ReactionService,
    private spotService: SpotService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe( params => {
      this.spotIdString = params['id'];
      this.spotId = +this.spotIdString;
      if (Number.isInteger(this.spotId)) {
        this.messageService.getMessagesBySpot(this.spotId).subscribe(
          (result) => {
            this.messageList = result;
            console.log('Loaded messages:')
            console.log(this.messageList);
          }
          //,
          //  (error) => {
          //  TODO: handle error
          // });
        )
      }
    });

    this.messageForm = this.formBuilder.group({
      content: [null, [Validators.required, Validators.minLength(1)]],
    });

    this.spotService.observeEvents(this.spotId).subscribe({
      next: (event) => {
        var newMessage: Message = JSON.parse(event.data);
        console.log(event.type, event.data);
        switch(event.type) {
          case 'message/new': {
            this.addMessage(newMessage);
            break;
          }
          case 'message/delete': {
            this.messageList = this.messageList.filter(m => m.id !== newMessage.id);
            break;
          }
          case 'message/updateReaction': {
            var target = this.messageList.find(m => m.id === newMessage.id);
            target.upVotes = newMessage.upVotes;
            target.downVotes = newMessage.downVotes;
            break;
          }
        }
        
      }
    });
  }

  submitDialog() {
    var newMessage = new Message(null, this.messageForm.value.content, null, this.spotId);
    this.messageService.saveMessage(newMessage).subscribe(
      (result: Message) => {
        this.addMessage(result);
        this.messageForm.reset();
      }
      //,
      //  (error) => {
      //  TODO: handle error
      // });
    );
  }

  public deleteOneMessage(message: Message): void {
    this.messageService.deleteById(message.id).subscribe( result => {
      this.messageList = this.messageList.filter( m => message.id != m.id);
    });
  }

  private addMessage(newMessage: Message): void {
    if (!this.messageList.some(m => m.id === newMessage.id )) {
      this.messageList.push(newMessage);
    }
  }

}
