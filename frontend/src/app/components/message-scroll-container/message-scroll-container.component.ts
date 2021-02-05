import { AfterViewInit, ChangeDetectorRef, ElementRef, EventEmitter, HostListener, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Message } from 'src/app/dtos/message';

@Component({
  selector: 'app-message-scroll-container [messages]',
  templateUrl: './message-scroll-container.component.html',
  styleUrls: ['./message-scroll-container.component.scss'],
  host: {'class': 'sidebarScrollableSection'}
})
export class MessageScrollContainerComponent implements OnInit, OnChanges {

  @Input() messages: Message[];
  @Input() canReact: boolean = false;
  @Input() canDelete: boolean = false;
  @Input() filteredMessages: boolean = false;
  @Input() showLoadMoreButton: boolean = true;
  @Input() scrollDownObservable: Observable<{}>;

  @Output() onLoadMore: EventEmitter<any> = new EventEmitter();
  @Output() onDelete: EventEmitter<Message> = new EventEmitter();
  @Output() onGoToLocation: EventEmitter<Message> = new EventEmitter();

  private previousLength: number = 0;

  constructor(private elementRef: ElementRef) {
  }

  ngOnInit(): void {
    setTimeout(() => this.scrollDownObservable?.subscribe(() => this.scrollToBottom()), 100);
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log('chages:');
    console.log(changes);

    if (changes.scrollDown) {
      this.scrollToBottom();
      return;
    }

    if (this.messages.length === this.previousLength) {
      return;
    }

    if (this.previousLength === 0) {
      console.log('first time received messages');
      this.scrollToBottom();
    } else if (this.messages.length > this.previousLength) {
      console.log('scroll back up after load');
      console.log(this.elementRef.nativeElement);
      this.scrollBackToPrevious();
    }

    this.previousLength = changes.messages.currentValue.length;
  }

  onLoadMoreButtonClick(): void {
    const messageArea = this.elementRef.nativeElement;
    const scrollOffset = messageArea.scrollHeight + messageArea.scrollTop;

    setTimeout(() => {
      messageArea.scrollTop = messageArea.scrollHeight - scrollOffset;
    });

    this.onLoadMore.emit();
  }

  @HostListener('scroll', ['$event']) private onScroll($event:Event) :void {
    const e = this.elementRef.nativeElement;
    const distBottom: number = e.scrollHeight - e.scrollTop - e.offsetHeight;
  };

  public scrollToBottom(): void {
    console.log(this.elementRef.nativeElement);
    console.log('scoll down');
    console.log('scoll height: ' + this.elementRef.nativeElement.scrollHeight);
    console.log('scoll top: ' + this.elementRef.nativeElement.scrollTop);

    setTimeout(() => {
      console.log(this.elementRef.nativeElement);
      this.elementRef.nativeElement.scrollTop = 99999;
      console.log('scoll height: ' + this.elementRef.nativeElement.scrollHeight);
      console.log('scoll top: ' + this.elementRef.nativeElement.scrollTop);
      console.log('scroll complete');
    });
  }

  private scrollBackToPrevious(): void {
    const messageArea = this.elementRef.nativeElement;
    const scrollOffset = messageArea.scrollHeight + messageArea.scrollTop;

    console.log('scoll back to previous height');
    console.log('scoll down');
    console.log('scoll height: ' + this.elementRef.nativeElement.scrollHeight);
    console.log('scoll top: ' + this.elementRef.nativeElement.scrollTop);

    setTimeout(() => {
      messageArea.scrollTop = messageArea.scrollHeight - scrollOffset;
      console.log('scoll height: ' + this.elementRef.nativeElement.scrollHeight);
      console.log('scoll top: ' + this.elementRef.nativeElement.scrollTop);
      console.log('scroll complete');
    });
  }

}
