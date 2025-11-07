import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UniversityPage } from './university-page';
import { UniversityPageService } from './services/university-page-service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('UniversityPage', () => {
  let component: UniversityPage;
  let fixture: ComponentFixture<UniversityPage>;
  let service: UniversityPageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UniversityPage],
    }).compileComponents();

    fixture = TestBed.createComponent(UniversityPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
