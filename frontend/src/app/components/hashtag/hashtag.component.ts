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

@Component({
  selector: 'app-hashtag',
  templateUrl: './hashtag.component.html',
  styleUrls: ['./hashtag.component.scss']
})
export class HashtagComponent implements OnInit {

  hashtag: Hashtag;
  hashtagName: string;
  spotsFlag: boolean = false;
  messagesFlag: boolean = false;
  public messageList: Message[] = [];
  public spotList: MLocSpot[] = [];

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

  private getMessages(): void {
    this.messageService.filterMessage({hashtag: this.hashtagName, user: null, page: 0, size: 10}).subscribe(result => {
      this.messageList = result.content;
    });
  }

  goToSpot(spot: Spot) {
    this.router.navigate(['locations', spot.location.id, 'spots', spot.id]);
  }

  onClose(): void {
    this.router.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }
}
