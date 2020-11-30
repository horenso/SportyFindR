import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {MessageService} from '../../services/message.service';
import {Message} from '../../dtos/message';
import {NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import * as _ from 'lodash';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  error: boolean = false;
  errorMessage: string = '';
  messageForm: FormGroup;
  // After first submission attempt, form validation will start
  submitted: boolean = false;
  private message: Message[];

  constructor(private messageService: MessageService, private ngbPaginationConfig: NgbPaginationConfig, private formBuilder: FormBuilder,
              private cd: ChangeDetectorRef, private authService: AuthService) {
    this.messageForm = this.formBuilder.group({
      content: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    this.loadMessage();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  /**
   * Starts form validation and builds a message dto for sending a creation request if the form is valid.
   * If the procedure was successful, the form will be cleared.
   */
  addMessage() {
    // this.submitted = true;
    // if (this.messageForm.valid) {
    //   const message: Message = new Message(null,
    //     this.messageForm.controls.content.value,
    //     new Date().toISOString()
    //   );
    //   this.createMessage(message);
    //   this.clearForm();
    // } else {
    //   console.log('Invalid input');
    // }
  }

  /**
   * Sends message creation request
   * @param message the message which should be created
   */
  createMessage(message: Message) {
    // this.messageService.createMessage(message).subscribe(
    //   () => {
    //     this.loadMessage();
    //   },
    //   error => {
    //     this.defaultServiceErrorHandling(error);
    //   }
    // );
  }

  getMessage(): Message[] {
    return this.message;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  /**
   * Loads the specified page of message from the backend
   */
  private loadMessage() {
    this.messageService.getMessagesBySpot(1).subscribe(
      (message: Message[]) => {
        this.message = message;
      },
      error => {
        this.defaultServiceErrorHandling(error);
      }
    );
  }


  private defaultServiceErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    if (typeof error.error === 'object') {
      this.errorMessage = error.error.error;
    } else {
      this.errorMessage = error.error;
    }
  }

  private clearForm() {
    this.messageForm.reset();
    this.submitted = false;
  }

}
