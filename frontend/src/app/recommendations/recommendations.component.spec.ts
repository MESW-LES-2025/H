import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RecommendationsComponent } from './recommendations.component';
import { RecommendationService, Suggestion } from './recommendation.service';
import { AuthService } from '../auth/auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

describe('RecommendationsComponent', () => {
    let component: RecommendationsComponent;
    let fixture: ComponentFixture<RecommendationsComponent>;
    let recommendationServiceSpy: jasmine.SpyObj<RecommendationService>;
    let authServiceSpy: jasmine.SpyObj<AuthService>;

    const mockSuggestions: Suggestion[] = [
        {
            id: 1,
            title: 'Computer Science',
            description: 'A great course',
            type: 'course',
            matchScore: 85,
            location: 'Portugal',
            courseType: 'Bachelor',
            universityName: 'Test University',
        },
        {
            id: 2,
            title: 'MIT',
            description: 'Top university',
            type: 'university',
            matchScore: 70,
            location: 'USA',
        },
    ];

    beforeEach(async () => {
        recommendationServiceSpy = jasmine.createSpyObj('RecommendationService', ['getRecommendations']);
        authServiceSpy = jasmine.createSpyObj('AuthService', ['isPremium']);

        await TestBed.configureTestingModule({
            imports: [RecommendationsComponent, RouterTestingModule],
            providers: [
                { provide: RecommendationService, useValue: recommendationServiceSpy },
                { provide: AuthService, useValue: authServiceSpy },
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(RecommendationsComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        authServiceSpy.isPremium.and.returnValue(false);
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    describe('Non-Premium User', () => {
        beforeEach(() => {
            authServiceSpy.isPremium.and.returnValue(false);
        });

        it('should show upgrade prompt for non-premium users', () => {
            fixture.detectChanges();

            expect(component.isPremium()).toBe(false);
            expect(component.loading()).toBe(false);
            expect(recommendationServiceSpy.getRecommendations).not.toHaveBeenCalled();
        });

        it('should not load recommendations for non-premium users', () => {
            fixture.detectChanges();
            component.ngOnInit();

            expect(recommendationServiceSpy.getRecommendations).not.toHaveBeenCalled();
        });
    });

    describe('Premium User', () => {
        beforeEach(() => {
            authServiceSpy.isPremium.and.returnValue(true);
        });

        it('should load recommendations for premium users', fakeAsync(() => {
            recommendationServiceSpy.getRecommendations.and.returnValue(of(mockSuggestions));

            fixture.detectChanges();
            tick();

            expect(component.isPremium()).toBe(true);
            expect(component.recommendations()).toEqual(mockSuggestions);
            expect(component.loading()).toBe(false);
        }));

        it('should handle empty recommendations', fakeAsync(() => {
            recommendationServiceSpy.getRecommendations.and.returnValue(of([]));

            fixture.detectChanges();
            tick();

            expect(component.recommendations()).toEqual([]);
            expect(component.loading()).toBe(false);
        }));

        it('should handle API error', fakeAsync(() => {
            recommendationServiceSpy.getRecommendations.and.returnValue(
                throwError(() => ({ status: 500 }))
            );

            fixture.detectChanges();
            tick();

            expect(component.error()).toBe('Failed to load recommendations. Please try again later.');
            expect(component.loading()).toBe(false);
        }));

        it('should set isPremium to false on 403 error', fakeAsync(() => {
            recommendationServiceSpy.getRecommendations.and.returnValue(
                throwError(() => ({ status: 403 }))
            );

            fixture.detectChanges();
            tick();

            expect(component.isPremium()).toBe(false);
            expect(component.error()).toBeNull();
            expect(component.loading()).toBe(false);
        }));
    });

    describe('Loading State', () => {
        it('should show loading state initially for premium users', () => {
            authServiceSpy.isPremium.and.returnValue(true);
            recommendationServiceSpy.getRecommendations.and.returnValue(of(mockSuggestions));

            expect(component.loading()).toBe(true);
        });
    });
});
