import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { ProfilePageService } from './profile-page-service';
import { UserViewmodel, FavoritesResponse } from '../viewmodels/user-viewmodel';
import { EditProfileRequest } from '../viewmodels/edit-profile-request';
import { environment } from '../../../environments/environment';

describe('ProfilePageService', () => {
  let service: ProfilePageService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  const mockUser: UserViewmodel = {
    id: 123,
    name: 'John Doe',
    age: 30,
    gender: 'MALE',
    location: 'New York',
    profileImage: 'image.jpg',
    jobTitle: 'Developer',
    academicHistory: [],
    role: 'USER',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProfilePageService],
    });

    service = TestBed.inject(ProfilePageService);
    httpMock = TestBed.inject(HttpTestingController);

    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getUserProfile', () => {
    it('should retrieve user profile by id', () => {
      service.getUserProfile(123).subscribe((user) => {
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
      const anotherUser: UserViewmodel = {
        id: 456,
        name: 'Jane Smith',
        age: 25,
        gender: 'FEMALE',
        location: 'Paris',
        profileImage: 'profile.jpg',
        jobTitle: 'Manager',
        academicHistory: [],
        role: 'USER',
      };

      service.getUserProfile(456).subscribe((user) => {
        expect(user.id).toBe(456);
        expect(user.name).toBe('Jane Smith');
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile/456`);
      expect(req.request.method).toBe('GET');
      req.flush(anotherUser);
    });

    it('should handle error when retrieving user profile', () => {
      const errorMessage = 'User not found';

      service.getUserProfile(999).subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.error).toBe(errorMessage);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile/999`);
      req.flush(errorMessage, { status: 404, statusText: 'Not Found' });
    });

    it('should send credentials with the request', () => {
      service.getUserProfile(1).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/1`);
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockUser);
    });
  });

  describe('getOwnProfile', () => {
    it('should retrieve current user profile', () => {
      service.getOwnProfile().subscribe((user) => {
        expect(user).toEqual(mockUser);
        expect(user.name).toBe('John Doe');
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile`);
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockUser);
    });

    it('should handle error when retrieving own profile', () => {
      service.getOwnProfile().subscribe({
        next: () => fail('should have failed with 401 error'),
        error: (error) => {
          expect(error.status).toBe(401);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile`);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });
  });

  describe('getOwnFavorites', () => {
    it('should retrieve favorites when userId exists in localStorage', () => {
      localStorage.setItem('userId', '123');

      const mockFavorites: FavoritesResponse = {
        universities: [
          {
            id: 1,
            name: 'Oxford University',
            description: 'Test',
            location: {
              id: 1,
              city: 'Oxford',
              country: 'UK',
              costOfLiving: 1500,
            },
          },
        ],
        courses: [
          {
            id: 1,
            name: 'Computer Science',
            courseType: 'Bachelor',
          },
        ],
      };

      service.getOwnFavorites().subscribe((favorites) => {
        expect(favorites).toEqual(mockFavorites);
        expect(favorites.universities.length).toBe(1);
        expect(favorites.courses.length).toBe(1);
      });

      const req = httpMock.expectOne(
        (request) =>
          request.url === `${apiUrl}/api/favorites` &&
          request.params.get('userId') === '123',
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.withCredentials).toBe(true);
      req.flush(mockFavorites);
    });

    it('should return empty favorites when userId is not in localStorage', (done) => {
      service.getOwnFavorites().subscribe((favorites) => {
        expect(favorites.universities).toEqual([]);
        expect(favorites.courses).toEqual([]);
        done();
      });

      httpMock.expectNone(`${apiUrl}/api/favorites`);
    });

    it('should handle error when retrieving favorites', () => {
      localStorage.setItem('userId', '123');

      service.getOwnFavorites().subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error) => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne(
        (request) => request.url === `${apiUrl}/api/favorites`,
      );
      req.flush('Server error', {
        status: 500,
        statusText: 'Internal Server Error',
      });
    });
  });

  describe('addFavoriteUniversity', () => {
    it('should add favorite university when userId exists', () => {
      localStorage.setItem('userId', '123');

      service.addFavoriteUniversity(1).subscribe();

      const req = httpMock.expectOne(
        (request) =>
          request.url === `${apiUrl}/api/favorites/universities/1` &&
          request.params.get('userId') === '123',
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.withCredentials).toBe(true);
      req.flush(null);
    });

    it('should not make request when userId is not in localStorage', (done) => {
      service.addFavoriteUniversity(1).subscribe(() => {
        done();
      });

      httpMock.expectNone(`${apiUrl}/api/favorites/universities/1`);
    });
  });

  describe('removeFavoriteUniversity', () => {
    it('should remove favorite university when userId exists', () => {
      localStorage.setItem('userId', '123');

      service.removeFavoriteUniversity(1).subscribe();

      const req = httpMock.expectOne(
        (request) =>
          request.url === `${apiUrl}/api/favorites/universities/1` &&
          request.params.get('userId') === '123',
      );
      expect(req.request.method).toBe('DELETE');
      expect(req.request.withCredentials).toBe(true);
      req.flush(null);
    });

    it('should not make request when userId is not in localStorage', (done) => {
      service.removeFavoriteUniversity(1).subscribe(() => {
        done();
      });

      httpMock.expectNone(`${apiUrl}/api/favorites/universities/1`);
    });
  });

  describe('addFavoriteCourse', () => {
    it('should add favorite course when userId exists', () => {
      localStorage.setItem('userId', '123');

      service.addFavoriteCourse(1).subscribe();

      const req = httpMock.expectOne(
        (request) =>
          request.url === `${apiUrl}/api/favorites/courses/1` &&
          request.params.get('userId') === '123',
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.withCredentials).toBe(true);
      req.flush(null);
    });

    it('should not make request when userId is not in localStorage', (done) => {
      service.addFavoriteCourse(1).subscribe(() => {
        done();
      });

      httpMock.expectNone(`${apiUrl}/api/favorites/courses/1`);
    });
  });

  describe('removeFavoriteCourse', () => {
    it('should remove favorite course when userId exists', () => {
      localStorage.setItem('userId', '123');

      service.removeFavoriteCourse(1).subscribe();

      const req = httpMock.expectOne(
        (request) =>
          request.url === `${apiUrl}/api/favorites/courses/1` &&
          request.params.get('userId') === '123',
      );
      expect(req.request.method).toBe('DELETE');
      expect(req.request.withCredentials).toBe(true);
      req.flush(null);
    });

    it('should not make request when userId is not in localStorage', (done) => {
      service.removeFavoriteCourse(1).subscribe(() => {
        done();
      });

      httpMock.expectNone(`${apiUrl}/api/favorites/courses/1`);
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
        jobTitle: 'Senior Developer',
      };

      const mockResponse: UserViewmodel = {
        id: 123,
        name: 'Updated Name',
        age: 31,
        gender: 'MALE',
        location: 'Boston',
        profileImage: 'image.jpg',
        jobTitle: 'Senior Developer',
        academicHistory: [],
        role: 'USER',
      };

      service.updateProfile(updateRequest).subscribe((user) => {
        expect(user).toEqual(mockResponse);
        expect(user.name).toBe('Updated Name');
        expect(user.jobTitle).toBe('Senior Developer');
      });

      const req = httpMock.expectOne(
        `${apiUrl}/api/profile/123/update-profile`,
      );
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
        jobTitle: 'Director',
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(
        `${apiUrl}/api/profile/456/update-profile`,
      );
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
        jobTitle: null,
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(
        `${apiUrl}/api/profile/789/update-profile`,
      );
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
        jobTitle: 'Job',
      };

      const errorMessage = 'Update failed';

      service.updateProfile(updateRequest).subscribe({
        next: () => fail('should have failed with 400 error'),
        error: (error) => {
          expect(error.status).toBe(400);
          expect(error.error).toBe(errorMessage);
        },
      });

      const req = httpMock.expectOne(
        `${apiUrl}/api/profile/123/update-profile`,
      );
      req.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });

    it('should not send credentials with the update request', () => {
      const updateRequest: EditProfileRequest = {
        id: 1,
        name: 'Test',
        age: 20,
        gender: 'MALE',
        location: 'Test City',
        jobTitle: 'Tester',
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/1/update-profile`);
      expect(req.request.withCredentials).toBeFalsy();
      req.flush({} as UserViewmodel);
    });

    it('should handle server error (500)', () => {
      const updateRequest: EditProfileRequest = {
        id: 123,
        name: 'Test',
        age: 25,
        gender: 'MALE',
        location: 'City',
        jobTitle: 'Job',
      };

      service.updateProfile(updateRequest).subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error) => {
          expect(error.status).toBe(500);
        },
      });

      const req = httpMock.expectOne(
        `${apiUrl}/api/profile/123/update-profile`,
      );
      req.flush('Server error', {
        status: 500,
        statusText: 'Internal Server Error',
      });
    });

    it('should use PUT method for updates', () => {
      const updateRequest: EditProfileRequest = {
        id: 1,
        name: 'Test',
        age: 20,
        gender: 'MALE',
        location: 'City',
        jobTitle: 'Job',
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/1/update-profile`);
      expect(req.request.method).toBe('PUT');
      req.flush({} as UserViewmodel);
    });
  });

  describe('changePassword', () => {
    it('should change password successfully', () => {
      const userId = 1;
      const data = { currentPassword: 'old', newPassword: 'new' };

      service.changePassword(userId, data).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/${userId}/password`);
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(data);
      expect(req.request.withCredentials).toBe(true);
      req.flush(null);
    });

    it('should handle error when changing password', () => {
      const userId = 1;
      const data = { currentPassword: 'old', newPassword: 'new' };

      service.changePassword(userId, data).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.status).toBe(400);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile/${userId}/password`);
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('deleteAccount', () => {
    it('should delete account successfully', () => {
      service.deleteAccount(123).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/delete/123`);
      expect(req.request.method).toBe('DELETE');
      expect(req.request.withCredentials).toBe(true);
      req.flush(null);
    });

    it('should handle error when deleting account', () => {
      service.deleteAccount(123).subscribe({
        next: () => fail('should have failed with 403 error'),
        error: (error) => {
          expect(error.status).toBe(403);
        },
      });

      const req = httpMock.expectOne(`${apiUrl}/api/profile/delete/123`);
      req.flush('Forbidden', { status: 403, statusText: 'Forbidden' });
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

    it('should construct correct endpoint for getOwnProfile', () => {
      service.getOwnProfile().subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile`);
      expect(req.request.url).toBe(`${apiUrl}/api/profile`);
      req.flush({} as UserViewmodel);
    });

    it('should construct correct endpoint for updateProfile', () => {
      const updateRequest: EditProfileRequest = {
        id: 1,
        name: 'Test',
        age: 20,
        gender: 'MALE',
        location: 'City',
        jobTitle: 'Job',
      };

      service.updateProfile(updateRequest).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/1/update-profile`);
      expect(req.request.url).toBe(`${apiUrl}/api/profile/1/update-profile`);
      req.flush({} as UserViewmodel);
    });

    it('should construct correct endpoint for deleteAccount', () => {
      service.deleteAccount(1).subscribe();

      const req = httpMock.expectOne(`${apiUrl}/api/profile/delete/1`);
      expect(req.request.url).toBe(`${apiUrl}/api/profile/delete/1`);
      req.flush(null);
    });
  });
});
