import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Message} from '../../dtos/message';
import {MessageService} from '../../services/message.service';
import {IconType} from '../../util/m-location';
import {Router} from '@angular/router';
import {SidebarService} from '../../services/sidebar.service';

@Component({
  selector: 'app-filter-messages',
  templateUrl: './filter-messages.component.html',
  styleUrls: ['./filter-messages.component.scss']
})
export class FilterMessagesComponent implements OnInit {

  @Output() goBack = new EventEmitter();

  public messages: Message[];

  constructor(private messageService: MessageService,
              private route: Router,
              private sidebarService: SidebarService) { }

  ngOnInit(): void {
  }

  onClose(): void {
    this.route.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }

}
