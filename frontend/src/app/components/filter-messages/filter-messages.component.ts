import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {Message} from '../../dtos/message';
import {MessageService} from '../../services/message.service';
import {IconType} from '../../util/m-location';
import {Router} from '@angular/router';
import {SidebarService} from '../../services/sidebar.service';
import {SubSink} from 'subsink';
import {NotificationService} from '../../services/notification.service';

@Component({
  selector: 'app-filter-messages',
  templateUrl: './filter-messages.component.html',
  styleUrls: ['./filter-messages.component.scss']
})
export class FilterMessagesComponent implements OnInit {

  @Output() goBack = new EventEmitter();

  @ViewChild('messageArea') private messageArea: ElementRef;
  @ViewChild('messageInput') private messageInput: ElementRef;

  private subs = new SubSink();

  public messageList: Message[] = [];

  private currentPage: number = 0;
  public lastPage: boolean = false;
  private pageSize: number = 10;

  public includeExpirationDate = false;

  constructor(private messageService: MessageService,
              private route: Router,
              private sidebarService: SidebarService,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.getMessageList();
  }

  private getMessageList() {
    this.subs.add(this.messageService.updateMessageFilterObservable.subscribe(change => {
      this.messageService.filterMessage({
        categoryMes: change.categoryMes,
        user: change.user,
        hashtag: change.hashtag,
        time: change.time,
        page: this.currentPage,
        size: this.pageSize
      }).subscribe(result => {
        this.messageList = result.content;
        this.lastPage = result.last;
        console.log(`Loaded ${result.size} messages.`);
        setTimeout(() => {
          this.scrollMessageAreaBottom();
          console.log(this.messageArea.nativeElement.scrollHeight);
          console.log(this.messageArea.nativeElement.scrollTop);
        });
      }, error => {
        this.notificationService.error('Error loading messages!');
        console.log(error);
      });
    }));
  }

  private scrollMessageAreaBottom(): void {
    this.messageArea.nativeElement.scrollTop = this.messageArea.nativeElement.scrollHeight;
  }

  onClose(): void {
    this.route.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }

}
