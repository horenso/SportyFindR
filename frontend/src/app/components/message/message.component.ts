import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Message } from 'src/app/dtos/message';
import { ReactionType } from 'src/app/dtos/reaction';
import { faTrash, faArrowUp, faArrowDown, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  author: string = 'Anonymous'; // in Version 3 the user name will be displayed
  @Input() message: Message;
  @Input() canReact: boolean = true; // whether the component shows reaction buttons
  @Input() canDelete: boolean = true; // wether the component shows a delete button 

  @Output() changedReaction = new EventEmitter<ReactionType>();

  deleteSymbol: IconDefinition = faTrash;
  upVoteSymbol: IconDefinition = faArrowUp;
  downVoteSymbol: IconDefinition = faArrowDown

  constructor() { }

  ngOnInit(): void {
  }

}
