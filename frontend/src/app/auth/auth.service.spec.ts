import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService, LoginRequest, RegisterRequest } from './auth.service';
import { environment } from '../../environments/environment';
import { UserViewmodel } from '../profile-page/viewmodels/user-viewmodel';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let router: jasmine.SpyObj<Router>;

  const mockUser: UserViewmodel = {
    id: 1,
    name: 'Test User',
    email: 'john.doe@example.com',
    age: 25,
    gender: 'Male',
    location: 'Test City',
    profileImage: 'test.jpg',
    jobTitle: 'Developer',
    academicHistory: [],
    userRole: 'USER',
  };

  beforeEach(() => {
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService, { provide: Router, useValue: routerSpy }],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    // Handle the restoreSession call made in constructor
    const restoreReq = httpMock.expectOne(`${environment.apiUrl}/api/auth/me`);
    expect(restoreReq.request.method).toBe('GET');
    restoreReq.flush(null);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Constructor and Session Restoration', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should restore session with valid user on initialization', () => {
      // Create a new service instance to test constructor behavior
      const httpClient = TestBed.inject(HttpClient);
      const newService = new AuthService(httpClient, router);

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/me`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);

      req.flush({ id: 1 });

      newService.currentUser$.subscribe((user) => {
        expect(user).toEqual(jasmine.objectContaining({ id: 1 }));
      });
    });

    it('should set currentUser to null when session restoration returns invalid user', () => {
      const httpClient = TestBed.inject(HttpClient);
      const newService = new AuthService(httpClient, router);

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/me`);
      req.flush({});

      newService.currentUser$.subscribe((user) => {
        expect(user).toBeNull();
      });
    });

    it('should set currentUser to null when session restoration fails', () => {
      const httpClient = TestBed.inject(HttpClient);
      const newService = new AuthService(httpClient, router);

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/me`);
      req.error(new ProgressEvent('error'));

      newService.currentUser$.subscribe((user) => {
        expect(user).toBeNull();
      });
    });
  });

  describe('login', () => {
    it('should successfully login and update currentUser', (done) => {
      const loginRequest: LoginRequest = {
        text: 'testuser',
        password: 'password123',
      };

      const loginResponse = {
        message: 'Login successful',
        status: 'success',
        user: mockUser,
      };

      service.login(loginRequest).subscribe((response) => {
        expect(response).toEqual(loginResponse);
        expect(response.status).toBe('success');

        service.currentUser$.subscribe((user) => {
          expect(user).toEqual(jasmine.objectContaining({ id: mockUser.id }));
          done();
        });
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequest);
      expect(req.request.withCredentials).toBe(true);

      req.flush(loginResponse);
    });

    it('should include CSRF header when set', (done) => {
      const loginRequest: LoginRequest = {
        text: 'testuser',
        password: 'password123',
      };

      const loginResponse = {
        message: 'Login successful',
        status: 'success',
        user: mockUser,
      };

      // set CSRF header values
      service.csrfToken = 'abc-123';
      service.csrfHeaderName = 'X-CSRF-TOKEN';

      service.login(loginRequest).subscribe((response) => {
        expect(response.status).toBe('success');
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('X-CSRF-TOKEN')).toBe('abc-123');
      expect(req.request.withCredentials).toBe(true);

      req.flush(loginResponse);
    });

    it('should not update currentUser on failed login', (done) => {
      const loginRequest: LoginRequest = {
        text: 'testuser',
        password: 'wrongpassword',
      };

      const loginResponse = {
        message: 'Invalid credentials',
        status: 'error',
      };

      // Set initial user state
      service['currentUserSubject'].next({ id: 999 });

      service.login(loginRequest).subscribe((response) => {
        expect(response).toEqual(loginResponse);
        expect(response.status).toBe('error');

        // Current user should remain unchanged
        service.currentUser$.subscribe((user) => {
          expect(user).toEqual(jasmine.objectContaining({ id: 999 }));
          done();
        });
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/login`);
      req.flush(loginResponse);
    });

    it('should not update currentUser when login response has no user', (done) => {
      const loginRequest: LoginRequest = {
        text: 'testuser',
        password: 'password123',
      };

      const loginResponse = {
        message: 'Login successful',
        status: 'success',
      };

      // Set initial state
      const initialUser = { id: 999 };
      service['currentUserSubject'].next(initialUser);

      service.login(loginRequest).subscribe(() => {
        // User should remain unchanged since response has no user
        service.currentUser$.subscribe((user) => {
          expect(user).toEqual(initialUser);
          done();
        });
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/login`);
      req.flush(loginResponse);
    });
  });

  describe('register', () => {
    it('should successfully register a new user', (done) => {
      const registerRequest: RegisterRequest = {
        name: 'Test User',
        username: 'testuser',
        email: 'test@example.com',
        password: 'password123',
      };

      const registerResponse = {
        message: 'Registration successful',
        status: 'success',
      };

      service.register(registerRequest).subscribe((response) => {
        expect(response).toEqual(registerResponse);
        expect(response.status).toBe('success');
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/register`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(registerRequest);

      req.flush(registerResponse);
    });

    it('should handle registration error', (done) => {
      const registerRequest: RegisterRequest = {
        name: 'Test User',
        username: 'existinguser',
        email: 'test@example.com',
        password: 'password123',
      };

      const errorResponse = {
        message: 'Username already exists',
        status: 'error',
      };

      service.register(registerRequest).subscribe((response) => {
        expect(response).toEqual(errorResponse);
        expect(response.status).toBe('error');
        done();
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/register`);
      req.flush(errorResponse);
    });
  });

  describe('getUserById', () => {
    it('should fetch user by ID', (done) => {
      const userId = 1;

      service.getUserById(userId).subscribe((user) => {
        expect(user).toEqual(mockUser);
        expect(user.id).toBe(userId);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/users/${userId}`,
      );
      expect(req.request.method).toBe('GET');

      req.flush(mockUser);
    });

    it('should handle error when fetching user by ID', (done) => {
      const userId = 999;

      service.getUserById(userId).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        },
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/users/${userId}`,
      );
      req.error(new ProgressEvent('error'), { status: 404 });
    });
  });

  describe('updateUser', () => {
    it('should update user data', (done) => {
      const userId = 1;
      const userData = {
        name: 'Updated Name',
        age: 30,
      };

      const updatedUser = { ...mockUser, ...userData };

      service.updateUser(userId, userData).subscribe((response) => {
        expect(response).toEqual(updatedUser);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/users/${userId}`,
      );
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(userData);

      req.flush(updatedUser);
    });

    it('should handle error when updating user', (done) => {
      const userId = 1;
      const userData = { name: 'Updated Name' };

      service.updateUser(userId, userData).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error).toBeTruthy();
          done();
        },
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/users/${userId}`,
      );
      req.error(new ProgressEvent('error'), { status: 400 });
    });
  });

  describe('logout', () => {
    it('should successfully logout and navigate to home', () => {
      // Set a current user
      service['currentUserSubject'].next({ id: 1 });

      service.logout();

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/logout`);
      expect(req.request.method).toBe('POST');
      expect(req.request.withCredentials).toBe(true);

      req.flush({});

      service.currentUser$.subscribe((user) => {
        expect(user).toBeNull();
      });

      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });

    it('should clear currentUser and navigate to home even on logout error', () => {
      // Set a current user
      service['currentUserSubject'].next({ id: 1 });

      service.logout();

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/logout`);
      req.error(new ProgressEvent('error'));

      service.currentUser$.subscribe((user) => {
        expect(user).toBeNull();
      });

      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });
  });

  describe('currentUser$ observable', () => {
    it('should emit current user changes', (done) => {
      const testUser = { id: 123 };
      const emissions: any[] = [];

      service.currentUser$.subscribe((user) => {
        emissions.push(user);

        if (emissions.length === 2) {
          expect(emissions[0]).toBeNull(); // Initial value after constructor
          expect(emissions[1]).toEqual(testUser);
          done();
        }
      });

      service['currentUserSubject'].next(testUser);
    });
  });

  describe('CSRF Token Management', () => {
    it('should allow setting CSRF token', () => {
      service.csrfToken = 'new-token';
      service.csrfHeaderName = 'X-CSRF-Header';

      expect(service.csrfToken).toBe('new-token');
      expect(service.csrfHeaderName).toBe('X-CSRF-Header');
    });

    it('should allow clearing CSRF token', () => {
      service.csrfToken = 'some-token';
      service.csrfHeaderName = 'X-CSRF-Header';

      service.csrfToken = null;
      service.csrfHeaderName = null;

      expect(service.csrfToken).toBeNull();
      expect(service.csrfHeaderName).toBeNull();
    });
  });
});
