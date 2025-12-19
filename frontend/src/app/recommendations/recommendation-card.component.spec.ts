import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RecommendationCardComponent } from './recommendation-card.component';
import { RouterTestingModule } from '@angular/router/testing';
import { Suggestion } from './recommendation.service';

describe('RecommendationCardComponent', () => {
    let component: RecommendationCardComponent;
    let fixture: ComponentFixture<RecommendationCardComponent>;

    const mockCourseSuggestion: Suggestion = {
        id: 1,
        title: 'Computer Science',
        description: 'A great course for learning programming and algorithms.',
        type: 'course',
        matchScore: 85,
        location: 'Portugal',
        courseType: 'Bachelor',
        universityName: 'Test University',
    };

    const mockUniversitySuggestion: Suggestion = {
        id: 2,
        title: 'MIT',
        description: 'Top university for technology and engineering.',
        type: 'university',
        matchScore: 70,
        location: 'USA',
    };

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [RecommendationCardComponent, RouterTestingModule],
        }).compileComponents();

        fixture = TestBed.createComponent(RecommendationCardComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        component.suggestion = mockCourseSuggestion;
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    describe('getLink', () => {
        it('should return course link for course type', () => {
            component.suggestion = mockCourseSuggestion;
            fixture.detectChanges();

            expect(component.getLink()).toEqual(['/course', '1']);
        });

        it('should return university link for university type', () => {
            component.suggestion = mockUniversitySuggestion;
            fixture.detectChanges();

            expect(component.getLink()).toEqual(['/university', '2']);
        });
    });

    describe('truncateDescription', () => {
        it('should not truncate short descriptions', () => {
            component.suggestion = mockCourseSuggestion;
            fixture.detectChanges();

            const shortText = 'Short text';
            expect(component.truncateDescription(shortText)).toBe('Short text');
        });

        it('should truncate long descriptions', () => {
            component.suggestion = mockCourseSuggestion;
            fixture.detectChanges();

            const longText = 'A'.repeat(150);
            const result = component.truncateDescription(longText);
            expect(result.length).toBe(103); // 100 + '...'
            expect(result.endsWith('...')).toBe(true);
        });

        it('should not truncate exactly 100 characters', () => {
            component.suggestion = mockCourseSuggestion;
            fixture.detectChanges();

            const exactText = 'A'.repeat(100);
            expect(component.truncateDescription(exactText)).toBe(exactText);
        });
    });
});
