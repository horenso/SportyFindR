import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  HostBinding,
  HostListener,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {Observable} from 'rxjs';
import {Message} from 'src/app/dtos/message';

@Component({
  selector: 'app-message-scroll-container [messages]',
  templateUrl: './message-scroll-container.component.html',
  styleUrls: ['./message-scroll-container.component.scss'],
})
export class MessageScrollContainerComponent implements OnChanges {

  @HostBinding('class') class = 'sidebarScrollableSection';

  @Input() messages: Message[];
  @Input() canReact: boolean = false;
  @Input() canDelete: boolean = false;
  @Input() filteredMessages: boolean = false;
  @Input() showLoadMoreButton: boolean = true;

  @Output() loadMore: EventEmitter<any> = new EventEmitter();
  @Output() deleteMessage: EventEmitter<Message> = new EventEmitter();
  @Output() goToLocation: EventEmitter<Message> = new EventEmitter();

  private previousLength: number = 0;

  constructor(private elementRef: ElementRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.scrollDown) {
      this.scrollToBottom();
      return;
    }

    if (this.messages.length === this.previousLength) {
      return;
    }

    if (this.previousLength === 0) {
      this.scrollToBottom();
    } else if (this.messages.length > this.previousLength) {
      this.scrollBackToPrevious();
    }

    this.previousLength = changes.messages.currentValue.length;
  }

  public scrollToBottom(): void {
    setTimeout(() => {
      this.elementRef.nativeElement.scrollTop = 99999;
    });
  }

  private scrollBackToPrevious(): void {
    const messageArea = this.elementRef.nativeElement;
    const scrollOffset = messageArea.scrollHeight + messageArea.scrollTop;

    setTimeout(() => {
      messageArea.scrollTop = messageArea.scrollHeight - scrollOffset;
    });
  }

}
