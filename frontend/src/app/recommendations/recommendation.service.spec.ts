import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { RecommendationService, Suggestion } from './recommendation.service';

describe('RecommendationService', () => {
    let service: RecommendationService;
    let httpMock: HttpTestingController;

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

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [RecommendationService],
        });

        service = TestBed.inject(RecommendationService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    describe('getRecommendations', () => {
        it('should return recommendations from the API', () => {
            service.getRecommendations().subscribe((suggestions) => {
                expect(suggestions).toEqual(mockSuggestions);
                expect(suggestions.length).toBe(2);
            });

            const req = httpMock.expectOne((r) => r.url.includes('/api/recommendations'));
            expect(req.request.method).toBe('GET');
            expect(req.request.withCredentials).toBe(true);
            req.flush(mockSuggestions);
        });

        it('should return empty array when API returns empty', () => {
            service.getRecommendations().subscribe((suggestions) => {
                expect(suggestions).toEqual([]);
            });

            const req = httpMock.expectOne((r) => r.url.includes('/api/recommendations'));
            req.flush([]);
        });

        it('should handle API error', () => {
            service.getRecommendations().subscribe({
                error: (err) => {
                    expect(err.status).toBe(403);
                },
            });

            const req = httpMock.expectOne((r) => r.url.includes('/api/recommendations'));
            req.flush({ message: 'Premium required' }, { status: 403, statusText: 'Forbidden' });
        });

        it('should handle unauthorized error', () => {
            service.getRecommendations().subscribe({
                error: (err) => {
                    expect(err.status).toBe(401);
                },
            });

            const req = httpMock.expectOne((r) => r.url.includes('/api/recommendations'));
            req.flush({ message: 'Unauthorized' }, { status: 401, statusText: 'Unauthorized' });
        });
    });
});
