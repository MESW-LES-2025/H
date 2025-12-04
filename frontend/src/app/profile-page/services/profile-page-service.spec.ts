import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProfilePageService } from './profile-page-service';
import { UserViewmodel } from '../viewmodels/user-viewmodel';
import { EditProfileRequest } from '../edit-profile/viewmodels/edit-profile-request';
import { environment } from '../../../environments/environment';

describe('ProfilePageService', () => {
  let service: ProfilePageService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProfilePageService]
    });
    
    service = TestBed.inject(ProfilePageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Ensure no outstanding HTTP requests
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getUserProfile', () => {
    it('should retrieve user profile by id', () => {
      const mockUser: UserViewmodel = {
        id: 123,
        name: 'John Doe',
        age: 30,
        gender: 'MALE',
        location: 'New York',
        profileImage: 'image.jpg',
        jobTitle: 'Developer',
        academicHistory: []
      };

      service.getUserProfile(123).subscribe(user => {
        expect(user).toEqual(mockUser);
        expect(user.id).toBe(123);
        expect(user.name).toBe('John Doe');
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile/123`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockUser);
    });

    it('should handle different user IDs', () => {
      const mockUser: UserViewmodel = {
        id: 456,
        name: 'Jane Smith',
        age: 25,
        gender: 'FEMALE',
        location: 'Paris',
        profileImage: 'profile.jpg',
        jobTitle: 'Manager',
        academicHistory: []
      };

      service.getUserProfile(456).subscribe(user => {
        expect(user.id).toBe(456);
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile/456`);
      expect(req.request.method).toBe('GET');
      req.flush(mockUser);
    });

    it('should handle error when retrieving user profile', () => {
      const errorMessage = 'User not found';

      service.getUserProfile(999).subscribe(
        () => fail('should have failed with 404 error'),
        (error) => {
          expect(error.status).toBe(404);
          expect(error.error).toBe(errorMessage);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/api/profile/999`);
      req.flush(errorMessage, { status: 404, statusText: 'Not Found' });
    });

    it('should send credentials with the request', () => {
      const mockUser: UserViewmodel = {
        id: 1,
        name: 'Test',
        age: 20,
        gender: 'OTHER',
        location: 'Tokyo',
        profileImage: 'test.jpg',
        jobTitle: 'Tester',
        academicHistory: []
      };

      service.getUserProfile(1).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/1`);
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockUser);
    });
  });

  describe('updateProfile', () => {
    it('should update user profile successfully', () => {
      const updateRequest: EditProfileRequest = {
        id: 123,
        name: 'Updated Name',
        age: 31,
        gender: 'MALE',
        location: 'Boston',
        jobTitle: 'Senior Developer'
      };

      const mockResponse: UserViewmodel = {
        id: 123,
        name: 'Updated Name',
        age: 31,
        gender: 'MALE',
        location: 'Boston',
        profileImage: 'image.jpg',
        jobTitle: 'Senior Developer',
        academicHistory: []
      };

      service.updateProfile(updateRequest).subscribe(user => {
        expect(user).toEqual(mockResponse);
        expect(user.name).toBe('Updated Name');
        expect(user.jobTitle).toBe('Senior Developer');
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile/123/update-profile`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updateRequest);
      req.flush(mockResponse);
    });

    it('should send correct request body', () => {
      const updateRequest: EditProfileRequest = {
        id: 456,
        name: 'Jane Updated',
        age: 26,
        gender: 'FEMALE',
        location: 'London',
        jobTitle: 'Director'
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/456/update-profile`);
      expect(req.request.body.id).toBe(456);
      expect(req.request.body.name).toBe('Jane Updated');
      expect(req.request.body.age).toBe(26);
      expect(req.request.body.gender).toBe('FEMALE');
      expect(req.request.body.location).toBe('London');
      expect(req.request.body.jobTitle).toBe('Director');
      req.flush({} as UserViewmodel);
    });

    it('should handle update with null optional fields', () => {
      const updateRequest: EditProfileRequest = {
        id: 789,
        name: 'Min User',
        age: null,
        gender: 'OTHER',
        location: null,
        jobTitle: null
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/789/update-profile`);
      expect(req.request.body.age).toBeNull();
      expect(req.request.body.location).toBeNull();
      expect(req.request.body.jobTitle).toBeNull();
      req.flush({} as UserViewmodel);
    });

    it('should handle error when updating profile', () => {
      const updateRequest: EditProfileRequest = {
        id: 123,
        name: 'Test',
        age: 25,
        gender: 'MALE',
        location: 'City',
        jobTitle: 'Job'
      };

      const errorMessage = 'Update failed';

      service.updateProfile(updateRequest).subscribe(
        () => fail('should have failed with 400 error'),
        (error) => {
          expect(error.status).toBe(400);
          expect(error.error).toBe(errorMessage);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/api/profile/123/update-profile`);
      req.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });

    it('should not send credentials with the update request', () => {
      const updateRequest: EditProfileRequest = {
        id: 1,
        name: 'Test',
        age: 20,
        gender: 'MALE',
        location: 'Test City',
        jobTitle: 'Tester'
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/1/update-profile`);
      expect(req.request.withCredentials).toBe(false);
      req.flush({} as UserViewmodel);
    });

    it('should handle server error (500)', () => {
      const updateRequest: EditProfileRequest = {
        id: 123,
        name: 'Test',
        age: 25,
        gender: 'MALE',
        location: 'City',
        jobTitle: 'Job'
      };

      service.updateProfile(updateRequest).subscribe(
        () => fail('should have failed with 500 error'),
        (error) => {
          expect(error.status).toBe(500);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/api/profile/123/update-profile`);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });

    it('should use PUT method for updates', () => {
      const updateRequest: EditProfileRequest = {
        id: 1,
        name: 'Test',
        age: 20,
        gender: 'MALE',
        location: 'City',
        jobTitle: 'Job'
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/1/update-profile`);
      expect(req.request.method).toBe('PUT');
      req.flush({} as UserViewmodel);
    });
  });

  describe('API URL Configuration', () => {
    it('should use correct API URL from environment', () => {
      service.getUserProfile(1).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/api/profile/1`);
      expect(req.request.url).toContain(environment.apiUrl);
      req.flush({} as UserViewmodel);
    });

    it('should construct correct endpoint for getUserProfile', () => {
      service.getUserProfile(999).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/999`);
      expect(req.request.url).toBe(`${apiUrl}/api/profile/999`);
      req.flush({} as UserViewmodel);
    });

    it('should construct correct endpoint for updateProfile', () => {
      const updateRequest: EditProfileRequest = {
        id: 1,
        name: 'Test',
        age: 20,
        gender: 'MALE',
        location: 'City',
        jobTitle: 'Job'
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/1/update-profile`);
      expect(req.request.url).toBe(`${apiUrl}/api/profile/1/update-profile`);
      req.flush({} as UserViewmodel);
    });
  });

});