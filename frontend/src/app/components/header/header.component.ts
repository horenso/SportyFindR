import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {LocalStorageService} from 'ngx-webstorage';
import {UserService} from '../../services/user.service';
import {User} from '../../dtos/user';
import {Router} from '@angular/router';

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
              private userService: UserService,
              private router: Router) {
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.username = this.localStorage.retrieve('username');
      this.userService.getUserByEmail(this.username).subscribe(result => {
        this.loggedUser = result;
      });
    }
  }

  deleteUser() {
    console.log(this.loggedUser);
    this.userService.deleteUserById(this.loggedUser.id).subscribe(result => {
      this.authService.logoutUser();
    });
    this.router.navigate(['']);
  }
}
