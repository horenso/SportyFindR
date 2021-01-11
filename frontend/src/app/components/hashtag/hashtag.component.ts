import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {SidebarService} from '../../services/sidebar.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HashtagService} from '../../services/hashtag.service';
import {Spot} from '../../dtos/spot';
import {Message} from '../../dtos/message';
import {Hashtag} from '../../dtos/hashtag';
import {parseIntStrictly} from '../../util/parse-int';
import {IconType} from '../../util/m-location';
import {MessageService} from '../../services/message.service';

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

  constructor(
    public authService: AuthService,
    private router: Router,
    private hashtagService: HashtagService,
    private activedRoute: ActivatedRoute,
    private sidebarService: SidebarService,
    private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.activedRoute.params.subscribe(params => {
      this.hashtagName = params.hashtagName;
      console.log(this.hashtagName);
      });
    this.hashtagService.getHashtagByName(this.hashtagName).subscribe(result => {
      console.log(result);
      this.hashtag = result;
      this.messageList = result.messagesList;
    });
    }

  showSpots(): void {
    this.spotsFlag = true;
    this.messagesFlag = false;
  }

  showMessages(): void {
    this.spotsFlag = false;
    this.messagesFlag = true;
  }

  hideBoth(): void {
    this.spotsFlag = false;
    this.messagesFlag = false;
  }


  goToSpot(spot: Spot) {
    this.router.navigate(['locations', spot.location.id, 'spots', spot.id]);
  }

  onClose(): void {
    this.router.navigate(['..']);
    this.sidebarService.markerLocation?.changeIcon(IconType.Default);
    this.sidebarService.changeVisibilityAndFocus({isVisible: false});
  }

  deleteOneMessage(message: Message): void {
    this.messageService.deleteById(message.id).subscribe(result => {
    this.messageList = this.messageList.filter(m => message.id !== m.id);
    });
  }
}
