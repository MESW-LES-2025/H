import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, Validators} from '@angular/forms';
import { CoursesService } from './service/courses-service';
import { CourseViewmodel } from './viewmodels/course-viewmodel';
import { Subject, takeUntil } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import {AsyncPipe, DatePipe} from '@angular/common';
import {CourseFilters} from "./viewmodels/course-filters";
import {CourseFiltersForm} from "./viewmodels/course-filters-form";
import {Page} from "../shared/viewmodels/pagination";
import {CourseTypeEnum} from "../shared/enums/course-type-enum";
import {DataService} from "../shared/services/data-service";

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [DatePipe, ReactiveFormsModule, AsyncPipe],
  templateUrl: './courses.html',
  styleUrls: ['./courses.css'],
})
export class Courses implements OnInit, OnDestroy {
  currentPage: number = 0;
  pageSize: number = 10;
  maxPagesToShow: number = 5;
  protected readonly CourseTypeEnum: string[] = CourseTypeEnum;
  private courseService: CoursesService = inject(CoursesService);
  protected dataService: DataService = inject(DataService);

  courses: CourseViewmodel[] = [];
  pagedCourses: Page<CourseViewmodel> | null = null;
  isLoading: boolean = false;

  private destroy$: Subject<void> = new Subject<void>();

  filterCoursesForm: FormGroup = new FormGroup<CourseFiltersForm>({
    name: new FormControl<string | null>(null),
    courseTypes: new FormControl<string[]>([], {
      nonNullable: true,
    }),
    areasOfStudy: new FormControl<string[]>([], {
      nonNullable: true,
    }),
    onlyRemote: new FormControl<boolean>(false, {
      nonNullable: true,
    }),
    costMax: new FormControl<number | null>(null, [
      Validators.min(1),
      Courses.integerValidator,
    ]),
    duration: new FormControl<number | null>(null, [
      Validators.min(1),
      Courses.integerValidator,
    ]),
    languages: new FormControl<string[]>([], {
      nonNullable: true,
    }),
    countries: new FormControl<string[]>([], {
      nonNullable: true,
    }),
  });

  ngOnInit(): void {
    this.loadCourses();

    this.filterCoursesForm.valueChanges.pipe(
        debounceTime(2500),
        distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b)),
        takeUntil(this.destroy$)
    ).subscribe(() => {
      this.currentPage = 0;
      this.loadCourses();
    });

    // Set loading state immediately when form changes (before debounce)
    this.filterCoursesForm.valueChanges.pipe(
        takeUntil(this.destroy$)
    ).subscribe(() => {
      this.isLoading = true;
    });
  }

  loadCourses(): void {
    if (this.filterCoursesForm.valid) {
      this.isLoading = true;
      const filters = this.getFilters();
      const pageRequest = {
        page: this.currentPage,
        size: this.pageSize,
        sort: 'name,asc',
      };
      this.courseService.getCourses(filters, pageRequest)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (pagedResponse: Page<CourseViewmodel>) => {
              this.pagedCourses = pagedResponse;
              this.courses = pagedResponse.content;
              this.isLoading = false;
            },
            error: (err) => {
              console.error(err);  // TODO: Notifications
              this.isLoading = false;
            }
          });
    }
  }

  onCheckboxChange(event: Event, controlName: string) {
    const checkbox = event.target as HTMLInputElement;
    const value = checkbox.value;
    const selectedValues: string[] = this.filterCoursesForm.get(controlName)?.value || [];

    let newValues: string[];
    if (checkbox.checked) {
      newValues = selectedValues.includes(value)
          ? selectedValues
          : [...selectedValues, value];
    } else {
      newValues = selectedValues.filter(v => v !== value);
    }
    this.filterCoursesForm.get(controlName)?.setValue(newValues, { emitEvent: true });
  }

  getFilters(): CourseFilters {
    const formValue = this.filterCoursesForm.value;

    return {
      name: formValue.name ?? null,
      courseTypes: formValue.courseTypes ?? [],
      areasOfStudy: formValue.areasOfStudy ?? [],
      onlyRemote: formValue.onlyRemote ?? false,
      costMax: formValue.costMax ?? null,
      duration: formValue.duration ?? null,
      languages: formValue.languages ?? [],
      countries: formValue.countries ?? [],
    };
  }

  resetFilters(): void {
    this.filterCoursesForm.reset({
      name: null,
      courseTypes: [],
      areasOfStudy: [],
      onlyRemote: false,
      costMax: null,
      duration: null,
      languages: [],
      countries: [],
    }, { emitEvent: false });
    this.currentPage = 0;
    this.loadCourses();
  }


  get totalPages(): number {
    return this.pagedCourses?.totalPages ?? 0;
  }

  get pages(): number[] {
    const total = this.totalPages;
    const half = Math.floor(this.maxPagesToShow / 2);

    let start = Math.max(0, this.currentPage - half);
    let end = Math.min(total - 1, this.currentPage + half);

    if (this.currentPage - half < 0) {
      end = Math.min(total - 1, end + (half - this.currentPage));
    } else if (this.currentPage + half > total - 1) {
      start = Math.max(0, start - ((this.currentPage + half) - (total - 1)));
    }

    return Array.from({length: (end - start + 1)}, (_, i) => start + i);
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.currentPage) return;
    this.currentPage = page;
    this.loadCourses();
  }

  isValueSelected(controlName: string, value: any): boolean {
    const selectedValues = this.filterCoursesForm.get(controlName)?.value;
    if (!Array.isArray(selectedValues)) {
      return false;
    }
    return selectedValues.includes(value);
  }

  static integerValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (value == null) return null; // allow null, use required validator if needed
    return Number.isInteger(value) ? null : { integer: true };
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
