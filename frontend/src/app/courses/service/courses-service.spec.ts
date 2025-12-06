import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { CoursesService } from './courses-service';
import { environment } from '../../../environments/environment';
import { CourseFilters } from '../viewmodels/course-filters';
import { PageRequest, Page } from '../../shared/viewmodels/pagination';
import { CourseViewmodel } from '../viewmodels/course-viewmodel';

describe('CoursesService', () => {
  let service: CoursesService;
  let httpMock: HttpTestingController;

  const mockCourses: CourseViewmodel[] = [
    {
      id: 1,
      name: 'Computer Science',
      description: 'CS program',
      courseType: 'Bachelor',
      isRemote: false,
      minAdmissionGrade: 85,
      cost: 10000,
      duration: '4 years',
      credits: 180,
      language: 'English',
      startDate: '2025-09-01',
      applicationDeadline: '2025-06-01',
      website: 'https://example.com',
      contactEmail: 'contact@example.com',
      university: {
        id: 1,
        name: 'Test University',
        location: {
          id: 1,
          city: 'Test City',
          country: 'Test Country',
        },
        logo: 'https://example.com/logo.png',
      },
      areasOfStudy: [{ id: 1, name: 'Computer Science' }],
    },
  ];

  const mockPage: Page<CourseViewmodel> = {
    content: mockCourses,
    totalElements: 1,
    totalPages: 1,
    size: 10,
    number: 0,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CoursesService],
    });

    service = TestBed.inject(CoursesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCourses', () => {
    it('should fetch courses with basic pagination', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe((page) => {
        expect(page).toEqual(mockPage);
        expect(page.content.length).toBe(1);
        expect(page.content[0].name).toBe('Computer Science');
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          request.params.get('page') === '0' &&
          request.params.get('size') === '10'
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include sort parameter when provided', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
        sort: 'name,asc',
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          request.params.get('sort') === 'name,asc'
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include name filter when provided', (done) => {
      const filters: CourseFilters = {
        name: 'Computer',
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          request.params.get('name') === 'Computer'
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should not include empty name filter', (done) => {
      const filters: CourseFilters = {
        name: '',
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          !request.params.has('name')
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include courseTypes filter when provided', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: ['Bachelor', 'Master'],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        const courseTypes = request.params.getAll('courseTypes');
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          courseTypes?.length === 2 &&
          courseTypes.includes('Bachelor') &&
          courseTypes.includes('Master')
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include onlyRemote filter when true', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: true,
        costMax: null,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          request.params.get('onlyRemote') === 'true'
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include onlyRemote filter when false', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          request.params.get('onlyRemote') === 'false'
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include costMax filter when provided', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: 15000,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          request.params.get('maxCost') === '15000'
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include duration filter when provided', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: 24,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          request.params.get('duration') === '24'
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include languages filter when provided', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: ['English', 'Spanish'],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        const languages = request.params.getAll('languages');
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          languages?.length === 2 &&
          languages.includes('English') &&
          languages.includes('Spanish')
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include countries filter when provided', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: [],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: [],
        countries: ['USA', 'UK'],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        const countries = request.params.getAll('countries');
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          countries?.length === 2 &&
          countries.includes('USA') &&
          countries.includes('UK')
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include areasOfStudy filter when provided', (done) => {
      const filters: CourseFilters = {
        name: null,
        courseTypes: [],
        areasOfStudy: ['Computer Science', 'Engineering'],
        onlyRemote: false,
        costMax: null,
        duration: null,
        languages: [],
        countries: [],
      };

      const pageRequest: PageRequest = {
        page: 0,
        size: 10,
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        const areasOfStudy = request.params.getAll('areasOfStudy');
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          areasOfStudy?.length === 2 &&
          areasOfStudy.includes('Computer Science') &&
          areasOfStudy.includes('Engineering')
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });

    it('should include all filters when provided', (done) => {
      const filters: CourseFilters = {
        name: 'Computer',
        courseTypes: ['Bachelor'],
        areasOfStudy: ['Computer Science'],
        onlyRemote: true,
        costMax: 15000,
        duration: 48,
        languages: ['English'],
        countries: ['USA'],
      };

      const pageRequest: PageRequest = {
        page: 1,
        size: 20,
        sort: 'name,desc',
      };

      service.getCourses(filters, pageRequest).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne((request) => {
        return (
          request.url === `${environment.apiUrl}/api/courses` &&
          request.params.get('page') === '1' &&
          request.params.get('size') === '20' &&
          request.params.get('sort') === 'name,desc' &&
          request.params.get('name') === 'Computer' &&
          request.params.getAll('courseTypes')!.includes('Bachelor') &&
          request.params.getAll('areasOfStudy')!.includes('Computer Science') &&
          request.params.get('onlyRemote') === 'true' &&
          request.params.get('maxCost') === '15000' &&
          request.params.get('duration') === '48' &&
          request.params.getAll('languages')!.includes('English') &&
          request.params.getAll('countries')!.includes('USA')
        );
      });

      expect(req.request.method).toBe('GET');
      req.flush(mockPage);
    });
  });
});
