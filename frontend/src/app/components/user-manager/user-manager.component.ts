import { Component, OnInit } from '@angular/core';
import {User} from "../../dtos/user";

@Component({
  selector: 'app-user-manager',
  templateUrl: './user-manager.component.html',
  styleUrls: ['./user-manager.component.scss']
})
export class UserManagerComponent implements OnInit {

  users: User[];

  constructor() { }

  ngOnInit(): void {
  }

}
