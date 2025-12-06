import { TestBed } from '@angular/core/testing';
import { DataService } from './data-service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { AreasOfStudy } from '../viewmodels/area-of-study';
import { firstValueFrom } from 'rxjs';
import { skip, take } from 'rxjs/operators';
import { UserViewmodel } from '../../profile-page/viewmodels/user-viewmodel';

describe('DataService', () => {
  let service: DataService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DataService],
    });

    service = TestBed.inject(DataService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('loadFilterLists should request areas, languages and countries and publish results', async () => {
    const areasPromise = firstValueFrom(
      service.areasOfStudy$.pipe(skip(1), take(1)),
    );
    const langsPromise = firstValueFrom(
      service.languages$.pipe(skip(1), take(1)),
    );
    const countriesPromise = firstValueFrom(
      service.countries$.pipe(skip(1), take(1)),
    );

    service.loadFilterLists();

    const reqAreas = httpMock.expectOne((req) =>
      req.url.endsWith('/api/area-of-study'),
    );
    const reqLangs = httpMock.expectOne((req) =>
      req.url.endsWith('/api/courses/languages'),
    );
    const reqCountries = httpMock.expectOne((req) =>
      req.url.endsWith('/api/university/countries'),
    );

    reqAreas.flush([
      { name: 'Engineering' },
      { name: 'Mathematics' },
    ] as AreasOfStudy[]);
    reqLangs.flush(['English', 'Portuguese']);
    reqCountries.flush(['Brazil', 'Sweden']);

    const areas = await areasPromise;
    const langs = await langsPromise;
    const countries = await countriesPromise;

    expect(areas).toEqual(['Engineering', 'Mathematics']);
    expect(langs).toEqual(['English', 'Portuguese']);
    expect(countries).toEqual(['Brazil', 'Sweden']);
  });

  it('loadFilterLists should handle errors and publish empty arrays', async () => {
    spyOn(console, 'error');

    const areasPromise = firstValueFrom(
      service.areasOfStudy$.pipe(skip(1), take(1)),
    );
    const langsPromise = firstValueFrom(
      service.languages$.pipe(skip(1), take(1)),
    );
    const countriesPromise = firstValueFrom(
      service.countries$.pipe(skip(1), take(1)),
    );

    service.loadFilterLists();

    const reqAreas = httpMock.expectOne((req) =>
      req.url.endsWith('/api/area-of-study'),
    );
    const reqLangs = httpMock.expectOne((req) =>
      req.url.endsWith('/api/courses/languages'),
    );
    const reqCountries = httpMock.expectOne((req) =>
      req.url.endsWith('/api/university/countries'),
    );

    // flush the non-errored requests first, then make one request fail to trigger catchError branch
    reqLangs.flush(['English']);
    reqCountries.flush(['Brazil']);
    reqAreas.error(new ProgressEvent('error'), { status: 500 });

    const areas = await areasPromise;
    const langs = await langsPromise;
    const countries = await countriesPromise;

    expect(console.error).toHaveBeenCalled();
    expect(areas).toEqual([]);
    expect(langs).toEqual([]);
    expect(countries).toEqual([]);
  });

  it('getUserAtualId / getUserAtual / setUserAtual should work', () => {
    expect(service.getUserAtual()).toBeNull();
    expect(service.getUserAtualId()).toBeNull();

    const user: UserViewmodel = {
      id: 42,
      displayName: 'Test',
      email: 'a@b.c',
    } as any;
    service.setUserAtual(user);

    expect(service.getUserAtual()).toEqual(user);
    expect(service.getUserAtualId()).toBe(42);
  });
});
