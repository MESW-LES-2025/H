import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { ExploreService } from './explore-service';
import { environment } from '../../../environments/environment';
import { UniversityDTO } from '../viewmodels/explore-viewmodel';
import { Page } from '../../shared/viewmodels/pagination';

describe('ExploreService', () => {
  let service: ExploreService;
  let httpMock: HttpTestingController;

  const mockUniversityDTO: UniversityDTO = {
    id: 1,
    name: 'Test University',
    description: 'A great university',
    location: {
      id: 1,
      city: 'New York',
      country: 'USA',
      costOfLiving: 2000,
    },
  };

  const mockPage: Page<UniversityDTO> = {
    content: [mockUniversityDTO],
    totalElements: 1,
    totalPages: 1,
    size: 3,
    number: 0,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ExploreService],
    });

    service = TestBed.inject(ExploreService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('search', () => {
    it('should search with basic pagination', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'Any', null, 'Any', pageRequest).subscribe((page) => {
        expect(page.content.length).toBe(1);
        expect(page.content[0].title).toBe('Test University');
        expect(page.content[0].id).toBe('1');
        expect(page.totalPages).toBe(1);
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('page') === '0' &&
          request.params.get('size') === '3'
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include sort parameter when provided', (done) => {
      const pageRequest = { page: 0, size: 3, sort: 'name,asc' };

      service.search('', 'Any', null, 'Any', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('sort') === 'name,asc'
        );
      });

      req.flush(mockPage);
    });

    it('should include query parameter when provided', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service
        .search('Harvard', 'Any', null, 'Any', pageRequest)
        .subscribe(() => {
          done();
        });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('name') === 'Harvard'
        );
      });

      req.flush(mockPage);
    });

    it('should trim query parameter', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service
        .search('  Harvard  ', 'Any', null, 'Any', pageRequest)
        .subscribe(() => {
          done();
        });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('name') === 'Harvard'
        );
      });

      req.flush(mockPage);
    });

    it('should not include query parameter when empty', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'Any', null, 'Any', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          !request.params.has('name')
        );
      });

      req.flush(mockPage);
    });

    it('should include country parameter when not "Any"', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'USA', null, 'Any', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('countries') === 'USA'
        );
      });

      req.flush(mockPage);
    });

    it('should not include country parameter when "Any"', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'Any', null, 'Any', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          !request.params.has('countries')
        );
      });

      req.flush(mockPage);
    });

    it('should include costMax parameter when provided', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'Any', 3000, 'Any', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('costOfLivingMax') === '3000'
        );
      });

      req.flush(mockPage);
    });

    it('should not include costMax parameter when null', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'Any', null, 'Any', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          !request.params.has('costOfLivingMax')
        );
      });

      req.flush(mockPage);
    });

    it('should include hasScholarship as true when scholarship is "Yes"', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'Any', null, 'Yes', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('hasScholarship') === 'true'
        );
      });

      req.flush(mockPage);
    });

    it('should include hasScholarship as false when scholarship is "No"', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'Any', null, 'No', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('hasScholarship') === 'false'
        );
      });

      req.flush(mockPage);
    });

    it('should not include hasScholarship when scholarship is "Any"', (done) => {
      const pageRequest = { page: 0, size: 3 };

      service.search('', 'Any', null, 'Any', pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          !request.params.has('hasScholarship')
        );
      });

      req.flush(mockPage);
    });

    it('should include all parameters when provided', (done) => {
      const pageRequest = { page: 1, size: 5, sort: 'name,desc' };

      service
        .search('Harvard', 'USA', 3000, 'Yes', pageRequest)
        .subscribe(() => {
          done();
        });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/university` &&
          request.params.get('page') === '1' &&
          request.params.get('size') === '5' &&
          request.params.get('sort') === 'name,desc' &&
          request.params.get('name') === 'Harvard' &&
          request.params.get('countries') === 'USA' &&
          request.params.get('costOfLivingMax') === '3000' &&
          request.params.get('hasScholarship') === 'true'
        );
      });

      req.flush(mockPage);
    });
  });

  describe('toCollegeVM', () => {
    it('should map UniversityDTO to CollegeVM', () => {
      const result = service.toCollegeVM(mockUniversityDTO);

      expect(result.id).toBe('1');
      expect(result.title).toBe('Test University');
      expect(result.blurb).toBe('A great university');
      expect(result.country).toBe('USA');
      expect(result.city).toBe('New York');
      expect(result.costOfLiving).toBe(2000);
      expect(result.isFavorite).toBe(false);
      expect(result.color).toBe('#7DB19F');
    });

    it('should handle missing description', () => {
      const dtoWithoutDescription: UniversityDTO = {
        ...mockUniversityDTO,
        description: '',
      };

      const result = service.toCollegeVM(dtoWithoutDescription);

      expect(result.blurb).toBe('No description available');
    });

    it('should handle missing location', () => {
      const dtoWithoutLocation: UniversityDTO = {
        ...mockUniversityDTO,
        location: null,
      };

      const result = service.toCollegeVM(dtoWithoutLocation);

      expect(result.country).toBe('Unknown');
      expect(result.city).toBe('Unknown');
      expect(result.costOfLiving).toBe(0);
    });
  });

  describe('addFavoriteUniversity', () => {
    it('should add university to favorites', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue('1');

      service.addFavoriteUniversity(123).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites/universities/123?userId=1`,
      );
      expect(req.request.method).toBe('POST');
      expect(req.request.params.get('userId')).toBe('1');
      expect(req.request.body).toEqual({});
      req.flush(null);
    });

    it('should throw error when user not logged in', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue(null);

      service.addFavoriteUniversity(123).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('User not logged in');
          done();
        },
      });
    });
  });

  describe('removeFavoriteUniversity', () => {
    it('should remove university from favorites', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue('1');

      service.removeFavoriteUniversity(123).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites/universities/123?userId=1`,
      );
      expect(req.request.method).toBe('DELETE');
      expect(req.request.params.get('userId')).toBe('1');
      req.flush(null);
    });

    it('should throw error when user not logged in', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue(null);

      service.removeFavoriteUniversity(123).subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('User not logged in');
          done();
        },
      });
    });
  });

  describe('getFavorites', () => {
    it('should get user favorites', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue('1');
      const mockFavorites = {
        universities: [{ id: 1 }, { id: 2 }],
        courses: [{ id: 3 }],
      };

      service.getFavorites().subscribe((favorites) => {
        expect(favorites.universities.length).toBe(2);
        expect(favorites.courses.length).toBe(1);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites?userId=1`,
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('userId')).toBe('1');
      req.flush(mockFavorites);
    });

    it('should throw error when user not logged in', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue(null);

      service.getFavorites().subscribe({
        next: () => fail('should have failed'),
        error: (error) => {
          expect(error.message).toBe('User not logged in');
          done();
        },
      });
    });
  });

  describe('getFavoriteUniversities', () => {
    it('should get favorite university IDs', (done) => {
      const mockResponse = {
        universities: [{ id: 1 }, { id: 2 }, { id: 3 }],
      };

      service.getFavoriteUniversities(1).subscribe((ids) => {
        expect(ids).toEqual([1, 2, 3]);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites?userId=1`,
      );
      expect(req.request.method).toBe('GET');
      expect(req.request.params.get('userId')).toBe('1');
      req.flush(mockResponse);
    });

    it('should handle empty favorites list', (done) => {
      const mockResponse = {
        universities: [],
      };

      service.getFavoriteUniversities(1).subscribe((ids) => {
        expect(ids).toEqual([]);
        done();
      });

      const req = httpMock.expectOne(
        `${environment.apiUrl}/api/favorites?userId=1`,
      );
      req.flush(mockResponse);
    });
  });
});
