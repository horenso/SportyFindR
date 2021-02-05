import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {Message} from '../../dtos/message';
import {MessageService} from '../../services/message.service';
import {IconType} from '../../util/m-location';
import {Router} from '@angular/router';
import {SidebarService} from '../../services/sidebar.service';
import {SubSink} from 'subsink';
import {NotificationService} from '../../services/notification.service';
import {FilterService} from 'src/app/services/filter.service';
import {FilterMessage} from 'src/app/dtos/filter-message';

@Component({
  selector: 'app-filter-messages',
  templateUrl: './filter-messages.component.html',
  styleUrls: ['./filter-messages.component.scss']
})
export class FilterMessagesComponent implements OnInit {

  @Output() goBack = new EventEmitter();

  private subs = new SubSink();

  public messageList: Message[] = [];

  public lastPage: boolean = false;

  public numberMessagesFiltered: number = 0;

  public includeExpirationDate = false;

  private currentFilter: FilterMessage = null;

  constructor(
    private messageService: MessageService,
    private route: Router,
    private sidebarService: SidebarService,
    private filterService: FilterService,
    private notificationService: NotificationService) {
  }

  ngOnInit(): void {
    this.subs.add(this.filterService.updateMessageFilterObservable.subscribe(change => {
      console.log(change);
      this.getMessages(change);
      this.currentFilter = change;
    }));
  }

  public loadMore() {
    this.messageService.filterMessage({
      categoryId: this.currentFilter.categoryId,
      user: this.currentFilter.user,
      hashtag: this.currentFilter.hashtag,
      time: this.currentFilter.time,
      page: this.currentFilter.page,
      size: this.currentFilter.size
    }).subscribe(result => {
      result.content.forEach(message => {
        this.messageList.unshift(message);
      });
      this.messageList = [].concat(this.messageList);
      this.lastPage = result.last;
      this.currentFilter.page = this.currentFilter.page + 1;
      console.log(`Loaded ${result.size} messages.`);
    }, error => {
      this.notificationService.error('Error loading messages!');
      console.log(error);
    });
  }

  public getMessages(change: FilterMessage) {
    this.messageService.filterMessage({
      categoryId: change.categoryId,
      user: change.user,
      hashtag: change.hashtag,
      time: change.time,
      page: change.page,
      size: change.size
    }).subscribe(result => {
      this.messageList = result.content.reverse();
      this.messageList = [].concat(this.messageList);
      this.numberMessagesFiltered = result.totalElements;
      this.lastPage = result.last;
      change.page = result.number + 1;
      console.log(`Loaded ${result.size} messages.`);
    }, error => {
      this.notificationService.error('Error loading messages!');
      console.log(error);
    });
  }

  onClose(): void {
    this.route.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }
}
