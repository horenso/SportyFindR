import { Component, OnInit } from '@angular/core';
import {User} from '../../dtos/user';
import {LocalStorageService} from 'ngx-webstorage';
import {UserService} from '../../services/user.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NotificationService} from '../../services/notification.service';
import {Router} from '@angular/router';
import {HeaderComponent} from '../header/header.component';

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['./edit-account.component.scss']
})
export class EditAccountComponent implements OnInit {

  user: User;
  accountForm: FormGroup;
  error: boolean = false;
  errorMessage: string = '';

  constructor(private localStorage: LocalStorageService,
              private userService: UserService,
              private formBuilder: FormBuilder,
              private notificationService: NotificationService,
              private router: Router) { }

  ngOnInit(): void {
    this.accountForm = this.formBuilder.group({
      username: [, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      email: [, [Validators.required, Validators.minLength(6), Validators.maxLength(30), Validators.email]],
      password: [, Validators.minLength(7)]
    });

    this.userService.getUserByEmail(this.localStorage.retrieve('username')).subscribe(
      result => {
        this.user = result;
        if (this.user != null) {
          this.setValues();
        }
      }
    );
  }

  private setValues(): void {
    this.accountForm.controls.username.setValue(this.user.name);
    this.accountForm.controls.email.setValue(this.user.email);
  }

  onCancel() {
    return null;
  }

  onConfirm() {
    const val = this.accountForm.value;
    const updatedUser = new User(this.user.id,
                    this.accountForm.value.username,
                    this.accountForm.value.email,
                    this.accountForm.value.password,
                    true,
                    this.user.roleIds);

    this.userService.updateUser(updatedUser).subscribe(() => {
      this.notificationService.success('Successfully updated user: ' + updatedUser.name);
      this.localStorage.clear('username');
      this.localStorage.store('username', val.email);
      this.router.navigate(['']);
    },
      error => {
        this.error = true;
        if (typeof error.error === 'object') {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
        this.notificationService.error(this.errorMessage);
      });
  }
}
