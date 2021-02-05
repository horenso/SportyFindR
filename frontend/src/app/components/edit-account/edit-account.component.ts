import {Component, OnInit} from '@angular/core';
import {User} from '../../dtos/user';
import {LocalStorageService} from 'ngx-webstorage';
import {UserService} from '../../services/user.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {NotificationService} from '../../services/notification.service';
import {Router} from '@angular/router';
import {AuthRequest} from '../../dtos/auth-request';
import {Subscription} from 'rxjs';
import {AuthService} from '../../services/auth.service';
import {MyErrorStateMatcher} from '../register/register.component';

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['./edit-account.component.scss']
})
export class EditAccountComponent implements OnInit {

  matcher = new MyErrorStateMatcher();
  passwordForm: FormGroup;
  user: User;
  accountForm: FormGroup;
  error: boolean = false;
  errorMessage: string = '';
  private subscription: Subscription = null;

  constructor(private localStorage: LocalStorageService,
              private userService: UserService,
              private formBuilder: FormBuilder,
              private authService: AuthService,
              private notificationService: NotificationService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.accountForm = this.formBuilder.group({
      username: [, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      email: [, [Validators.required, Validators.minLength(6), Validators.maxLength(40), Validators.email]],
      passwords: this.passwordForm = this.formBuilder.group({
        password: [, [Validators.required, Validators.minLength(7)]],
        confirmPassword: [, [Validators.required, Validators.minLength(7)]]
      },  {validator: this.checkPasswords
      })
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

  checkPasswords(group: FormGroup) { // here we have the 'passwords' group
    const pass = group.controls.password.value;
    const confirmPass = group.controls.confirmPassword.value;
    return pass === confirmPass ? null : { notSame: true};
  }

  private setValues(): void {
    this.accountForm.controls.username.setValue(this.user.name);
    this.accountForm.controls.email.setValue(this.user.email);
  }

  onCancel() {
    this.router.navigate(['']);
  }

  onConfirm() {
    const val = this.accountForm.value;
    const updatedUser = new User(this.user.id,
      this.accountForm.value.username,
      this.accountForm.value.email,
      this.passwordForm.value.password,
      true,
      this.user.roleIds);

    this.userService.updateUser(updatedUser).subscribe(() => {
        this.localStorage.clear('username');
        this.localStorage.store('username', val.email);
        const authRequest: AuthRequest = new AuthRequest(updatedUser.email, updatedUser.password);
        this.authenticateUser(authRequest);
      },
      error => {
        this.error = true;
        if (typeof error.error === 'object') {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage = error.error;
        }
        this.notificationService.error(this.errorMessage);
      });
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    console.log('Try to authenticate user: ' + authRequest.email);
    this.subscription = this.authService.loginUser(authRequest).subscribe(
      () => {
        this.notificationService.success('Successfully edited user');
        this.router.navigate(['']);
        this.localStorage.store('username', authRequest.email);
      },
      error => {
        console.log('Could not log in due to:');
        console.log(error);
        this.error = true;
        if (typeof error.error === 'object') {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
        this.notificationService.error(this.errorMessage);
      }
    );
  }
}
