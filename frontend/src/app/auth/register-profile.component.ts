import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
    ReactiveFormsModule,
    FormBuilder,
    FormGroup,
    FormArray,
    Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';
import { CoursesService } from '../courses/service/courses-service';
import { CourseViewmodel } from '../courses/viewmodels/course-viewmodel';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

interface EducationEntry {
    courseId: number;
    courseName: string;
    startDate: string;
    endDate: string;
}

@Component({
    selector: 'app-register-profile',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: './register-profile.component.html',
    styleUrls: ['./login.component.css', './register-profile.component.css'],
})
export class RegisterProfileComponent implements OnInit {
    form!: FormGroup;
    loading = signal(false);
    error = signal<string | null>(null);

    courseSearchQuery = signal('');
    courseSearchResults = signal<CourseViewmodel[]>([]);
    isSearching = signal(false);
    showCourseDropdown = signal(false);
    private searchSubject = new Subject<string>();

    educationEntries = signal<EducationEntry[]>([]);

    genderOptions = [
        { value: 'MALE', label: 'Male' },
        { value: 'FEMALE', label: 'Female' },
        { value: 'OTHER', label: 'Prefer not to say' },
    ];

    private userId: number | null = null;

    constructor(
        private fb: FormBuilder,
        private router: Router,
        private auth: AuthService,
        private coursesService: CoursesService,
    ) { }

    ngOnInit(): void {
        // Get userId from state passed during navigation
        const navigation = this.router.getCurrentNavigation();
        const state = navigation?.extras?.state as { userId?: number } | undefined;
        this.userId = state?.userId ?? null;

        // If no userId, try to get from localStorage (fallback)
        if (!this.userId) {
            const storedUserId = localStorage.getItem('pendingProfileUserId');
            if (storedUserId) {
                this.userId = parseInt(storedUserId, 10);
            }
        }

        this.form = this.fb.group({
            age: [null, [Validators.min(16), Validators.max(120)]],
            gender: ['OTHER'],
            location: [''],
            jobTitle: [''],
        });

        // Setup debounced course search
        this.searchSubject
            .pipe(debounceTime(300), distinctUntilChanged())
            .subscribe((query) => {
                this.performCourseSearch(query);
            });
    }

    onCourseSearchInput(event: Event): void {
        const input = event.target as HTMLInputElement;
        const query = input.value;
        this.courseSearchQuery.set(query);

        if (query.length >= 2) {
            this.showCourseDropdown.set(true);
            this.searchSubject.next(query);
        } else {
            this.courseSearchResults.set([]);
            this.showCourseDropdown.set(false);
        }
    }

    onInputBlur(): void {
        setTimeout(() => {
            this.showCourseDropdown.set(false);
        }, 200);
    }

    private performCourseSearch(query: string): void {
        if (query.length < 2) return;

        this.isSearching.set(true);
        const filters = {
            name: query,
            courseTypes: [],
            areasOfStudy: [],
            onlyRemote: false,
            costMax: null,
            duration: null,
            languages: [],
            countries: [],
        };
        this.coursesService
            .getCourses(filters, { page: 0, size: 10, sort: 'name,asc' })
            .subscribe({
                next: (page) => {
                    this.courseSearchResults.set(page.content);
                    this.isSearching.set(false);
                    if (page.content.length > 0) {
                        this.showCourseDropdown.set(true);
                    }
                },
                error: (err) => {
                    this.courseSearchResults.set([]);
                    this.isSearching.set(false);
                },
            });
    }

    selectCourse(course: CourseViewmodel): void {
        // Check if already added
        const existing = this.educationEntries().find(
            (e) => e.courseId === course.id,
        );
        if (existing) {
            this.showCourseDropdown.set(false);
            this.courseSearchQuery.set('');
            return;
        }

        const newEntry: EducationEntry = {
            courseId: course.id,
            courseName: course.name || `Course ${course.id}`,
            startDate: '',
            endDate: '',
        };

        this.educationEntries.set([...this.educationEntries(), newEntry]);
        this.showCourseDropdown.set(false);
        this.courseSearchQuery.set('');
        this.courseSearchResults.set([]);
    }

    removeEducation(index: number): void {
        const entries = [...this.educationEntries()];
        entries.splice(index, 1);
        this.educationEntries.set(entries);
    }

    updateEducationDate(
        index: number,
        field: 'startDate' | 'endDate',
        event: Event,
    ): void {
        const input = event.target as HTMLInputElement;
        const entries = [...this.educationEntries()];
        entries[index] = { ...entries[index], [field]: input.value };
        this.educationEntries.set(entries);
    }

    hideCourseDropdown(): void {
        // Delay to allow click on dropdown items
        setTimeout(() => {
            this.showCourseDropdown.set(false);
        }, 200);
    }

    onSubmit(): void {
        if (this.form.invalid || !this.userId) {
            this.error.set('Unable to update profile. Please try again.');
            return;
        }

        this.loading.set(true);
        this.error.set(null);

        const profileData = {
            age: this.form.value.age,
            gender: this.form.value.gender,
            location: this.form.value.location,
            jobTitle: this.form.value.jobTitle,
        };

        this.auth.updateUser(this.userId, profileData).subscribe({
            next: () => {
                // Auto-login with stored credentials
                this.performAutoLogin();
            },
            error: (err) => {
                console.error('Profile update error', err);
                this.error.set('Failed to update profile. Please try again.');
                this.loading.set(false);
            },
        });
    }

    private performAutoLogin(): void {
        const email = localStorage.getItem('pendingLoginEmail');
        const password = localStorage.getItem('pendingLoginPassword');
        const userId = this.userId;

        // Clean up stored data
        localStorage.removeItem('pendingProfileUserId');
        localStorage.removeItem('pendingLoginEmail');
        localStorage.removeItem('pendingLoginPassword');

        if (email && password) {
            this.auth.login({ text: email, password }).subscribe({
                next: (res) => {
                    this.loading.set(false);
                    if (res.status === 'success') {
                        // Redirect to user's profile page
                        this.router.navigate(['/profile', userId]);
                    } else {
                        // Login failed, go to login page
                        this.router.navigate(['/login']);
                    }
                },
                error: () => {
                    this.loading.set(false);
                    // Login failed, go to login page
                    this.router.navigate(['/login']);
                },
            });
        } else {
            this.loading.set(false);
            this.router.navigate(['/login']);
        }
    }

    skip(): void {
        this.loading.set(true);
        this.performAutoLogin();
    }

    goTo(path: string, ev: Event): void {
        ev.preventDefault();
        const nav = () => this.router.navigate([`/${path}`]);
        const doc: any = document;
        doc.startViewTransition ? doc.startViewTransition(() => nav()) : nav();
    }
}
