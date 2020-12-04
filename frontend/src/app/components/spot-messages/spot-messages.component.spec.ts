import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpotMessagesComponent } from './spot-messages.component';

describe('SpotMessagesComponent', () => {
  let component: SpotMessagesComponent;
  let fixture: ComponentFixture<SpotMessagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpotMessagesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpotMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
