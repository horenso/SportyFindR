import { AfterContentInit, AfterViewInit, ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';

@Component({
  selector: 'app-text-with-hashtags [text]',
  templateUrl: './text-with-hashtags.component.html',
  styleUrls: ['./text-with-hashtags.component.scss']
})
export class TextWithHashtagsComponent implements OnInit, AfterViewInit {

  @Input() text: string;
  @Input() maxRows: number = 3;
  @Output() clickedHashtag = new EventEmitter();

  @ViewChild('textArea', {static: false}) textAreaElement: ElementRef;

  tokens: string[] = [];
  textTooLong: boolean = false;
  showAll: boolean = false;

  ngOnInit(): void {
    this.tokens = this.text.split(/(#[A-Za-z0-9]+)/g);
  }

  ngAfterViewInit(): void {
    const lineHeight = parseInt(window.getComputedStyle(this.textAreaElement.nativeElement).lineHeight);
    const elementHeight = this.textAreaElement.nativeElement.scrollHeight;

    setTimeout(() => this.textTooLong = (elementHeight / lineHeight) > this.maxRows);
  }

  public isHashtag(word: string) {
    return /^(#[A-Za-z0-9]+)$/.test(word);
  }

  public onClickedHashtag(hashtag: string): void {
    console.log('Clicked on hashtag: ' + hashtag);
    this.clickedHashtag.emit(hashtag);
  }

  public getTextAreaClass(): string {
    if (!this.showAll) {
      return 'lineClamp';
    } else {
      return '';
    }
  }

  // this is to prevent the double click select on hashtags 
  public preventDefault(event: MouseEvent): void {
    event.preventDefault();
  }
}
