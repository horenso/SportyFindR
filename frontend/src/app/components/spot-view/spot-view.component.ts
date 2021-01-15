import {Component, EventEmitter, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Message} from 'src/app/dtos/message';
import {MessageService} from 'src/app/services/message.service';
import {SidebarService} from 'src/app/services/sidebar.service';
import {SpotService} from 'src/app/services/spot.service';
import {parsePositiveInteger} from '../../util/parse-int';
import {MLocSpot} from '../../util/m-loc-spot';
import {MapService} from 'src/app/services/map.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-spot-view',
  templateUrl: './spot-view.component.html',
  styleUrls: ['./spot-view.component.scss']
})
export class SpotViewComponent implements OnInit, OnDestroy {

  @Output() goBack = new EventEmitter();

  public spotId: number;
  public locationId: number;
  public spot: MLocSpot;

  public messageList: Message[] = [];
  public messageForm: FormGroup;

  constructor(
    private messageService: MessageService,
    private spotService: SpotService,
    private formBuilder: FormBuilder,
    private sidebarService: SidebarService,
    private activedRoute: ActivatedRoute,
    private mapService: MapService,
    private router: Router,
    private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.activedRoute.params.subscribe(params => {
      this.locationId = parsePositiveInteger(params.locId);
      this.spotId = parsePositiveInteger(params.spotId);

      if (isNaN(this.locationId)) {
        this.notificationService.navigateHomeAndShowError(NotificationService.locIdNotInt);
        return;
      }

      if (isNaN(this.spotId)) {
        this.notificationService.navigateHomeAndShowError(NotificationService.spotIdNotInt);
        return;
      }

      if (this.sidebarService.spot != null && this.sidebarService.spot.id === this.spotId) {
        // SidebarService has the spot because it was just clicked on from within a location
        this.spot = this.sidebarService.spot;
        this.getMessagesAndStartEventHandling();
      } else {
        this.spotService.getById(this.spotId).subscribe(
          result => {
            this.spot = result;
            this.getMessagesAndStartEventHandling();
          }, error => {
            this.notificationService.navigateHomeAndShowError('Error loading spot!');
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
    this.messageService.create(newMessage).subscribe(
      (result: Message) => {
        this.addMessage(result);
        this.messageForm.reset();
        this.notificationService.success(result.content);
      }
    );
  }

  onGoBack(): void {
    this.router.navigate(['locations', this.locationId]);
  }

  deleteOneMessage(message: Message): void {
    this.messageService.deleteById(message.id).subscribe(result => {
      this.messageList = this.messageList.filter(m => message.id !== m.id);
    });
  }

  deleteSpot(spotId: number) {
    this.spotService.deleteById(spotId).subscribe(result => {
      if (result) { // if the location was deleted
        this.mapService.removeMarkerLocation(this.locationId);
        this.router.navigate(['']);
        this.sidebarService.changeVisibilityAndFocus({isVisible: false});
      } else {
        this.router.navigate(['locations', this.locationId]);
      }
    });
  }

  editSpot(): void {
    this.sidebarService.spot = this.spot;
    this.router.navigate(['locations', this.locationId, 'spots', this.spotId, 'edit']);
  }

  private getMessagesAndStartEventHandling(): void {
    this.messageService.getBySpotId(this.spot.id).subscribe(
      result => {
        this.messageList = result;
        console.log(`Loaded ${result.length} messages.`);
      }, error => {
        this.notificationService.error('Error loading messages!');
        console.log(error);
      });
    this.handleEvents();
  }

  private addMessage(newMessage: Message): void {
    if (!this.messageList.some(m => m.id === newMessage.id)) {
      this.messageList.push(newMessage);
    }
  }

  private handleEvents(): void {
    this.spotService.openSseConnection(this.spot.id).subscribe({
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
