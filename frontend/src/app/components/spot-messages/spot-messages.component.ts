import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Message} from 'src/app/dtos/message';
import {MessageService} from 'src/app/services/message.service';
import {ReactionService} from 'src/app/services/reaction.service';
import {SpotService} from 'src/app/services/spot.service';

@Component({
  selector: 'app-spot-messages',
  templateUrl: './spot-messages.component.html',
  styleUrls: ['./spot-messages.component.scss']
})
export class SpotMessagesComponent implements OnInit {

  @Input() spotId: number;
  @Output() goBack = new EventEmitter();

  messageList: Message[] = [];
  messageForm: FormGroup;

  constructor(
    private messageService: MessageService,
    private spotService: SpotService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder
  ) {
  }

  ngOnInit(): void {
    this.messageService.getMessagesBySpot(this.spotId).subscribe(
      (result) => {
        this.messageList = result;
        console.log('Loaded messages:');
        console.log(this.messageList);
      }
    );

    this.messageForm = this.formBuilder.group({
      content: [null, [Validators.required, Validators.minLength(1)]],
    });

    this.spotService.observeEvents(this.spotId).subscribe({
      next: (event) => {
        const newMessage: Message = JSON.parse(event.data);
        console.log(event.type, event.data);
        switch (event.type) {
          case 'message/new': {
            this.addMessage(newMessage);
            break;
          }
          case 'message/delete': {
            this.messageList = this.messageList.filter(m => m.id !== newMessage.id);
            break;
          }
          case 'message/updateReaction': {
            const target = this.messageList.find(m => m.id === newMessage.id);
            target.upVotes = newMessage.upVotes;
            target.downVotes = newMessage.downVotes;
            break;
          }
        }

      }
    });
  }

  submitDialog() {
    const newMessage = new Message(null, this.messageForm.value.content, null, this.spotId);
    this.messageService.saveMessage(newMessage).subscribe(
      (result: Message) => {
        this.addMessage(result);
        this.messageForm.reset();
      }
    );
  }

  public deleteOneMessage(message: Message): void {
    this.messageService.deleteById(message.id).subscribe(result => {
      this.messageList = this.messageList.filter(m => message.id !== m.id);
    });
  }

  private addMessage(newMessage: Message): void {
    if (!this.messageList.some(m => m.id === newMessage.id)) {
      this.messageList.push(newMessage);
    }
  }

}
