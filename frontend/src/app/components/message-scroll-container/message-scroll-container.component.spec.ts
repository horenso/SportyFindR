import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageScrollContainerComponent } from './message-scroll-container.component';

describe('MessageScrollContainerComponent', () => {
  let component: MessageScrollContainerComponent;
  let fixture: ComponentFixture<MessageScrollContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MessageScrollContainerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageScrollContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
