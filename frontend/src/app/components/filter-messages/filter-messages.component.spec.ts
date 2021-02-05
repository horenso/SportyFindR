import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FilterMessagesComponent} from './filter-messages.component';

describe('FilterMessagesComponent', () => {
  let component: FilterMessagesComponent;
  let fixture: ComponentFixture<FilterMessagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FilterMessagesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
