import { TestBed } from '@angular/core/testing';
import { UniversityPageService } from './university-page-service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';

describe('UniversityPageService', () => {
  let service: UniversityPageService;
  let httpMock: HttpTestingController;
  const baseUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UniversityPageService],
    });

    service = TestBed.inject(UniversityPageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getUniversityProfile should request DTO and map to viewmodel (full DTO)', async () => {
    const dto = {
      id: 5,
      name: 'Example University',
      description: 'A great place',
      contactInfo: 'contact@example.com',
      website: 'https://example.edu',
      address: '123 Example St',
      logo: 'https://logo.example/1.png',
      location: { id: 2, city: 'CityX', country: 'CountryY', costOfLiving: 1000 },
      courses: [
        { id: 11, name: 'Course A', courseType: 'Bachelor' },
        { id: 12, name: 'Course B', courseType: 'Master' },
      ],
      scholarships: [{ id: 1, name: 'S1', description: 'd', amount: 1000, courseType: 'Bachelor' }],
    } as any;

    const resultPromise = firstValueFrom(service.getUniversityProfile(5));

    const req = httpMock.expectOne(`${baseUrl}/api/university/5`);
    expect(req.request.method).toBe('GET');
    req.flush(dto);

    const vm = await resultPromise;

    expect(vm.id).toBe(5);
    expect(vm.name).toBe('Example University');
    expect(vm.location).toBe('CityX, CountryY');
    expect(vm.logo).toBe(dto.logo);
    expect(vm.bannerImage).toContain('images.unsplash.com');
    expect(vm.description).toBe('A great place');
    expect(vm.courses.length).toBe(2);
    expect(vm.courses[0].area).toBe('Bachelor');
    expect(vm.courses[0].university.id).toBe(5);
    expect(vm.scholarships.length).toBe(1);
  });

  it('mapToViewmodel should handle missing/empty fields', async () => {
    const dto = {
      id: 7,
      name: 'No Location Uni',
      description: null,
      contactInfo: null,
      website: null,
      address: null,
      logo: '',
      location: null,
      courses: [
        { id: 21, name: 'Course X', courseType: '' },
      ],
      scholarships: null,
    } as any;

    const resultPromise = firstValueFrom(service.getUniversityProfile(7));

    const req = httpMock.expectOne(`${baseUrl}/api/university/7`);
    expect(req.request.method).toBe('GET');
    req.flush(dto);

    const vm = await resultPromise;

    expect(vm.location).toBe('Location not available');
    expect(vm.description).toBe('No description available');
    expect(vm.logo).toContain('https://via.placeholder.com');
    expect(vm.courses[0].area).toBe('General');
    expect(vm.scholarships).toEqual([]);
    expect(vm.address).toBe('N/A');
    expect(vm.contactInfo).toBe('N/A');
    expect(vm.website).toBe('N/A');
  });
});
