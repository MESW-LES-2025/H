import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UniversityPage } from './university-page';
import { provideRouter } from '@angular/router';

describe('UniversityPage', () => {
  let component: UniversityPage;
  let fixture: ComponentFixture<UniversityPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UniversityPage],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(UniversityPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

