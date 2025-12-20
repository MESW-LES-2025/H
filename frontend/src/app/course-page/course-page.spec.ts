import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CoursePage } from './course-page';
import { CoursePageService } from './services/course-page-service';
import { ActivatedRoute } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { CourseViewmodel } from './viewmodels/course-viewmodel';

describe('CoursePage', () => {
  let component: CoursePage;
  let fixture: ComponentFixture<CoursePage>;
  let service: jasmine.SpyObj<CoursePageService>;
  let activatedRoute: ActivatedRoute;

  const mockCourse: CourseViewmodel = {
    id: 201,
    name: 'Bachelor of Science in Computer Science',
    area: 'Computer Science',
    description: 'A comprehensive computer science program',
    duration: '48 months',
    level: 'Bachelor',
    language: 'English',
    credits: 180,
    bannerImage: 'https://example.com/banner.jpg',
    university: {
      id: 1,
      name: 'Test University',
      description: 'A test university',
      location: {
        id: 1,
        city: 'Test City',
        country: 'Test Country',
        costOfLiving: 1000,
      },
    },
    topics: ['Programming', 'Algorithms', 'Data Structures'],
    requirements: ['High school diploma', 'Math proficiency'],
    isFavorite: false,
    cost: 20000,
  };

  beforeEach(async () => {
    const serviceSpyObj = jasmine.createSpyObj('CoursePageService', [
      'getCourseProfile',
      'getFavoriteCourses',
      'addFavoriteCourse',
      'removeFavoriteCourse',
    ]);

    await TestBed.configureTestingModule({
      imports: [CoursePage],
      providers: [
        provideHttpClient(),
        { provide: CoursePageService, useValue: serviceSpyObj },
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
    service = TestBed.inject(
      CoursePageService,
    ) as jasmine.SpyObj<CoursePageService>;
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load course data on init', (done) => {
      service.getCourseProfile.and.returnValue(of(mockCourse));
      service.getFavoriteCourses.and.returnValue(of([201, 202]));

      spyOn(localStorage, 'getItem').and.returnValue('1');

      component.ngOnInit();

      setTimeout(() => {
        expect(service.getCourseProfile).toHaveBeenCalledWith(201);
        expect(component.course).toEqual(mockCourse);
        expect(component.isFavorite).toBe(false);
        done();
      }, 100);
    });

    it('should load course without checking favorites when user not logged in', (done) => {
      service.getCourseProfile.and.returnValue(of(mockCourse));

      spyOn(localStorage, 'getItem').and.returnValue(null);

      component.ngOnInit();

      setTimeout(() => {
        expect(service.getCourseProfile).toHaveBeenCalledWith(201);
        expect(component.course).toEqual(mockCourse);
        expect(service.getFavoriteCourses).not.toHaveBeenCalled();
        expect(component.isFavorite).toBe(false);
        done();
      }, 100);
    });

    it('should set isFavorite to false when course is not in favorites', (done) => {
      service.getCourseProfile.and.returnValue(of(mockCourse));
      service.getFavoriteCourses.and.returnValue(of([100, 150]));

      spyOn(localStorage, 'getItem').and.returnValue('1');

      component.ngOnInit();

      setTimeout(() => {
        expect(component.isFavorite).toBe(false);
        done();
      }, 100);
    });

    it('should set isFavorite to true when course is in favorites list', (done) => {
      service.getCourseProfile.and.returnValue(of(mockCourse));
      service.getFavoriteCourses.and.returnValue(of([100, 201, 150]));

      spyOn(component['authService'], 'getCurrentUserId').and.returnValue(1);

      component.ngOnInit();

      setTimeout(() => {
        expect(component.isFavorite).toBe(true);
        done();
      }, 100);
    });

    it('should parse course id from route params', () => {
      service.getCourseProfile.and.returnValue(of(mockCourse));
      spyOn(localStorage, 'getItem').and.returnValue(null);

      component.ngOnInit();

      expect(service.getCourseProfile).toHaveBeenCalledWith(201);
    });
  });

  describe('toggleFavorite', () => {
    beforeEach(() => {
      component.course = mockCourse;
    });

    it('should add course to favorites when not favorited', (done) => {
      component.isFavorite = false;
      spyOn(component['authService'], 'getCurrentUserId').and.returnValue(1); // <-- Add this line
      service.addFavoriteCourse.and.returnValue(of(void 0));

      component.toggleFavorite();

      setTimeout(() => {
        expect(service.addFavoriteCourse).toHaveBeenCalledWith(201);
        expect(component.isFavorite).toBe(true);
        done();
      }, 100);
    });

    it('should remove course from favorites when already favorited', (done) => {
      component.isFavorite = true;
      component.course = mockCourse;
      spyOn(component['authService'], 'getCurrentUserId').and.returnValue(1);
      service.removeFavoriteCourse.and.returnValue(of(void 0));

      component.toggleFavorite();

      setTimeout(() => {
        expect(service.removeFavoriteCourse).toHaveBeenCalledWith(201);
        expect(component.isFavorite).toBe(false);
        done();
      }, 100);
    });

    it('should not toggle favorite when course is null', () => {
      component.course = null;
      component.isFavorite = false;

      component.toggleFavorite();

      expect(service.addFavoriteCourse).not.toHaveBeenCalled();
      expect(service.removeFavoriteCourse).not.toHaveBeenCalled();
    });

    it('should call addFavoriteCourse service method', () => {
      component.isFavorite = false;
      component.course = mockCourse;
      spyOn(component['authService'], 'getCurrentUserId').and.returnValue(1);
      service.addFavoriteCourse.and.returnValue(of(void 0));

      component.toggleFavorite();

      expect(service.addFavoriteCourse).toHaveBeenCalledWith(201);
    });

    it('should call removeFavoriteCourse service method', () => {
      component.isFavorite = true;
      component.course = mockCourse;
      spyOn(component['authService'], 'getCurrentUserId').and.returnValue(1);
      service.removeFavoriteCourse.and.returnValue(of(void 0));

      component.toggleFavorite();

      expect(service.removeFavoriteCourse).toHaveBeenCalledWith(201);
    });

    it('should show login message when user not logged in', fakeAsync(() => {
      component.course = mockCourse;
      spyOn(component['authService'], 'getCurrentUserId').and.returnValue(null);

      component.toggleFavorite();

      expect(component.message).toBe('Please log in to save courses to your favorites.');
      expect(component.messageType).toBe('info');
      expect(service.addFavoriteCourse).not.toHaveBeenCalled();
      expect(service.removeFavoriteCourse).not.toHaveBeenCalled();

      tick(3000);
      expect(component.message).toBeNull();
      expect(component.messageType).toBeNull();
    }));

    it('should show error message when addFavoriteCourse fails', fakeAsync(() => {
      component.isFavorite = false;
      component.course = mockCourse;
      spyOn(component['authService'], 'getCurrentUserId').and.returnValue(1);
      service.addFavoriteCourse.and.returnValue(throwError(() => new Error('fail')));

      component.toggleFavorite();
      tick();

      expect(component.message).toBe('Could not add to favorites. Please try again.');
      expect(component.messageType).toBe('error');
      expect(component.isFavorite).toBe(false);

      tick(3000);
      expect(component.message).toBeNull();
      expect(component.messageType).toBeNull();
    }));

    it('should show error message when removeFavoriteCourse fails', fakeAsync(() => {
      component.isFavorite = true;
      component.course = mockCourse;
      spyOn(component['authService'], 'getCurrentUserId').and.returnValue(1);
      service.removeFavoriteCourse.and.returnValue(throwError(() => new Error('fail')));

      component.toggleFavorite();
      tick();

      expect(component.message).toBe('Could not remove from favorites. Please try again.');
      expect(component.messageType).toBe('error');
      expect(component.isFavorite).toBe(true);

      tick(3000);
      expect(component.message).toBeNull();
      expect(component.messageType).toBeNull();
    }));
  });

  describe('Component properties', () => {
    it('should initialize with default values', () => {
      expect(component.course).toBeNull();
      expect(component.active).toBe(1);
      expect(component.isFavorite).toBe(false);
      expect(component.message).toBeNull();
      expect(component.messageType).toBeNull();
    });

    it('should update course property when loaded', (done) => {
      service.getCourseProfile.and.returnValue(of(mockCourse));
      spyOn(localStorage, 'getItem').and.returnValue(null);

      component.ngOnInit();

      setTimeout(() => {
        expect(component.course).not.toBeNull();
        expect(component.course?.id).toBe(201);
        expect(component.course?.name).toBe(
          'Bachelor of Science in Computer Science',
        );
        done();
      }, 100);
    });
  });

  describe('Active tab', () => {
    it('should start with first tab active', () => {
      expect(component.active).toBe(1);
    });

    it('should allow changing active tab', () => {
      component.active = 2;
      expect(component.active).toBe(2);

      component.active = 3;
      expect(component.active).toBe(3);
    });
  });
});

