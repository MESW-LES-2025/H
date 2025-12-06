import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ExploreComponent } from './explore.component';
import { ExploreService } from './services/explore-service';
import { DataService } from '../shared/services/data-service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Page } from '../shared/viewmodels/pagination';
import { CollegeVM } from './viewmodels/explore-viewmodel';

describe('ExploreComponent', () => {
  let component: ExploreComponent;
  let fixture: ComponentFixture<ExploreComponent>;
  let exploreService: jasmine.SpyObj<ExploreService>;
  let dataService: jasmine.SpyObj<DataService>;
  let router: jasmine.SpyObj<Router>;

  const mockColleges: CollegeVM[] = [
    {
      id: '1',
      title: 'Test University',
      blurb: 'A great university',
      photo: 'test.jpg',
      color: '#7DB19F',
      country: 'USA',
      city: 'New York',
      costOfLiving: 2000,
      isFavorite: false,
    },
    {
      id: '2',
      title: 'Another University',
      blurb: 'Another great place',
      photo: 'test2.jpg',
      color: '#7DB19F',
      country: 'UK',
      city: 'London',
      costOfLiving: 2500,
      isFavorite: true,
    },
  ];

  const mockPage: Page<CollegeVM> = {
    content: mockColleges,
    totalElements: 2,
    totalPages: 1,
    size: 3,
    number: 0,
  };

  beforeEach(async () => {
    const exploreServiceSpy = jasmine.createSpyObj('ExploreService', [
      'search',
      'getFavorites',
      'addFavoriteUniversity',
      'removeFavoriteUniversity',
    ]);

    const dataServiceSpy = jasmine.createSpyObj('DataService', [], {
      countries$: of(['USA', 'UK', 'Canada']),
    });

    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ExploreComponent],
      providers: [
        { provide: ExploreService, useValue: exploreServiceSpy },
        { provide: DataService, useValue: dataServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    exploreService = TestBed.inject(ExploreService) as jasmine.SpyObj<ExploreService>;
    dataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    exploreService.search.and.returnValue(of(mockPage));
    exploreService.getFavorites.and.returnValue(
      of({ universities: [{ id: 2 }], courses: [] })
    );

    fixture = TestBed.createComponent(ExploreComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load countries from dataService', (done) => {
      fixture.detectChanges();

      setTimeout(() => {
        expect(component.countries()).toContain('USA');
        expect(component.countries()).toContain('Any');
        done();
      }, 100);
    });

    it('should load favorites and search when user is logged in', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue('1');

      fixture.detectChanges();

      setTimeout(() => {
        expect(exploreService.getFavorites).toHaveBeenCalled();
        expect(component.favoriteUniversityIds()).toEqual([2]);
        expect(exploreService.search).toHaveBeenCalled();
        done();
      }, 100);
    });

    it('should search without loading favorites when user not logged in', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue(null);

      fixture.detectChanges();

      setTimeout(() => {
        expect(exploreService.getFavorites).not.toHaveBeenCalled();
        expect(exploreService.search).toHaveBeenCalled();
        done();
      }, 100);
    });

    it('should handle error when loading favorites', (done) => {
      spyOn(localStorage, 'getItem').and.returnValue('1');
      spyOn(console, 'error');
      exploreService.getFavorites.and.returnValue(
        throwError(() => new Error('Failed'))
      );

      fixture.detectChanges();

      setTimeout(() => {
        expect(console.error).toHaveBeenCalled();
        expect(exploreService.search).toHaveBeenCalled();
        done();
      }, 100);
    });
  });

  describe('search', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should search and set results', (done) => {
      component.search();

      setTimeout(() => {
        expect(exploreService.search).toHaveBeenCalled();
        expect(component.results().length).toBe(2);
        expect(component.hasMorePages()).toBe(false);
        expect(component.isLoading()).toBe(false);
        done();
      }, 100);
    });

    it('should reset page to 0 before searching', () => {
      component.pageRequest.page = 5;

      component.search();

      expect(component.pageRequest.page).toBe(0);
    });

    it('should set isLoading to true then false after search completes', (done) => {
      let isLoadingDuringSearch = false;

      // Check isLoading immediately after calling search
      component.search();
      isLoadingDuringSearch = component.isLoading();

      setTimeout(() => {
        expect(isLoadingDuringSearch).toBe(true);
        expect(component.isLoading()).toBe(false);
        done();
      }, 100);
    });

    it('should pass null for cost when at maxCost', (done) => {
      component.cost.set(5000);

      component.search();

      setTimeout(() => {
        const callArgs = exploreService.search.calls.mostRecent().args;
        expect(callArgs[2]).toBeNull();
        done();
      }, 100);
    });

    it('should pass cost value when below maxCost', (done) => {
      component.cost.set(3000);

      component.search();

      setTimeout(() => {
        const callArgs = exploreService.search.calls.mostRecent().args;
        expect(callArgs[2]).toBe(3000);
        done();
      }, 100);
    });

    it('should apply favorite flags to results', (done) => {
      component.favoriteUniversityIds.set([1]);

      component.search();

      setTimeout(() => {
        const result = component.results().find((r) => r.id === '1');
        expect(result?.isFavorite).toBe(true);
        done();
      }, 100);
    });
  });

  describe('loadMore', () => {
    beforeEach(() => {
      fixture.detectChanges();
      component.hasMorePages.set(true);
      component.pageRequest.page = 0;
    });

    it('should load more results and append to existing', (done) => {
      const morePage: Page<CollegeVM> = {
        content: [mockColleges[0]],
        totalElements: 3,
        totalPages: 2,
        size: 3,
        number: 1,
      };
      exploreService.search.and.returnValue(of(morePage));

      component.loadMore();

      setTimeout(() => {
        expect(component.pageRequest.page).toBe(1);
        expect(component.results().length).toBeGreaterThan(2);
        done();
      }, 100);
    });

    it('should not load more if already loading', () => {
      component.isLoading.set(true);
      exploreService.search.calls.reset();

      component.loadMore();

      expect(exploreService.search).not.toHaveBeenCalled();
    });

    it('should not load more if no more pages', () => {
      component.hasMorePages.set(false);
      exploreService.search.calls.reset();

      component.loadMore();

      expect(exploreService.search).not.toHaveBeenCalled();
    });

    it('should update hasMorePages based on response', (done) => {
      const lastPage: Page<CollegeVM> = {
        content: [mockColleges[0]],
        totalElements: 3,
        totalPages: 2,
        size: 3,
        number: 1,
      };
      exploreService.search.and.returnValue(of(lastPage));

      component.loadMore();

      setTimeout(() => {
        expect(component.hasMorePages()).toBe(false);
        done();
      }, 100);
    });
  });

  describe('filter changes', () => {
    beforeEach(() => {
      fixture.detectChanges();
      exploreService.search.calls.reset();
    });

    it('should trigger search when scholarship changes', () => {
      component.onScholarshipChange('Yes');

      expect(component.scholarship()).toBe('Yes');
      expect(exploreService.search).toHaveBeenCalled();
    });

    it('should trigger search when country changes', () => {
      component.onCountryChange('USA');

      expect(component.country()).toBe('USA');
      expect(exploreService.search).toHaveBeenCalled();
    });

    it('should trigger search when cost changes', () => {
      component.onCostChange(3000);

      expect(component.cost()).toBe(3000);
      expect(exploreService.search).toHaveBeenCalled();
    });
  });

  describe('clearFilters', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should reset all filters to defaults', () => {
      component.q.set('test');
      component.country.set('USA');
      component.cost.set(2000);
      component.scholarship.set('Yes');

      component.clearFilters();

      expect(component.q()).toBe('');
      expect(component.country()).toBe('Any');
      expect(component.cost()).toBe(5000);
      expect(component.scholarship()).toBe('Any');
    });

    it('should trigger search after clearing', () => {
      exploreService.search.calls.reset();

      component.clearFilters();

      expect(exploreService.search).toHaveBeenCalled();
    });
  });

  describe('onFavoriteUniversityClick', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should add university to favorites when not favorited', (done) => {
      const college = { ...mockColleges[0], isFavorite: false };
      const mockEvent = { stopPropagation: jasmine.createSpy() } as any;
      exploreService.addFavoriteUniversity.and.returnValue(of(void 0));

      component.onFavoriteUniversityClick(college, mockEvent);

      setTimeout(() => {
        expect(mockEvent.stopPropagation).toHaveBeenCalled();
        expect(exploreService.addFavoriteUniversity).toHaveBeenCalledWith(1);
        expect(college.isFavorite).toBe(true);
        expect(component.favoriteUniversityIds()).toContain(1);
        done();
      }, 100);
    });

    it('should remove university from favorites when already favorited', (done) => {
      const college = { ...mockColleges[0], isFavorite: true };
      const mockEvent = { stopPropagation: jasmine.createSpy() } as any;
      component.favoriteUniversityIds.set([1, 2]);
      exploreService.removeFavoriteUniversity.and.returnValue(of(void 0));

      component.onFavoriteUniversityClick(college, mockEvent);

      setTimeout(() => {
        expect(exploreService.removeFavoriteUniversity).toHaveBeenCalledWith(1);
        expect(college.isFavorite).toBe(false);
        expect(component.favoriteUniversityIds()).not.toContain(1);
        done();
      }, 100);
    });

    it('should handle error when adding favorite', (done) => {
      const college = { ...mockColleges[0], isFavorite: false };
      const mockEvent = { stopPropagation: jasmine.createSpy() } as any;
      spyOn(console, 'error');
      exploreService.addFavoriteUniversity.and.returnValue(
        throwError(() => new Error('Failed'))
      );

      component.onFavoriteUniversityClick(college, mockEvent);

      setTimeout(() => {
        expect(console.error).toHaveBeenCalled();
        done();
      }, 100);
    });

    it('should handle error when removing favorite', (done) => {
      const college = { ...mockColleges[0], isFavorite: true };
      const mockEvent = { stopPropagation: jasmine.createSpy() } as any;
      spyOn(console, 'error');
      exploreService.removeFavoriteUniversity.and.returnValue(
        throwError(() => new Error('Failed'))
      );

      component.onFavoriteUniversityClick(college, mockEvent);

      setTimeout(() => {
        expect(console.error).toHaveBeenCalled();
        done();
      }, 100);
    });

    it('should return early for invalid university id', () => {
      const college = { ...mockColleges[0], id: 'invalid' };
      const mockEvent = { stopPropagation: jasmine.createSpy() } as any;

      component.onFavoriteUniversityClick(college, mockEvent);

      expect(exploreService.addFavoriteUniversity).not.toHaveBeenCalled();
      expect(exploreService.removeFavoriteUniversity).not.toHaveBeenCalled();
    });
  });

  describe('goToUniversity', () => {
    it('should navigate to university page', () => {
      component.goToUniversity('123');

      expect(router.navigate).toHaveBeenCalledWith(['/university', '123']);
    });
  });

  describe('component initialization', () => {
    it('should initialize with default values', () => {
      expect(component.q()).toBe('');
      expect(component.country()).toBe('Any');
      expect(component.cost()).toBe(5000);
      expect(component.scholarship()).toBe('Any');
      expect(component.results()).toEqual([]);
      expect(component.hasMorePages()).toBe(false);
      expect(component.isLoading()).toBe(false);
    });
  });
});
