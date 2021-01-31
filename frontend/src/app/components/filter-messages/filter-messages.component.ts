import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Message} from '../../dtos/message';
import {MessageService} from '../../services/message.service';
import {IconType} from '../../util/m-location';
import {Router} from '@angular/router';
import {SidebarService} from '../../services/sidebar.service';
import {SubSink} from 'subsink';
import {Page} from '../../models/page.model';

@Component({
  selector: 'app-filter-messages',
  templateUrl: './filter-messages.component.html',
  styleUrls: ['./filter-messages.component.scss']
})
export class FilterMessagesComponent implements OnInit {

  @Output() goBack = new EventEmitter();

  private subs = new SubSink();

  public messagePage: Message[];

  constructor(private messageService: MessageService,
              private route: Router,
              private sidebarService: SidebarService) { }

  ngOnInit(): void {

    this.subs.add(this.messageService.updateMessageFilterObservable.subscribe(change => {
      this.messageService.filterMessage({
        categoryMes: change.categoryMes,
        user: change.user,
        hashtag: change.hashtag,
        time: change.time
      }).subscribe(
        (result: Page<Message>) => {
          // add page to list method zeugs here ?
        }
      );
    }));
  }

  onClose(): void {
    this.route.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }

}
