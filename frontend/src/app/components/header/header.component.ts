import {Component, OnChanges, OnInit, SimpleChanges} from '@angular/core';
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
    this.retrieveUsername();
  }

  deleteUser() {
    this.userService.deleteUserById(this.loggedUser.id).subscribe(result => {
      this.authService.logoutUser();
    });
    this.router.navigate(['']);
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: 'Deleting your account will also delete your messages and reactions, do you want to proceed?'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteUser();
      }
    });
  }

  retrieveUsername(): void {
    this.authService.currentUser.subscribe(result => {
      if (this.authService.currentUserEmail() != null) {
        this.userService.getUserByEmail(this.authService.currentUserEmail()).subscribe(
          result => {
            this.loggedUser = result;
          }
        );
      }
    });
  }
}
