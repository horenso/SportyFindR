import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {SidebarService} from '../../services/sidebar.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HashtagService} from '../../services/hashtag.service';
import {Spot} from '../../dtos/spot';
import {Message} from '../../dtos/message';
import {IconType} from '../../util/m-location';
import {MessageService} from '../../services/message.service';
import {Hashtag} from 'src/app/dtos/hashtag';
import { SpotService } from 'src/app/services/spot.service';
import { MLocSpot } from 'src/app/util/m-loc-spot';
import { result } from 'lodash';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { Observable, Subject } from 'rxjs';

@Component({
  selector: 'app-hashtag',
  templateUrl: './hashtag.component.html',
  styleUrls: ['./hashtag.component.scss']
})
export class HashtagComponent implements OnInit {

  hashtagName: string;
  spotsFlag: boolean = false;
  messagesFlag: boolean = false;

  public messageList: Message[] = [];
  public spotList: MLocSpot[] = [];

  private currentPage: number = 0;
  public lastPage: boolean = false;

  messageCount: number = 0;

  private scrollDownSubject: Subject<{}> = new Subject();
  public scrollDownObservable: Observable<{}> = this.scrollDownSubject.asObservable();

  constructor(
    public authService: AuthService,
    private router: Router,
    private spotService: SpotService,
    private activedRoute: ActivatedRoute,
    private sidebarService: SidebarService,
    private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.activedRoute.params.subscribe(params => {
      this.hashtagName = params.hashtagName;
      console.log(this.hashtagName);
      this.getSpots();
      this.getMessages();
    });
  }

  private getSpots(): void {
    this.spotService.getByLocationId(null, this.hashtagName, null).subscribe(result => {
      this.spotList = result;
    });
  }

  public getMessages(): void {
    this.messageService.filterMessage({hashtag: this.hashtagName, user: null, page: this.currentPage, size: 10}).subscribe(result => {
      result.content.forEach(message => {
        this.messageList.unshift(message);
      });
      this.messageCount = result.totalElements;
      this.messageList = [].concat(this.messageList);
      this.lastPage = result.last;
      console.log(result);
      this.currentPage++;
      console.log('hashtag got: ');
      console.log(this.messageList);
    });
  }

  public getSpotTab(): string {
    return `Spots (${this.spotList.length})`;
  }

  public getMessageTab(): string {
    return `Messages (${this.messageCount})`;
  }

  public tabChanged(tabChange: MatTabChangeEvent): void {
    if (tabChange.tab.textLabel.startsWith("Message")){
      this.scrollDownSubject.next();
    }
  }

  goToSpot(spot: MLocSpot) {
    console.log('given spot:');
    console.log(spot);

    this.router.navigate(['locations', spot.markerLocation.id, 'spots', spot.id]);
  }

  onClose(): void {
    this.router.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }
}
