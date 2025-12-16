import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { AdminService } from './admin.service';
import { environment } from '../../environments/environment';
import { UserViewmodel } from '../profile-page/viewmodels/user-viewmodel';
import { UniversityLight } from '../universities/viewmodels/university-light';
import { CourseLight } from '../shared/viewmodels/course-light';
import { Review } from '../university-page/viewmodels/review';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AdminService],
    });

    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getUsers() should perform GET and return users', (done) => {
    const mockUsers: UserViewmodel[] = [
      {
        id: 1,
        name: 'Alice',
        email: 'john.doe@example.com',
        age: 25,
        gender: 'FEMALE',
        location: 'City A',
        profileImage: '',
        jobTitle: 'Student',
        academicHistory: [],
        userRole: 'REGULAR',
      },
      {
        id: 2,
        name: 'Bob',
        email: 'bob@example.com',
        age: 30,
        gender: 'MALE',
        location: 'City B',
        profileImage: '',
        jobTitle: 'Engineer',
        academicHistory: [],
        userRole: 'ADMIN',
      },
    ];

    service.getUsers().subscribe((res) => {
      expect(res).toEqual(mockUsers);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/admin/users`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUsers);
  });

  it('getUniversities() should perform GET and return universities', (done) => {
    const mockUnis: UniversityLight[] = [
      {
        id: 10,
        name: 'Uni A',
        description: 'Desc A',
        location: {
          id: 1,
          city: 'City A',
          country: 'Country A',
          costOfLiving: 1000,
        },
      },
      {
        id: 11,
        name: 'Uni B',
        description: 'Desc B',
        location: {
          id: 2,
          city: 'City B',
          country: 'Country B',
          costOfLiving: 1000,
        },
      },
    ];

    service.getUniversities().subscribe((res) => {
      expect(res).toEqual(mockUnis);
      done();
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/admin/universities`,
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockUnis);
  });

  it('getCourses() should perform GET and return courses', (done) => {
    const mockCourses: CourseLight[] = [
      {
        id: 100,
        name: 'Course 1',
        courseType: 'BACHELOR',
        universityName: 'Uni A',
      },
      {
        id: 101,
        name: 'Course 2',
        courseType: 'MASTER',
        universityName: 'Uni B',
      },
    ];

    service.getCourses().subscribe((res) => {
      expect(res).toEqual(mockCourses);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/admin/courses`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCourses);
  });

  it('getReviews() should perform GET and return reviews', (done) => {
    const mockReviews: Review[] = [
      {
        id: 1,
        userId: 1,
        userName: 'Alice',
        universityId: 10,
        courseId: undefined,
        rating: 5,
        title: 'Great course!',
        description: 'Really enjoyed it',
        reviewDate: '2025-01-01',
      },
      {
        id: 2,
        userId: 2,
        userName: 'Bob',
        universityId: undefined,
        courseId: 101,
        rating: 3,
        title: 'Not bad',
        description: 'Could be better',
        reviewDate: '2025-01-02',
      },
    ];

    service.getReviews().subscribe((res) => {
      expect(res).toEqual(mockReviews);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/admin/reviews`);
    expect(req.request.method).toBe('GET');
    req.flush(mockReviews);
  });

  it('getAll() should call all four endpoints and return aggregated result', (done) => {
    const mockUsers: UserViewmodel[] = [
      {
        id: 1,
        name: 'Alice',
        email: 'john.doe@example.com',
        age: 25,
        gender: 'FEMALE',
        location: 'City A',
        profileImage: '',
        jobTitle: 'Student',
        academicHistory: [],
        userRole: 'REGULAR',
      },
    ];
    const mockUnis: UniversityLight[] = [
      {
        id: 10,
        name: 'Uni A',
        description: '',
        location: {
          id: 1,
          city: 'City A',
          country: 'Country A',
          costOfLiving: 1000,
        },
      },
    ];
    const mockCourses: CourseLight[] = [
      {
        id: 100,
        name: 'Course 1',
        courseType: 'BACHELOR',
        universityName: 'Uni A',
      },
    ];
    const mockReviews = [
      {
        id: 1,
        userId: 1,
        userName: 'Alice',
        universityId: 10,
        courseId: undefined,
        rating: 5,
        title: 'Great!',
        description: 'Awesome university',
        reviewDate: '2025-01-01',
      },
    ];

    service.getAll().subscribe((res) => {
      expect(res.users).toEqual(mockUsers);
      expect(res.universities).toEqual(mockUnis);
      expect(res.courses).toEqual(mockCourses);
      expect(res.reviews).toEqual(mockReviews);
      done();
    });

    const reqUsers = httpMock.expectOne(
      `${environment.apiUrl}/api/admin/users`,
    );
    expect(reqUsers.request.method).toBe('GET');
    reqUsers.flush(mockUsers);

    const reqUnis = httpMock.expectOne(
      `${environment.apiUrl}/api/admin/universities`,
    );
    expect(reqUnis.request.method).toBe('GET');
    reqUnis.flush(mockUnis);

    const reqCourses = httpMock.expectOne(
      `${environment.apiUrl}/api/admin/courses`,
    );
    expect(reqCourses.request.method).toBe('GET');
    reqCourses.flush(mockCourses);

    const reqReviews = httpMock.expectOne(
      `${environment.apiUrl}/api/admin/reviews`,
    );
    expect(reqReviews.request.method).toBe('GET');
    reqReviews.flush(mockReviews);
  });

  it('deleteUser() should call DELETE and return void', (done) => {
    service.deleteUser(5).subscribe(() => {
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/admin/users/5`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null, { status: 204, statusText: 'No Content' });
  });
});
