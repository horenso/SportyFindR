import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Message } from 'src/app/dtos/message';
import { MessageService } from 'src/app/services/message.service';

@Component({
  selector: 'app-spot-messages',
  templateUrl: './spot-messages.component.html',
  styleUrls: ['./spot-messages.component.scss']
})
export class SpotMessagesComponent implements OnInit {

  messageList: Array<Message> = [];
  idString: string;
  currentSpot: number;

  messageForm: FormGroup;

  constructor(
    private messageService: MessageService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe( params => {
      this.idString = params['id'];
      this.currentSpot = +this.idString;
      if (Number.isInteger(this.currentSpot)) {
        this.messageService.getMessagesBySpot(this.currentSpot).subscribe(
          (result) => {
            this.messageList = result;
            console.log(this.messageList);
          }
          //,
          //  (error) => {
          //  TODO: handle error
          // });
        )
      }
    });

    this.messageForm = this.formBuilder.group({
      content: [null, [Validators.required, Validators.minLength(1)]],
    });
  }

  submitDialog() {
    var newMessage = new Message(null, this.messageForm.value.content, new Date(Date.now()), this.currentSpot);
    this.messageService.saveMessage(newMessage).subscribe(
      (result: Message) => {
        this.messageList.push(result);
        console.log(this.messageList);
      }
      //,
      //  (error) => {
      //  TODO: handle error
      // });
    );
  }

}
