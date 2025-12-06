import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { CoursePageService } from './course-page-service';
import { environment } from '../../../environments/environment';

describe('CoursePageService', () => {
  let service: CoursePageService;
  let httpMock: HttpTestingController;

  const mockCourseDTO = {
    id: 1,
    name: 'Computer Science',
    description: 'A comprehensive CS program',
    courseType: 'Bachelor',
    isRemote: false,
    minAdmissionGrade: 85,
    cost: 10000,
    duration: 48,
    credits: 180,
    language: 'English',
    startDate: '2025-09-01',
    applicationDeadline: '2025-06-01',
    website: 'https://example.com',
    contactEmail: 'contact@example.com',
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
    areasOfStudy: [
      { id: 1, name: 'Computer Science' },
      { id: 2, name: 'Software Engineering' },
    ],
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CoursePageService],
    });

    service = TestBed.inject(CoursePageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCourseProfile', () => {
    it('should fetch and map course profile', (done) => {
      const courseId = 1;

      service.getCourseProfile(courseId).subscribe((course) => {
        expect(course).toBeDefined();
        expect(course.id).toBe(1);
        expect(course.name).toBe('Computer Science');
        expect(course.area).toBe('Computer Science');
        expect(course.description).toBe('A comprehensive CS program');
        expect(course.duration).toBe('48 months');
        expect(course.level).toBe('Bachelor');
        expect(course.language).toBe('English');
        expect(course.credits).toBe(180);
        expect(course.university.name).toBe('Test University');
        expect(course.topics).toEqual([
          'Computer Science',
          'Software Engineering',
        ]);
        expect(course.requirements).toEqual(['Minimum admission grade: 85']);
        expect(course.isFavorite).toBe(false);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/courses/${courseId}`,
      );
      expect(req.request.method).toBe('GET');
      req.flush(mockCourseDTO);
    });

    it('should handle course with no areas of study', (done) => {
      const dtoWithoutAreas = { ...mockCourseDTO, areasOfStudy: [] };

      service.getCourseProfile(1).subscribe((course) => {
        expect(course.area).toBe('General');
        expect(course.topics).toEqual([]);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/courses/1`);
      req.flush(dtoWithoutAreas);
    });

    it('should handle course with no description', (done) => {
      const dtoWithoutDescription = { ...mockCourseDTO, description: '' };

      service.getCourseProfile(1).subscribe((course) => {
        expect(course.description).toBe('No description available');
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/courses/1`);
      req.flush(dtoWithoutDescription);
    });

    it('should handle course with no duration', (done) => {
      const dtoWithoutDuration = { ...mockCourseDTO, duration: 0 };

      service.getCourseProfile(1).subscribe((course) => {
        expect(course.duration).toBe('N/A');
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/courses/1`);
      req.flush(dtoWithoutDuration);
    });

    it('should handle course with no courseType', (done) => {
      const dtoWithoutType = { ...mockCourseDTO, courseType: '' };

      service.getCourseProfile(1).subscribe((course) => {
        expect(course.level).toBe('General');
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/courses/1`);
      req.flush(dtoWithoutType);
    });

    it('should handle course with no language', (done) => {
      const dtoWithoutLanguage = { ...mockCourseDTO, language: '' };

      service.getCourseProfile(1).subscribe((course) => {
        expect(course.language).toBe('Not specified');
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/courses/1`);
      req.flush(dtoWithoutLanguage);
    });

    it('should handle course with no credits', (done) => {
      const dtoWithoutCredits = { ...mockCourseDTO, credits: 0 };

      service.getCourseProfile(1).subscribe((course) => {
        expect(course.credits).toBe(0);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/courses/1`);
      req.flush(dtoWithoutCredits);
    });

    it('should handle course with no minAdmissionGrade', (done) => {
      const dtoWithoutGrade = { ...mockCourseDTO, minAdmissionGrade: 0 };

      service.getCourseProfile(1).subscribe((course) => {
        expect(course.requirements).toEqual([]);
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/courses/1`);
      req.flush(dtoWithoutGrade);
    });
  });

  describe('getFavoriteCourses', () => {
    it('should fetch favorite courses for a user', (done) => {
      const userId = 1;
      const mockResponse = {
        courses: [{ id: 1 }, { id: 2 }, { id: 3 }],
      };

      service.getFavoriteCourses(userId).subscribe((courseIds) => {
        expect(courseIds).toEqual([1, 2, 3]);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites?userId=${userId}`,
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('userId')).toBe(userId.toString());
      req.flush(mockResponse);
    });

    it('should handle empty favorite courses list', (done) => {
      const userId = 1;
      const mockResponse = {
        courses: [],
      };

      service.getFavoriteCourses(userId).subscribe((courseIds) => {
        expect(courseIds).toEqual([]);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites?userId=${userId}`,
      );
      req.flush(mockResponse);
    });
  });

  describe('addFavoriteCourse', () => {
    it('should add course to favorites', (done) => {
      const courseId = 123;
      spyOn(localStorage, 'getItem').and.returnValue('1');

      service.addFavoriteCourse(courseId).subscribe(() => {
        expect(true).toBe(true);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites/courses/${courseId}?userId=1`,
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.params.get('userId')).toBe('1');
      expect(req.request.body).toEqual({});
      req.flush(null);
    });

    it('should throw error when user not logged in', () => {
      spyOn(localStorage, 'getItem').and.returnValue(null);

      expect(() => service.addFavoriteCourse(123)).toThrowError(
        'User not logged in',
      );
    });
  });

  describe('removeFavoriteCourse', () => {
    it('should remove course from favorites', (done) => {
      const courseId = 123;
      spyOn(localStorage, 'getItem').and.returnValue('1');

      service.removeFavoriteCourse(courseId).subscribe(() => {
        expect(true).toBe(true);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites/courses/${courseId}?userId=1`,
      );
      expect(req.request.method).toBe('DELETE');
      expect(req.request.params.get('userId')).toBe('1');
      req.flush(null);
    });

    it('should throw error when user not logged in', () => {
      spyOn(localStorage, 'getItem').and.returnValue(null);

      expect(() => service.removeFavoriteCourse(123)).toThrowError(
        'User not logged in',
      );
    });
  });
});
