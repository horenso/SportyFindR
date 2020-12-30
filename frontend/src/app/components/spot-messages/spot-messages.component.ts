import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Message} from 'src/app/dtos/message';
import {MessageService} from 'src/app/services/message.service';
import {SidebarService} from 'src/app/services/sidebar.service';
import {SpotService} from 'src/app/services/spot.service';
import {parseIntStrictly} from './../../util/parse-int';
import {MLocSpot} from '../../util/m-loc-spot';

@Component({
  selector: 'app-spot-messages',
  templateUrl: './spot-messages.component.html',
  styleUrls: ['./spot-messages.component.scss']
})
export class SpotMessagesComponent implements OnInit, OnDestroy {

  spotId: number;
  locationId: number;

  spot: MLocSpot;

  @Output() goBack = new EventEmitter();

  messageList: Message[] = [];
  messageForm: FormGroup;

  constructor(
    private messageService: MessageService,
    private spotService: SpotService,
    private formBuilder: FormBuilder,
    private sidebarService: SidebarService,
    private activeRoute: ActivatedRoute,
    private router: Router) {
  }

  ngOnInit(): void {
    this.activeRoute.params.subscribe(params => {
      this.locationId = parseIntStrictly(params.locId);
      this.spotId = parseIntStrictly(params.spotId);

      if (isNaN(this.locationId)) {
        console.log('Invalid location!');
        return;
      }

      if (isNaN(this.spotId)) {
        console.log('Invalid spot!');
        return;
      }

      if (this.sidebarService.spot != null && this.sidebarService.spot.id === this.spotId) {
        this.spot = this.sidebarService.spot;
        this.getMessagesAndStartEventHandling();
      } else {
        this.spotService.getSpotById(this.spotId).subscribe(result => {
          this.spot = result;
          this.getMessagesAndStartEventHandling();
        });
      }
    });

    this.messageForm = this.formBuilder.group({
      content: [null, [Validators.required, Validators.minLength(1)]],
    });
  }

  ngOnDestroy(): void {
    this.spotService.closeConnection();
  }

  submitDialog() {
    const newMessage = new Message(null, this.messageForm.value.content, null, this.spot.id);
    this.messageService.saveMessage(newMessage).subscribe(
      (result: Message) => {
        this.addMessage(result);
        this.messageForm.reset();
      }
    );
  }

  onGoBack(): void {
    this.router.navigate(['locations', this.locationId]);
  }

  public deleteOneMessage(message: Message): void {
    this.messageService.deleteById(message.id).subscribe(result => {
      this.messageList = this.messageList.filter(m => message.id !== m.id);
    });
  }

  deleteSpot(spotId: number) {
    this.spotService.deleteById(spotId).subscribe(result => {
      // TODO: notificaiton
    });
    // TODO: if this is the last spot of the location, do this:
    // this.mapService.removeMarkerLocation(this.locationId);
  }

  private getMessagesAndStartEventHandling(): void {
    this.messageService.getMessagesBySpot(this.spot.id).subscribe(result => {
      this.messageList = result;
      console.log('Loaded messages:');
      console.log(this.messageList);
    });
    this.handleEvents();
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
}
