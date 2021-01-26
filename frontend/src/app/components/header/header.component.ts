import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NotificationService} from '../../services/notification.service';
import {LocalStorageService} from 'ngx-webstorage';
import {UserService} from '../../services/user.service';
import {User} from '../../dtos/user';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  username: string;
  loggedUser: User;

  constructor(public authService: AuthService,
              private localStorage: LocalStorageService,
              private userService: UserService) {
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.username = this.localStorage.retrieve('username');
      this.userService.getUserByEmail(this.username).subscribe(result => {
        this.loggedUser = result;
      });
    }
  }


}
