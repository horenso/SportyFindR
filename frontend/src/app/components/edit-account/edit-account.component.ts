import { Component, OnInit } from '@angular/core';
import {User} from '../../dtos/user';
import {LocalStorageService} from 'ngx-webstorage';
import {UserService} from '../../services/user.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['./edit-account.component.scss']
})
export class EditAccountComponent implements OnInit {

  user: User;
  accountForm: FormGroup;

  constructor(private localStorage: LocalStorageService,
              private userService: UserService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.accountForm = this.formBuilder.group({
      username: [, [Validators.required, Validators.minLength(3)]],
      email: [, [Validators.required]],
      password: []
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

    this.localStorage.clear('username');
    this.localStorage.store('username', val.email);

    this.userService.updateUser(updatedUser).subscribe(result => {
      console.log(result);
    });
  }
}
