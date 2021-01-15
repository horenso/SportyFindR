import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrls: ['./page-not-found.component.scss']
})
export class PageNotFoundComponent implements OnInit, OnDestroy {

  public route: any;

  private subsciption: Subscription;

  constructor(private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.subsciption = this.activatedRoute.url.subscribe(url => {
      this.route = url.toString();
    });
  }

  ngOnDestroy(): void {
    if (this.subsciption != null) {
      this.subsciption.unsubscribe();
    }
  }
}
