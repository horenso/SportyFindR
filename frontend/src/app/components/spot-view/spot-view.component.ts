import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Message} from 'src/app/dtos/message';
import {MessageService} from 'src/app/services/message.service';
import {SidebarService} from 'src/app/services/sidebar.service';
import {SpotService} from 'src/app/services/spot.service';
import {parsePositiveInteger} from '../../util/parse-int';
import {MLocSpot} from '../../util/m-loc-spot';
import {MapService} from 'src/app/services/map.service';
import {NotificationService} from 'src/app/services/notification.service';
import {SubSink} from 'subsink';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-spot-view',
  templateUrl: './spot-view.component.html',
  styleUrls: ['./spot-view.component.scss']
})
export class SpotViewComponent implements OnInit, OnDestroy, AfterViewInit {

  @Output() goBack = new EventEmitter();

  public spotId: number;
  public locationId: number;
  public spot: MLocSpot;

  public messageList: Message[] = [];
  public newMessage: string = '';

  @ViewChild('messageInput') private messageInput: ElementRef;

  private subs = new SubSink();

  private currentPage: number = 0;
  public lastPage: boolean = false;
  private pageSize: number = 10;

  public includeExpirationDate = false;
  public minExpirationDate = new Date(Date.now());
  public expirationDate = null;

  constructor(
    private messageService: MessageService,
    private spotService: SpotService,
    private formBuilder: FormBuilder,
    private sidebarService: SidebarService,
    private activatedRoute: ActivatedRoute,
    private mapService: MapService,
    private router: Router,
    private notificationService: NotificationService,
    public authService: AuthService,
    private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.subs.add(this.activatedRoute.params.subscribe(params => {
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
        this.subs.add(this.spotService.getById(this.spotId).subscribe(
          result => {
            this.spot = result;
            this.getMessagesAndStartEventHandling();
          }, error => {
            this.notificationService.navigateHomeAndShowError('Error loading spot!');
          }));
      }
    }));
  }


  ngOnDestroy(): void {
    this.spotService.closeConnection();
    this.subs?.unsubscribe();
  }

  ngAfterViewInit(): void {
    if (this.messageInput != null) {
      this.messageInput.nativeElement.focus();
    }
    this.cdr.detectChanges();
  }

  keydown(event: KeyboardEvent): void {
    if (event.code === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.submitDialog();
    }
  }

  submitDialog(): void {
    if (this.newMessage?.length < 1 || /^\s*$/.test(this.newMessage)) {
      this.notificationService.error('Message must not be Empty!');
      return;
    }
    if (this.expirationDate != null) {
      this.expirationDate.setHours(this.expirationDate.getHours() + 1);
    }
    const newMessage = new Message(null, this.newMessage, null, null, this.spot.id, null, null);
    newMessage.expirationDate = this.expirationDate;
    this.subs.add(this.messageService.create(newMessage).subscribe(
      result => {
        this.addMessage(result);
        this.newMessage = '';
        this.expirationDate = null;
        this.includeExpirationDate = false;
      }, error => {
        this.notificationService.error(error.error.message);
        console.error(error);
      }
    ));
  }

  onLoadMore(): void {
    this.subs.add(this.messageService.getBySpotId(this.spot.id, this.currentPage, this.pageSize).subscribe(
      result => {
        result.content.forEach(message => {
          this.messageList.unshift(message);
        });
        this.messageList = [].concat(this.messageList); // It needs a new reference to detect changes
        this.lastPage = result.last;
        this.currentPage++;
      }
    ));
  }

  onGoBack(): void {
    this.router.navigate(['locations', this.locationId]);
  }

  deleteOneMessage(message: Message): void {
    this.subs.add(this.messageService.deleteById(message.id).subscribe(result => {
      this.messageList = this.messageList.filter(m => message.id !== m.id);
      this.messageList = [].concat(this.messageList);
    }));
  }

  deleteSpot(spotId: number) {
    this.subs.add(this.spotService.deleteById(spotId).subscribe(result => {
      if (result) { // if the location was deleted
        this.mapService.removeMarkerLocation(this.locationId);
        this.router.navigate(['']);
        this.sidebarService.changeVisibilityAndFocus({isVisible: false});
      } else {
        this.router.navigate(['locations', this.locationId]);
      }
    }));
  }

  editSpot(): void {
    this.sidebarService.spot = this.spot;
    this.router.navigate(['locations', this.locationId, 'spots', this.spotId, 'edit']);
  }

  enableExpirationDate(): void {
    this.expirationDate = new Date();
    this.expirationDate.setHours(this.expirationDate.getHours() + 1);
    this.expirationDate.setMinutes(0, 0, 0);
    // add a day
    this.includeExpirationDate = true;
  }

  disableExpirationDate(): void {
    this.expirationDate = null;
    this.includeExpirationDate = false;
  }

  private getMessagesAndStartEventHandling(): void {
    this.subs.add(this.messageService.getBySpotId(this.spot.id, this.currentPage, this.pageSize).subscribe(
      result => {
        this.messageList = result.content.reverse();
        this.lastPage = result.last;
        console.log('lastPage: ' + this.lastPage);

        this.currentPage++;
        console.log(`Loaded ${result.size} messages.`);
      }, error => {
        this.notificationService.error('Error loading messages!');
        console.log(error);
      }));
    this.handleEvents();
  }

  private addMessage(newMessage: Message): void {
    if (!this.messageList.some(m => m.id === newMessage.id)) {
      this.messageList.push(newMessage);
    }
  }

  private handleEvents(): void {
    this.subs.add(this.spotService.openSseConnection(this.spot.id).subscribe({
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
    }));
  }

  showControlItems(): boolean {
    if (this.authService.isLoggedIn()) {
      if (this.authService.isUserAdmin() || (this.spot.owner != null && this.authService.currentUserEmail() === this.spot.owner.email)) {
        return true;
      }
    } else { return false; }
  }
}
