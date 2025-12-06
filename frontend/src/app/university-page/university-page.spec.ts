import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { UniversityPage } from './university-page';
import { UniversityPageService } from './services/university-page-service';
import { ExploreService } from '../explore-page/services/explore-service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';

describe('UniversityPage', () => {
  let component: UniversityPage;
  let fixture: ComponentFixture<UniversityPage>;
  let uniService: jasmine.SpyObj<UniversityPageService>;
  let exploreService: jasmine.SpyObj<ExploreService>;
  let router: Router;

  const sampleUni = {
    id: 10,
    name: 'Test Uni',
    bannerImage: '',
    logo: '',
    description: 'desc',
    courses: [],
    studentCount: 0,
    location: 'Nowhere',
    address: '',
    contactInfo: '',
    website: 'N/A',
    scholarships: [],
  } as any;

  beforeEach(async () => {
    const uniServiceSpy = jasmine.createSpyObj('UniversityPageService', [
      'getUniversityProfile',
    ]);
    const exploreSpy = jasmine.createSpyObj('ExploreService', [
      'getFavoriteUniversities',
      'addFavoriteUniversity',
      'removeFavoriteUniversity',
    ]);

    await TestBed.configureTestingModule({
      imports: [UniversityPage, RouterTestingModule.withRoutes([])],
      providers: [
        { provide: UniversityPageService, useValue: uniServiceSpy },
        { provide: ExploreService, useValue: exploreSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: { get: (_: string) => '10' } } },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UniversityPage);
    component = fixture.componentInstance;
    uniService = TestBed.inject(
      UniversityPageService,
    ) as jasmine.SpyObj<UniversityPageService>;
    exploreService = TestBed.inject(
      ExploreService,
    ) as jasmine.SpyObj<ExploreService>;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    // ensure localStorage clean
    localStorage.removeItem('userId');
  });

  afterEach(() => {
    localStorage.removeItem('userId');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit should load university and set isFavorite false when no user', (done) => {
    uniService.getUniversityProfile.and.returnValue(of(sampleUni));

    fixture.detectChanges(); // runs ngOnInit

    setTimeout(() => {
      expect(uniService.getUniversityProfile).toHaveBeenCalledWith(10);
      expect((component as any).university).toEqual(sampleUni);
      expect((component as any).isFavorite).toBeFalse();
      done();
    }, 20);
  });

  it('ngOnInit should load favorite state when userId present', (done) => {
    uniService.getUniversityProfile.and.returnValue(of(sampleUni));
    exploreService.getFavoriteUniversities.and.returnValue(of([10]));

    localStorage.setItem('userId', '5');

    fixture.detectChanges();

    setTimeout(() => {
      expect(uniService.getUniversityProfile).toHaveBeenCalledWith(10);
      expect(exploreService.getFavoriteUniversities).toHaveBeenCalledWith(5);
      expect((component as any).isFavorite).toBeTrue();
      done();
    }, 20);
  });

  it('onToggleFavorite should do nothing when university is null', () => {
    (component as any).university = null;
    component.onToggleFavorite();

    expect(exploreService.addFavoriteUniversity).not.toHaveBeenCalled();
    expect(exploreService.removeFavoriteUniversity).not.toHaveBeenCalled();
  });

  it('loadFavoriteState should set isFavorite false when favorites do not include uni', fakeAsync(() => {
    (component as any).university = sampleUni;
    localStorage.setItem('userId', '5');
    exploreService.getFavoriteUniversities.and.returnValue(of([1, 2, 3]));

    (component as any).loadFavoriteState();
    tick();

    expect(exploreService.getFavoriteUniversities).toHaveBeenCalledWith(5);
    expect((component as any).isFavorite).toBeFalse();
  }));

  it('loadFavoriteState should early return and set isFavorite false when university is null even if userId exists', () => {
    (component as any).university = null;
    localStorage.setItem('userId', '5');
    (component as any).isFavorite = true;

    (component as any).loadFavoriteState();

    expect((component as any).isFavorite).toBeFalse();
    expect(exploreService.getFavoriteUniversities).not.toHaveBeenCalled();
  });

  it('loadFavoriteState should log error when getFavoriteUniversities errors', fakeAsync(() => {
    (component as any).university = sampleUni;
    localStorage.setItem('userId', '5');
    spyOn(console, 'error');
    exploreService.getFavoriteUniversities.and.returnValue(
      throwError(() => new Error('fail')),
    );

    (component as any).loadFavoriteState();
    tick();

    expect(console.error).toHaveBeenCalled();
  }));

  it('onToggleFavorite should add favorite when not favorite', fakeAsync(() => {
    (component as any).university = sampleUni;
    (component as any).isFavorite = false;
    exploreService.addFavoriteUniversity.and.returnValue(of(void 0));

    component.onToggleFavorite();
    tick();

    expect(exploreService.addFavoriteUniversity).toHaveBeenCalledWith(10);
    expect((component as any).isFavorite).toBeTrue();
  }));

  it('onToggleFavorite should remove favorite when already favorite', fakeAsync(() => {
    (component as any).university = sampleUni;
    (component as any).isFavorite = true;
    exploreService.removeFavoriteUniversity.and.returnValue(of(void 0));

    component.onToggleFavorite();
    tick();

    expect(exploreService.removeFavoriteUniversity).toHaveBeenCalledWith(10);
    expect((component as any).isFavorite).toBeFalse();
  }));

  it('goToCourse should navigate to course route', () => {
    component.goToCourse(123);

    expect(router.navigate).toHaveBeenCalledWith(['/course', 123]);
  });
});
