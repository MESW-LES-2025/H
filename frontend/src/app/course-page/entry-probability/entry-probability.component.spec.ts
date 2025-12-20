import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { EntryProbabilityComponent, ProbabilityResponse } from './entry-probability.component';
import { AuthService } from '../../auth/auth.service';
import { environment } from '../../../environments/environment';

describe('EntryProbabilityComponent', () => {
    let component: EntryProbabilityComponent;
    let fixture: ComponentFixture<EntryProbabilityComponent>;
    let httpMock: HttpTestingController;
    let authServiceSpy: jasmine.SpyObj<AuthService>;
    const apiUrl = environment.apiUrl;

    const mockProbability: ProbabilityResponse = {
        courseId: 1,
        percentage: 75,
        confidenceLevel: 'HIGH',
        label: 'Excellent Fit',
        factors: {
            'Academic Grade': 'Exceeds requirements (180/150)',
            'Education Level': 'Meets requirement (Bachelor\'s)',
            'Study Area': 'Related field (Computer Science)'
        }
    };

    beforeEach(async () => {
        authServiceSpy = jasmine.createSpyObj('AuthService', ['isPremium']);

        await TestBed.configureTestingModule({
            imports: [
                EntryProbabilityComponent,
                HttpClientTestingModule,
                RouterTestingModule
            ],
            providers: [
                { provide: AuthService, useValue: authServiceSpy }
            ]
        }).compileComponents();

        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    describe('Non-Premium User', () => {
        beforeEach(() => {
            authServiceSpy.isPremium.and.returnValue(false);
            fixture = TestBed.createComponent(EntryProbabilityComponent);
            component = fixture.componentInstance;
            component.courseId = 1;
            fixture.detectChanges();
        });

        it('should create', () => {
            expect(component).toBeTruthy();
        });

        it('should show locked state for non-premium users', () => {
            expect(component.isPremium()).toBeFalse();
            const compiled = fixture.nativeElement as HTMLElement;
            expect(compiled.querySelector('.locked-state')).toBeTruthy();
            expect(compiled.querySelector('.lock-icon')).toBeTruthy();
        });

        it('should display upgrade prompt', () => {
            const compiled = fixture.nativeElement as HTMLElement;
            const unlockBtn = compiled.querySelector('.unlock-btn');
            expect(unlockBtn).toBeTruthy();
            expect(unlockBtn?.textContent).toContain('Upgrade to Premium');
        });

        it('should not make API call for non-premium users', () => {
            httpMock.expectNone(`${apiUrl}/api/courses/1/entry-probability`);
        });

        it('should set loading to false immediately', () => {
            expect(component.loading()).toBeFalse();
        });
    });

    describe('Premium User', () => {
        beforeEach(() => {
            authServiceSpy.isPremium.and.returnValue(true);
            fixture = TestBed.createComponent(EntryProbabilityComponent);
            component = fixture.componentInstance;
            component.courseId = 1;
        });

        it('should show loading state initially', () => {
            fixture.detectChanges();
            expect(component.loading()).toBeTrue();

            const compiled = fixture.nativeElement as HTMLElement;
            expect(compiled.querySelector('.loading-state')).toBeTruthy();

            // Complete the pending request
            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush(mockProbability);
        });

        it('should load probability for premium users', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            expect(req.request.method).toBe('GET');
            expect(req.request.withCredentials).toBeTrue();
            req.flush(mockProbability);

            tick();
            fixture.detectChanges();

            expect(component.probability()).toEqual(mockProbability);
            expect(component.loading()).toBeFalse();
        }));

        it('should display probability percentage', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush(mockProbability);

            tick();
            fixture.detectChanges();

            const compiled = fixture.nativeElement as HTMLElement;
            expect(compiled.querySelector('.percentage')?.textContent).toContain('75%');
        }));

        it('should display probability label', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush(mockProbability);

            tick();
            fixture.detectChanges();

            const compiled = fixture.nativeElement as HTMLElement;
            expect(compiled.querySelector('.label')?.textContent).toContain('Excellent Fit');
        }));

        it('should display factors breakdown', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush(mockProbability);

            tick();
            fixture.detectChanges();

            const compiled = fixture.nativeElement as HTMLElement;
            const factorItems = compiled.querySelectorAll('.factor-item');
            expect(factorItems.length).toBe(3);
        }));

        it('should handle API error', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.error(new ProgressEvent('error'), { status: 500 });

            tick();
            fixture.detectChanges();

            expect(component.error()).toBeTruthy();
            expect(component.loading()).toBeFalse();
        }));

        it('should handle 403 error by setting isPremium to false', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.error(new ProgressEvent('error'), { status: 403 });

            tick();
            fixture.detectChanges();

            expect(component.isPremium()).toBeFalse();
            expect(component.loading()).toBeFalse();
        }));
    });

    describe('Gauge Calculation', () => {
        beforeEach(() => {
            authServiceSpy.isPremium.and.returnValue(true);
            fixture = TestBed.createComponent(EntryProbabilityComponent);
            component = fixture.componentInstance;
            component.courseId = 1;
        });

        it('should calculate correct stroke dash array for 75%', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush(mockProbability);

            tick();

            const circumference = 2 * Math.PI * 50;
            const expectedOffset = (75 / 100) * circumference;
            const result = component.getStrokeDashArray();

            expect(result).toBe(`${expectedOffset} ${circumference}`);
        }));

        it('should return 0 offset when no probability', () => {
            const circumference = 2 * Math.PI * 50;
            const result = component.getStrokeDashArray();

            expect(result).toBe(`0 ${circumference}`);

            // No API call expected since we don't call detectChanges
        });
    });

    describe('Factor Entries', () => {
        beforeEach(() => {
            authServiceSpy.isPremium.and.returnValue(true);
            fixture = TestBed.createComponent(EntryProbabilityComponent);
            component = fixture.componentInstance;
            component.courseId = 1;
        });

        it('should convert factors object to array of entries', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush(mockProbability);

            tick();

            const entries = component.getFactorEntries();
            expect(entries.length).toBe(3);
            expect(entries[0].key).toBe('Academic Grade');
            expect(entries[0].value).toContain('Exceeds requirements');
        }));

        it('should return empty array when no probability', () => {
            const entries = component.getFactorEntries();
            expect(entries).toEqual([]);
        });
    });

    describe('Color Classes', () => {
        beforeEach(() => {
            authServiceSpy.isPremium.and.returnValue(true);
            fixture = TestBed.createComponent(EntryProbabilityComponent);
            component = fixture.componentInstance;
            component.courseId = 1;
        });

        it('should apply high class for percentage >= 70', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush({ ...mockProbability, percentage: 80 });

            tick();
            fixture.detectChanges();

            const compiled = fixture.nativeElement as HTMLElement;
            expect(compiled.querySelector('.gauge-progress.high')).toBeTruthy();
            expect(compiled.querySelector('.label.high')).toBeTruthy();
        }));

        it('should apply medium class for percentage 40-69', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush({ ...mockProbability, percentage: 55 });

            tick();
            fixture.detectChanges();

            const compiled = fixture.nativeElement as HTMLElement;
            expect(compiled.querySelector('.gauge-progress.medium')).toBeTruthy();
            expect(compiled.querySelector('.label.medium')).toBeTruthy();
        }));

        it('should apply low class for percentage < 40', fakeAsync(() => {
            fixture.detectChanges();

            const req = httpMock.expectOne(`${apiUrl}/api/courses/1/entry-probability`);
            req.flush({ ...mockProbability, percentage: 25 });

            tick();
            fixture.detectChanges();

            const compiled = fixture.nativeElement as HTMLElement;
            expect(compiled.querySelector('.gauge-progress.low')).toBeTruthy();
            expect(compiled.querySelector('.label.low')).toBeTruthy();
        }));
    });
});
