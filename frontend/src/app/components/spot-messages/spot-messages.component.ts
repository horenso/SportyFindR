import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Message} from 'src/app/dtos/message';
import {Spot} from 'src/app/dtos/spot';
import {MessageService} from 'src/app/services/message.service';
import { SidebarService } from 'src/app/services/sidebar.service';
import {SpotService} from 'src/app/services/spot.service';

@Component({
  selector: 'app-spot-messages',
  templateUrl: './spot-messages.component.html',
  styleUrls: ['./spot-messages.component.scss']
})
export class SpotMessagesComponent implements OnInit {

  spot: Spot;
  @Output() goBack = new EventEmitter();

  messageList: Message[] = [];
  messageForm: FormGroup;
  deleted: boolean = false;

  constructor(
    private messageService: MessageService,
    private spotService: SpotService,
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private sidebarService: SidebarService,
  ) {
  }

  ngOnInit(): void {
    this.spot = this.sidebarService.spot;
    this.messageService.getMessagesBySpot(this.spot.id).subscribe(
      (result) => {
        this.messageList = result;
        console.log('Loaded messages:');
        console.log(this.messageList);
        this.changeDetectorRef.detectChanges();
      }
    );

    this.messageForm = this.formBuilder.group({
      content: [null, [Validators.required, Validators.minLength(1)]],
    });

    this.handleEvents();
  }

  submitDialog() {
    const newMessage = new Message(null, this.messageForm.value.content, null, this.spot.id);
    this.messageService.saveMessage(newMessage).subscribe(
      (result: Message) => {
        this.addMessage(result);
        this.messageForm.reset();
        this.changeDetectorRef.detectChanges();
      }
    );
  }

  onGoBack(): void {
    this.changeDetectorRef.detectChanges();
  }

  public deleteOneMessage(message: Message): void {
    this.messageService.deleteById(message.id).subscribe(result => {
      this.messageList = this.messageList.filter(m => message.id !== m.id);
      this.changeDetectorRef.detectChanges();
    });
  }

  deleteSpot(spotId: number) {
    this.spotService.deleteById(spotId).subscribe( result => {
      this.deleted = true;
      this.changeDetectorRef.detectChanges();
    });
  }

  private addMessage(newMessage: Message): void {
    if (!this.messageList.some(m => m.id === newMessage.id)) {
      this.messageList.push(newMessage);
    }
  }

  private handleEvents(): void {
    this.spotService.observeEvents(this.spot.id).subscribe({
      next: (event) => {
        const newMessage: Message = JSON.parse(event.data);
        console.log(event.type, event.data);
        switch (event.type) {
          case 'message/new': {
            this.addMessage(newMessage);
            this.changeDetectorRef.detectChanges();
            break;
          }
          case 'message/delete': {
            this.messageList = this.messageList.filter(m => m.id !== newMessage.id);
            this.changeDetectorRef.detectChanges();
            break;
          }
          case 'message/updateReaction': {
            const target = this.messageList.find(m => m.id === newMessage.id);
            target.upVotes = newMessage.upVotes;
            target.downVotes = newMessage.downVotes;
            this.changeDetectorRef.detectChanges();
            break;
          }
        }
      }
    });
  }
}
