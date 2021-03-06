import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {NotificationService} from '../../services/notification.service';
import {User} from '../../dtos/user';
import {ErrorStateMatcher} from '@angular/material/core';
import {UserService} from '../../services/user.service';

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const invalidCtrl = !!(control && control.invalid && control.parent.dirty);
    const invalidParent = !!(control && control.parent && control.parent.invalid && control.parent.dirty);
    return (invalidCtrl || invalidParent);
  }
}

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
  submitted: boolean = false;
  error: boolean = false;
  errorMessage: string = '';
  user: User;
  passwordForm: FormGroup;
  matcher = new MyErrorStateMatcher();
  passwordsMatch = false;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private router: Router,
    private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      username: [, [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
      email: [, [Validators.required, Validators.minLength(6), Validators.maxLength(40), Validators.email]],
      passwords: this.passwordForm = this.formBuilder.group({
        password: [, [Validators.required, Validators.minLength(7)]],
        confirmPassword: [, [Validators.required, Validators.minLength(7)]]
      },  {validator: this.checkPasswords
      })
    });
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  checkPasswords(group: FormGroup) { // here we have the 'passwords' group
    const pass = group.controls.password.value;
    const confirmPass = group.controls.confirmPassword.value;
    return pass === confirmPass ? null : { notSame: true};
  }

  register() {
    this.user = new User(null, this.registerForm.controls.username.value, this.registerForm.controls.email.value,
      this.passwordForm.controls.password.value, true, null );
    this.userService.registerUser(this.user).subscribe(() => {
        this.notificationService.success('Successfully registered, you can log in now.');
        this.router.navigate(['/login']);
      },
      error => {
        console.log('Could not register user due to:');
        console.log(error);
        this.notificationService.error('Could not register user.');
      });
  }
}
