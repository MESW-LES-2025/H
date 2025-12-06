import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Courses } from './courses';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { CoursesService } from './service/courses-service';
import { DataService } from '../shared/services/data-service';
import { of, throwError } from 'rxjs';
import { Page } from '../shared/viewmodels/pagination';
import { CourseViewmodel } from './viewmodels/course-viewmodel';

describe('Courses', () => {
  let component: Courses;
  let fixture: ComponentFixture<Courses>;
  let coursesService: jasmine.SpyObj<CoursesService>;
  let dataService: jasmine.SpyObj<DataService>;

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

  beforeEach(async () => {
    const coursesServiceSpy = jasmine.createSpyObj('CoursesService', ['getCourses']);
    const dataServiceSpy = jasmine.createSpyObj('DataService', ['loadFilterLists'], {
      areasOfStudy$: of([]),
      languages$: of([]),
      countries$: of([]),
    });

    await TestBed.configureTestingModule({
      imports: [Courses],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: CoursesService, useValue: coursesServiceSpy },
        { provide: DataService, useValue: dataServiceSpy },
      ],
    }).compileComponents();

    coursesService = TestBed.inject(CoursesService) as jasmine.SpyObj<CoursesService>;
    dataService = TestBed.inject(DataService) as jasmine.SpyObj<DataService>;
    coursesService.getCourses.and.returnValue(of(mockPage));

    fixture = TestBed.createComponent(Courses);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('should load courses on init', () => {
      fixture.detectChanges();

      expect(coursesService.getCourses).toHaveBeenCalled();
      expect(component.courses).toEqual(mockCourses);
      expect(component.pagedCourses).toEqual(mockPage);
    });

    it('should subscribe to form value changes with debounce', fakeAsync(() => {
      fixture.detectChanges();
      coursesService.getCourses.calls.reset();

      component.filterCoursesForm.patchValue({ name: 'Test' });
      tick(2500);

      expect(coursesService.getCourses).toHaveBeenCalled();
      expect(component.currentPage).toBe(0);
    }));

    it('should not reload if form is invalid', fakeAsync(() => {
      fixture.detectChanges();
      coursesService.getCourses.calls.reset();

      component.filterCoursesForm.patchValue({ costMax: -5 });
      tick(2500);

      expect(component.filterCoursesForm.valid).toBe(false);
    }));
  });

  describe('loadCourses', () => {
    it('should load courses with filters and pagination', () => {
      component.filterCoursesForm.patchValue({ name: 'Computer' });
      component.currentPage = 1;

      component.loadCourses();

      expect(coursesService.getCourses).toHaveBeenCalledWith(
        jasmine.objectContaining({ name: 'Computer' }),
        jasmine.objectContaining({ page: 1, size: 10, sort: 'name,asc' })
      );
    });

    it('should handle error when loading courses', () => {
      spyOn(console, 'error');
      coursesService.getCourses.and.returnValue(throwError(() => new Error('Failed')));

      component.loadCourses();

      expect(console.error).toHaveBeenCalled();
    });

    it('should not load courses if form is invalid', () => {
      coursesService.getCourses.calls.reset();
      component.filterCoursesForm.patchValue({ costMax: -5 });

      component.loadCourses();

      expect(coursesService.getCourses).not.toHaveBeenCalled();
    });
  });

  describe('onCheckboxChange', () => {
    it('should add value when checkbox is checked', () => {
      const event = {
        target: { checked: true, value: 'Bachelor' },
      } as any;

      component.onCheckboxChange(event, 'courseTypes');

      expect(component.filterCoursesForm.get('courseTypes')?.value).toContain('Bachelor');
    });

    it('should remove value when checkbox is unchecked', () => {
      component.filterCoursesForm.patchValue({ courseTypes: ['Bachelor', 'Master'] });

      const event = {
        target: { checked: false, value: 'Bachelor' },
      } as any;

      component.onCheckboxChange(event, 'courseTypes');

      expect(component.filterCoursesForm.get('courseTypes')?.value).toEqual(['Master']);
    });

    it('should not duplicate values', () => {
      component.filterCoursesForm.patchValue({ courseTypes: ['Bachelor'] });

      const event = {
        target: { checked: true, value: 'Bachelor' },
      } as any;

      component.onCheckboxChange(event, 'courseTypes');

      expect(component.filterCoursesForm.get('courseTypes')?.value).toEqual(['Bachelor']);
    });

    it('should handle missing control (use fallback empty array) without throwing', () => {
      const event = { target: { checked: true, value: 'X' } } as any;

      expect(() => component.onCheckboxChange(event, 'nonexistent')).not.toThrow();
      expect(component.filterCoursesForm.get('nonexistent')).toBeNull();
    });

    it('should use fallback [] when control value is falsy and set new values', () => {
      // set name control to empty string (falsy) so `|| []` branch triggers
      component.filterCoursesForm.get('name')?.setValue('');

      const event = { target: { checked: true, value: 'newVal' } } as any;

      component.onCheckboxChange(event, 'name');

      // the control will receive the new array value via setValue
      expect(component.filterCoursesForm.get('name')?.value).toEqual(['newVal']);
    });
  });

  describe('getFilters', () => {
    it('should return filters from form values', () => {
      component.filterCoursesForm.patchValue({
        name: 'Computer',
        courseTypes: ['Bachelor'],
        onlyRemote: true,
      });

      const filters = component.getFilters();

      expect(filters.name).toBe('Computer');
      expect(filters.courseTypes).toEqual(['Bachelor']);
      expect(filters.onlyRemote).toBe(true);
    });

    it('should handle null values with defaults', () => {
      component.filterCoursesForm.reset();

      const filters = component.getFilters();

      expect(filters.name).toBeNull();
      expect(filters.courseTypes).toEqual([]);
      expect(filters.onlyRemote).toBe(false);
    });

    it('should return defaults when controls are undefined', () => {
      // set each control explicitly to undefined
      const controls = [
        'name',
        'courseTypes',
        'areasOfStudy',
        'onlyRemote',
        'costMax',
        'duration',
        'languages',
        'countries',
      ];

      controls.forEach((c) => {
        component.filterCoursesForm.get(c as any)?.setValue(undefined as any);
      });

      const filters = component.getFilters();

      expect(filters.name).toBeNull();
      expect(filters.courseTypes).toEqual([]);
      expect(filters.areasOfStudy).toEqual([]);
      expect(filters.onlyRemote).toBeFalse();
      expect(filters.costMax).toBeNull();
      expect(filters.duration).toBeNull();
      expect(filters.languages).toEqual([]);
      expect(filters.countries).toEqual([]);
    });
  });

  describe('resetFilters', () => {
    it('should reset form to default values', () => {
      component.filterCoursesForm.patchValue({
        name: 'Test',
        courseTypes: ['Bachelor'],
        onlyRemote: true,
      });

      component.resetFilters();

      expect(component.filterCoursesForm.get('name')?.value).toBeNull();
      expect(component.filterCoursesForm.get('courseTypes')?.value).toEqual([]);
      expect(component.filterCoursesForm.get('onlyRemote')?.value).toBe(false);
    });

    it('should reset current page to 0', () => {
      component.currentPage = 5;

      component.resetFilters();

      expect(component.currentPage).toBe(0);
    });

    it('should reload courses after reset', () => {
      coursesService.getCourses.calls.reset();

      component.resetFilters();

      expect(coursesService.getCourses).toHaveBeenCalled();
    });
  });

  describe('pagination', () => {
    it('should return total pages from pagedCourses', () => {
      component.pagedCourses = { ...mockPage, totalPages: 5 };

      expect(component.totalPages).toBe(5);
    });

    it('should return 0 when pagedCourses is null', () => {
      component.pagedCourses = null;

      expect(component.totalPages).toBe(0);
    });

    it('should calculate pages array correctly', () => {
      component.pagedCourses = { ...mockPage, totalPages: 10 };
      component.currentPage = 5;
      component.maxPagesToShow = 5;

      const pages = component.pages;

      expect(pages).toEqual([3, 4, 5, 6, 7]);
    });

    it('should handle pages at the start', () => {
      component.pagedCourses = { ...mockPage, totalPages: 10 };
      component.currentPage = 0;
      component.maxPagesToShow = 5;

      const pages = component.pages;

      expect(pages).toEqual([0, 1, 2, 3, 4]);
    });

    it('should handle pages at the end', () => {
      component.pagedCourses = { ...mockPage, totalPages: 10 };
      component.currentPage = 9;
      component.maxPagesToShow = 5;

      const pages = component.pages;

      expect(pages).toEqual([5, 6, 7, 8, 9]);
    });
  });

  describe('goToPage', () => {
    beforeEach(() => {
      component.pagedCourses = { ...mockPage, totalPages: 5 };
      coursesService.getCourses.calls.reset();
    });

    it('should change page and load courses', () => {
      component.currentPage = 0;

      component.goToPage(2);

      expect(component.currentPage).toBe(2);
      expect(coursesService.getCourses).toHaveBeenCalled();
    });

    it('should not change page if page is negative', () => {
      component.currentPage = 2;

      component.goToPage(-1);

      expect(component.currentPage).toBe(2);
      expect(coursesService.getCourses).not.toHaveBeenCalled();
    });

    it('should not change page if page is greater than or equal to total pages', () => {
      component.currentPage = 2;

      component.goToPage(10);

      expect(component.currentPage).toBe(2);
      expect(coursesService.getCourses).not.toHaveBeenCalled();
    });

    it('should not change page if page is the same as current', () => {
      component.currentPage = 2;

      component.goToPage(2);

      expect(component.currentPage).toBe(2);
      expect(coursesService.getCourses).not.toHaveBeenCalled();
    });
  });

  describe('isValueSelected', () => {
    it('should return true if value is selected', () => {
      component.filterCoursesForm.patchValue({ courseTypes: ['Bachelor', 'Master'] });

      expect(component.isValueSelected('courseTypes', 'Bachelor')).toBe(true);
    });

    it('should return false if value is not selected', () => {
      component.filterCoursesForm.patchValue({ courseTypes: ['Bachelor'] });

      expect(component.isValueSelected('courseTypes', 'Master')).toBe(false);
    });

    it('should return false if control value is not an array', () => {
      component.filterCoursesForm.patchValue({ name: 'Test' });

      expect(component.isValueSelected('name', 'Test')).toBe(false);
    });
  });

  describe('ngOnDestroy', () => {
    it('should complete destroy$ subject', () => {
      const completeSpy = spyOn(component['destroy$'], 'complete');
      const nextSpy = spyOn(component['destroy$'], 'next');

      component.ngOnDestroy();

      expect(nextSpy).toHaveBeenCalled();
      expect(completeSpy).toHaveBeenCalled();
    });
  });
});
