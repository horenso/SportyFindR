import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReactionMessageComponent } from './reaction-message.component';

describe('ReactionMessageComponent', () => {
  let component: ReactionMessageComponent;
  let fixture: ComponentFixture<ReactionMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReactionMessageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReactionMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
