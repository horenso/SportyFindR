import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {LocalStorageService} from 'ngx-webstorage';

@Component({
  selector: 'app-user-account',
  templateUrl: './user-account.component.html',
  styleUrls: ['./user-account.component.scss']
})
export class UserAccountComponent implements OnInit {

  loggedUser: User;

  constructor(private authService: AuthService,
              private userService: UserService,
              private localStorage: LocalStorageService
  ) { }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      const email = this.localStorage.retrieve('username');
      this.userService.getUserByEmail(email).subscribe(result => {
        this.loggedUser = result;
        console.log(result);
      });
    }
  }

}
