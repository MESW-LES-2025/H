import { TestBed } from '@angular/core/testing';
import {
    HttpClientTestingModule,
    HttpTestingController,
} from '@angular/common/http/testing';
import { SubscriptionService, SubscriptionResponse } from './subscription.service';
import { environment } from '../../environments/environment';

describe('SubscriptionService', () => {
    let service: SubscriptionService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [SubscriptionService],
        });

        service = TestBed.inject(SubscriptionService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    describe('subscribe', () => {
        it('should send subscription request with payment method', (done) => {
            const mockResponse: SubscriptionResponse = {
                message: 'Subscription successful! Welcome to Premium.',
                status: 'success',
                userRole: 'PREMIUM',
                premiumStartDate: '2024-12-18',
            };

            service.subscribe('MOCK_CARD').subscribe((response) => {
                expect(response).toEqual(mockResponse);
                expect(response.status).toBe('success');
                done();
            });

            const req = httpMock.expectOne(`${environment.apiUrl}/api/subscriptions`);
            expect(req.request.method).toBe('POST');
            expect(req.request.body).toEqual({ paymentMethod: 'MOCK_CARD' });
            expect(req.request.withCredentials).toBe(true);

            req.flush(mockResponse);
        });

        it('should handle subscription error', (done) => {
            const errorResponse: SubscriptionResponse = {
                message: 'User is already a premium member',
                status: 'error',
            };

            service.subscribe('MOCK_CARD').subscribe((response) => {
                expect(response.status).toBe('error');
                done();
            });

            const req = httpMock.expectOne(`${environment.apiUrl}/api/subscriptions`);
            req.flush(errorResponse);
        });
    });

    describe('getStatus', () => {
        it('should get subscription status for premium user', (done) => {
            const mockResponse: SubscriptionResponse = {
                message: 'User is a premium member',
                status: 'success',
                userRole: 'PREMIUM',
                premiumStartDate: '2024-12-18',
            };

            service.getStatus().subscribe((response) => {
                expect(response).toEqual(mockResponse);
                expect(response.userRole).toBe('PREMIUM');
                done();
            });

            const req = httpMock.expectOne(`${environment.apiUrl}/api/users/me/status`);
            expect(req.request.method).toBe('GET');
            expect(req.request.withCredentials).toBe(true);

            req.flush(mockResponse);
        });

        it('should get subscription status for regular user', (done) => {
            const mockResponse: SubscriptionResponse = {
                message: 'User is not a premium member',
                status: 'success',
                userRole: 'REGULAR',
            };

            service.getStatus().subscribe((response) => {
                expect(response.userRole).toBe('REGULAR');
                done();
            });

            const req = httpMock.expectOne(`${environment.apiUrl}/api/users/me/status`);
            req.flush(mockResponse);
        });
    });

    describe('cancelSubscription', () => {
        it('should cancel subscription successfully', (done) => {
            const mockResponse: SubscriptionResponse = {
                message: 'Subscription cancelled successfully. You have been downgraded to a free account.',
                status: 'success',
                userRole: 'REGULAR',
            };

            service.cancelSubscription().subscribe((response) => {
                expect(response).toEqual(mockResponse);
                expect(response.status).toBe('success');
                expect(response.userRole).toBe('REGULAR');
                done();
            });

            const req = httpMock.expectOne(`${environment.apiUrl}/api/subscriptions`);
            expect(req.request.method).toBe('DELETE');
            expect(req.request.withCredentials).toBe(true);

            req.flush(mockResponse);
        });

        it('should handle cancel error when user is not premium', (done) => {
            const errorResponse: SubscriptionResponse = {
                message: 'User is not a premium member',
                status: 'error',
                userRole: 'REGULAR',
            };

            service.cancelSubscription().subscribe((response) => {
                expect(response.status).toBe('error');
                done();
            });

            const req = httpMock.expectOne(`${environment.apiUrl}/api/subscriptions`);
            req.flush(errorResponse);
        });

        it('should handle HTTP error during cancellation', (done) => {
            service.cancelSubscription().subscribe({
                next: () => fail('should have failed'),
                error: (error) => {
                    expect(error).toBeTruthy();
                    done();
                },
            });

            const req = httpMock.expectOne(`${environment.apiUrl}/api/subscriptions`);
            req.error(new ProgressEvent('error'), { status: 500 });
        });
    });
});
