import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CoursePage } from './course-page';
import { CoursePageService } from './services/course-page-service';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

describe('CoursePage', () => {
  let component: CoursePage;
  let fixture: ComponentFixture<CoursePage>;
  let service: CoursePageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoursePage],
      providers: [
        provideHttpClient(),
        CoursePageService,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '201',
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CoursePage);
    component = fixture.componentInstance;
    service = TestBed.inject(CoursePageService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  /*it('should load course data on init', (done) => {
    service.getCourseProfile(201).subscribe(data => {
      expect(data).toBeDefined();
      expect(data.name).toBe('Bachelor of Science in Computer Science');
      expect(data.area).toBe('Computer Science');
      done();
    });
  });

  it('should display course information', () => {
    const componentAny = component as any;
    expect(componentAny.course).toBeDefined();
    if (componentAny.course) {
      expect(componentAny.course.name).toBe('Bachelor of Science in Computer Science');
    }
  });*/
});
