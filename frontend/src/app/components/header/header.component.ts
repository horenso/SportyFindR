import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {LocalStorageService} from 'ngx-webstorage';
import {UserService} from '../../services/user.service';
import {User} from '../../dtos/user';
import {Router} from '@angular/router';
import {ConfirmDialogComponent} from '../confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  username: string;
  loggedUser: User;
  result: string = '';

  constructor(public authService: AuthService,
              public router: Router,
              private localStorage: LocalStorageService,
              private userService: UserService,
              public dialog: MatDialog) {
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.username = this.localStorage.retrieve('username');
      this.userService.getUserByEmail(this.username).subscribe(result => {
        this.loggedUser = result;
        console.log(this.loggedUser);
      });
    }

    this.authService.currentUser.subscribe(result => {
      this.loggedUser = result;
    })
  }

  deleteUser() {
    this.userService.deleteUserById(this.loggedUser.id).subscribe(result => {
      this.authService.logoutUser();
      console.log(result);
    });
    this.router.navigate(['']);
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: 'Do you really want to delete your account? This will delete all your content as well.'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser();
      }
    });
  }
}
