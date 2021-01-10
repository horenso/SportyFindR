import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {SpotService} from '../../services/spot.service';
import {SidebarService} from '../../services/sidebar.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-text-with-hashtags [text]',
  templateUrl: './text-with-hashtags.component.html',
  styleUrls: ['./text-with-hashtags.component.scss']
})
export class TextWithHashtagsComponent implements OnInit {

  constructor(
    private route: Router
  ) {
  }

  @Input() text: string;
  @Output() clickedHashtag = new EventEmitter();

  tokens: string[] = [];

  ngOnInit(): void {
    this.tokens = this.text.split(/(#[A-Za-z0-9]+)/g);
  }

  public isHashtag(word: string) {
    return /^(#[A-Za-z0-9]+)$/.test(word);
  }

  public onClickedHashtag(hashtag: string): void {
    console.log('Clicked on hashtag: ' + hashtag);
    this.route.navigate(['hashtags', hashtag.substr(1)]);
    //this.clickedHashtag.emit(hashtag);

  }

  // this is to prevent the double click select on hashtags
  public preventDefault(event: MouseEvent): void {
    event.preventDefault();
  }
}
