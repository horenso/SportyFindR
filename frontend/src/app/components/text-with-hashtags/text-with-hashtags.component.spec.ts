import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextWithHashtagsComponent } from './text-with-hashtags.component';

describe('TextWithHashtagsComponent', () => {
  let component: TextWithHashtagsComponent;
  let fixture: ComponentFixture<TextWithHashtagsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TextWithHashtagsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TextWithHashtagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
