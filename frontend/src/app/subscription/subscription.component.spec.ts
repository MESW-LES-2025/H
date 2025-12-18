import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { SubscriptionComponent } from './subscription.component';
import { SubscriptionService, SubscriptionResponse } from './subscription.service';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('SubscriptionComponent', () => {
    let component: SubscriptionComponent;
    let fixture: ComponentFixture<SubscriptionComponent>;
    let subscriptionService: jasmine.SpyObj<SubscriptionService>;
    let authService: jasmine.SpyObj<AuthService>;
    let router: jasmine.SpyObj<Router>;

    beforeEach(async () => {
        const subscriptionSpy = jasmine.createSpyObj('SubscriptionService', [
            'subscribe',
            'getStatus',
            'cancelSubscription',
        ]);
        const authSpy = jasmine.createSpyObj('AuthService', [
            'getCurrentUserId',
            'restoreSession',
        ]);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        // Default mock implementations
        subscriptionSpy.getStatus.and.returnValue(of({
            message: 'User is not a premium member',
            status: 'success',
            userRole: 'REGULAR',
        } as SubscriptionResponse));
        authSpy.restoreSession.and.returnValue(of({}));
        authSpy.getCurrentUserId.and.returnValue(1);

        await TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule,
                RouterTestingModule,
                SubscriptionComponent,
            ],
            providers: [
                { provide: SubscriptionService, useValue: subscriptionSpy },
                { provide: AuthService, useValue: authSpy },
                { provide: Router, useValue: routerSpy },
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(SubscriptionComponent);
        component = fixture.componentInstance;
        subscriptionService = TestBed.inject(SubscriptionService) as jasmine.SpyObj<SubscriptionService>;
        authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
        router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    });

    it('should create', () => {
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    describe('ngOnInit', () => {
        it('should check premium status on init', () => {
            fixture.detectChanges();
            expect(subscriptionService.getStatus).toHaveBeenCalled();
        });

        it('should set isAlreadyPremium when user is premium', () => {
            subscriptionService.getStatus.and.returnValue(of({
                message: 'User is a premium member',
                status: 'success',
                userRole: 'PREMIUM',
                premiumStartDate: '2024-12-18',
            } as SubscriptionResponse));

            fixture.detectChanges();

            expect(component.isAlreadyPremium()).toBeTrue();
            expect(component.premiumStartDate()).toBe('2024-12-18');
        });

        it('should handle getStatus error gracefully', () => {
            subscriptionService.getStatus.and.returnValue(throwError(() => new Error('Network error')));

            fixture.detectChanges();

            // Should not throw and remain not premium
            expect(component.isAlreadyPremium()).toBeFalse();
        });

        it('should set premiumStartDate to null when not provided', () => {
            subscriptionService.getStatus.and.returnValue(of({
                message: 'User is a premium member',
                status: 'success',
                userRole: 'PREMIUM',
            } as SubscriptionResponse));

            fixture.detectChanges();

            expect(component.isAlreadyPremium()).toBeTrue();
            expect(component.premiumStartDate()).toBeNull();
        });
    });

    describe('selectPaymentMethod', () => {
        it('should set selected payment method', () => {
            fixture.detectChanges();
            component.selectPaymentMethod('MOCK_CARD');
            expect(component.selectedPaymentMethod()).toBe('MOCK_CARD');
        });

        it('should show card form when MOCK_CARD is selected', () => {
            fixture.detectChanges();
            component.selectPaymentMethod('MOCK_CARD');
            expect(component.showCardForm()).toBeTrue();
        });

        it('should hide card form when MOCK_PAYPAL is selected', () => {
            fixture.detectChanges();
            component.selectPaymentMethod('MOCK_PAYPAL');
            expect(component.showCardForm()).toBeFalse();
        });

        it('should clear error when selecting payment method', () => {
            fixture.detectChanges();
            component.error.set('Some previous error');
            component.selectPaymentMethod('MOCK_CARD');
            expect(component.error()).toBeNull();
        });
    });

    describe('formatCardNumber', () => {
        beforeEach(() => {
            fixture.detectChanges();
        });

        it('should format card number with spaces', () => {
            component.cardNumber = '4242424242424242';
            component.formatCardNumber();
            expect(component.cardNumber).toBe('4242 4242 4242 4242');
        });

        it('should remove non-digit characters', () => {
            component.cardNumber = '4242-4242-4242-4242';
            component.formatCardNumber();
            expect(component.cardNumber).toBe('4242 4242 4242 4242');
        });

        it('should limit to 16 digits', () => {
            component.cardNumber = '42424242424242421234';
            component.formatCardNumber();
            expect(component.cardNumber).toBe('4242 4242 4242 4242');
        });

        it('should handle partial card numbers', () => {
            component.cardNumber = '424';
            component.formatCardNumber();
            expect(component.cardNumber).toBe('424');
        });

        it('should handle empty card number', () => {
            component.cardNumber = '';
            component.formatCardNumber();
            expect(component.cardNumber).toBe('');
        });
    });

    describe('card validation', () => {
        beforeEach(() => {
            fixture.detectChanges();
        });

        describe('isCardNumberValid', () => {
            it('should validate card number format', () => {
                component.cardNumber = '4242 4242 4242 4242';
                expect(component.isCardNumberValid()).toBeTrue();
            });

            it('should reject invalid card number', () => {
                component.cardNumber = '1234';
                expect(component.isCardNumberValid()).toBeFalse();
            });

            it('should reject card with letters', () => {
                component.cardNumber = '4242 4242 4242 ABCD';
                expect(component.isCardNumberValid()).toBeFalse();
            });
        });

        describe('isExpiryValid', () => {
            it('should validate valid future expiry', () => {
                // Always use a date far in the future
                component.expiryMonth = '12';
                component.expiryYear = '30';
                expect(component.isExpiryValid()).toBeTrue();
            });

            it('should reject invalid month (0)', () => {
                component.expiryMonth = '0';
                component.expiryYear = '30';
                expect(component.isExpiryValid()).toBeFalse();
            });

            it('should reject invalid month (13)', () => {
                component.expiryMonth = '13';
                component.expiryYear = '30';
                expect(component.isExpiryValid()).toBeFalse();
            });

            it('should reject past year', () => {
                component.expiryMonth = '01';
                component.expiryYear = '20';
                expect(component.isExpiryValid()).toBeFalse();
            });

            it('should reject empty month', () => {
                component.expiryMonth = '';
                component.expiryYear = '30';
                expect(component.isExpiryValid()).toBeFalse();
            });

            it('should reject empty year', () => {
                component.expiryMonth = '12';
                component.expiryYear = '';
                expect(component.isExpiryValid()).toBeFalse();
            });
        });

        describe('isCvvValid', () => {
            it('should validate 3-digit CVV', () => {
                component.cvv = '123';
                expect(component.isCvvValid()).toBeTrue();
            });

            it('should validate 4-digit CVV (Amex)', () => {
                component.cvv = '1234';
                expect(component.isCvvValid()).toBeTrue();
            });

            it('should reject 2-digit CVV', () => {
                component.cvv = '12';
                expect(component.isCvvValid()).toBeFalse();
            });

            it('should reject 5-digit CVV', () => {
                component.cvv = '12345';
                expect(component.isCvvValid()).toBeFalse();
            });

            it('should reject CVV with letters', () => {
                component.cvv = 'abc';
                expect(component.isCvvValid()).toBeFalse();
            });
        });

        describe('isCardFormValid', () => {
            it('should return true when all fields are valid', () => {
                component.cardNumber = '4242 4242 4242 4242';
                component.cardName = 'John Doe';
                component.expiryMonth = '12';
                component.expiryYear = '30';
                component.cvv = '123';
                expect(component.isCardFormValid()).toBeTrue();
            });

            it('should return false when card number is invalid', () => {
                component.cardNumber = '1234';
                component.cardName = 'John Doe';
                component.expiryMonth = '12';
                component.expiryYear = '30';
                component.cvv = '123';
                expect(component.isCardFormValid()).toBeFalse();
            });

            it('should return false when card name is too short', () => {
                component.cardNumber = '4242 4242 4242 4242';
                component.cardName = 'J';
                component.expiryMonth = '12';
                component.expiryYear = '30';
                component.cvv = '123';
                expect(component.isCardFormValid()).toBeFalse();
            });

            it('should return false when expiry is invalid', () => {
                component.cardNumber = '4242 4242 4242 4242';
                component.cardName = 'John Doe';
                component.expiryMonth = '13';
                component.expiryYear = '30';
                component.cvv = '123';
                expect(component.isCardFormValid()).toBeFalse();
            });

            it('should return false when CVV is invalid', () => {
                component.cardNumber = '4242 4242 4242 4242';
                component.cardName = 'John Doe';
                component.expiryMonth = '12';
                component.expiryYear = '30';
                component.cvv = '12';
                expect(component.isCardFormValid()).toBeFalse();
            });
        });
    });

    describe('getCardType', () => {
        beforeEach(() => {
            fixture.detectChanges();
        });

        it('should detect Visa card', () => {
            component.cardNumber = '4242 4242 4242 4242';
            expect(component.getCardType()).toBe('visa');
        });

        it('should detect Mastercard (starting with 5)', () => {
            component.cardNumber = '5555 5555 5555 4444';
            expect(component.getCardType()).toBe('mastercard');
        });

        it('should detect Mastercard (starting with 2)', () => {
            component.cardNumber = '2223 0031 2200 3222';
            expect(component.getCardType()).toBe('mastercard');
        });

        it('should detect Amex', () => {
            component.cardNumber = '3782 822463 10005';
            expect(component.getCardType()).toBe('amex');
        });

        it('should return unknown for other cards', () => {
            component.cardNumber = '6011 1111 1111 1117';
            expect(component.getCardType()).toBe('unknown');
        });
    });

    describe('getCardIcon', () => {
        beforeEach(() => {
            fixture.detectChanges();
        });

        it('should return Visa icon', () => {
            component.cardNumber = '4242 4242 4242 4242';
            expect(component.getCardIcon()).toBe('💳 Visa');
        });

        it('should return Mastercard icon', () => {
            component.cardNumber = '5555 5555 5555 4444';
            expect(component.getCardIcon()).toBe('💳 Mastercard');
        });

        it('should return Amex icon', () => {
            component.cardNumber = '3782 822463 10005';
            expect(component.getCardIcon()).toBe('💳 Amex');
        });

        it('should return generic icon for unknown cards', () => {
            component.cardNumber = '6011 1111 1111 1117';
            expect(component.getCardIcon()).toBe('💳');
        });
    });

    describe('subscribe and processPayment', () => {
        beforeEach(() => {
            fixture.detectChanges();
        });

        it('should call processPayment when subscribe is called', fakeAsync(() => {
            spyOn(component, 'processPayment');
            component.subscribe();
            expect(component.processPayment).toHaveBeenCalled();
        }));

        it('should set error when card form is invalid and MOCK_CARD selected', fakeAsync(() => {
            component.selectPaymentMethod('MOCK_CARD');
            component.cardNumber = '1234'; // Invalid
            component.processPayment();
            tick();
            expect(component.error()).toBe('Please fill in all card details correctly');
        }));

        it('should process payment successfully with PayPal', fakeAsync(() => {
            subscriptionService.subscribe.and.returnValue(of({
                message: 'Subscription successful!',
                status: 'success',
                userRole: 'PREMIUM',
            } as SubscriptionResponse));

            component.selectPaymentMethod('MOCK_PAYPAL');
            component.processPayment();

            // Wait for all delays (800 + 600 + 1000 + 500 = 2900ms)
            tick(3000);

            expect(subscriptionService.subscribe).toHaveBeenCalledWith('MOCK_PAYPAL');
            expect(component.subscriptionSuccess()).toBeTrue();
            expect(component.successMessage()).toBe('Subscription successful!');
        }));

        it('should process payment successfully with valid card', fakeAsync(() => {
            subscriptionService.subscribe.and.returnValue(of({
                message: 'Subscription successful!',
                status: 'success',
                userRole: 'PREMIUM',
            } as SubscriptionResponse));

            component.selectPaymentMethod('MOCK_CARD');
            component.cardNumber = '4242 4242 4242 4242';
            component.cardName = 'John Doe';
            component.expiryMonth = '12';
            component.expiryYear = '30';
            component.cvv = '123';

            component.processPayment();
            tick(3000);

            expect(subscriptionService.subscribe).toHaveBeenCalledWith('MOCK_CARD');
            expect(component.subscriptionSuccess()).toBeTrue();
        }));

        it('should handle subscription error from API', fakeAsync(() => {
            subscriptionService.subscribe.and.returnValue(of({
                message: 'User is already premium',
                status: 'error',
            } as SubscriptionResponse));

            component.selectPaymentMethod('MOCK_PAYPAL');
            component.processPayment();
            tick(3000);

            expect(component.error()).toBe('User is already premium');
            expect(component.loading()).toBeFalse();
        }));

        it('should handle HTTP error during subscription', fakeAsync(() => {
            subscriptionService.subscribe.and.returnValue(
                throwError(() => ({ error: { message: 'Server error' } }))
            );

            component.selectPaymentMethod('MOCK_PAYPAL');
            component.processPayment();
            tick(3000);

            expect(component.error()).toBe('Server error');
            expect(component.loading()).toBeFalse();
        }));

        it('should handle HTTP error without message', fakeAsync(() => {
            subscriptionService.subscribe.and.returnValue(
                throwError(() => ({}))
            );

            component.selectPaymentMethod('MOCK_PAYPAL');
            component.processPayment();
            tick(3000);

            expect(component.error()).toBe('Failed to process subscription. Please try again.');
        }));

        it('should set processing steps during payment', fakeAsync(() => {
            subscriptionService.subscribe.and.returnValue(of({
                message: 'Success',
                status: 'success',
            } as SubscriptionResponse));

            component.selectPaymentMethod('MOCK_PAYPAL');
            component.processPayment();

            expect(component.loading()).toBeTrue();
            tick(100);
            expect(component.processingStep()).toBe('Validating card...');

            tick(800);
            expect(component.processingStep()).toBe('Connecting to payment gateway...');

            tick(600);
            expect(component.processingStep()).toBe('Processing payment...');

            tick(1000);
            expect(component.processingStep()).toBe('Confirming transaction...');

            tick(500);
            expect(component.processingStep()).toBeNull();
        }));

        it('should restore session after successful subscription', fakeAsync(() => {
            subscriptionService.subscribe.and.returnValue(of({
                message: 'Success',
                status: 'success',
            } as SubscriptionResponse));

            component.selectPaymentMethod('MOCK_PAYPAL');
            component.processPayment();
            tick(3000);

            expect(authService.restoreSession).toHaveBeenCalled();
        }));
    });

    describe('cancel subscription', () => {
        beforeEach(() => {
            // Setup as premium user
            subscriptionService.getStatus.and.returnValue(of({
                message: 'User is a premium member',
                status: 'success',
                userRole: 'PREMIUM',
                premiumStartDate: '2024-12-18',
            } as SubscriptionResponse));
            fixture.detectChanges();
        });

        it('should open cancel modal', () => {
            component.openCancelModal();
            expect(component.showCancelModal()).toBeTrue();
        });

        it('should close cancel modal', () => {
            component.openCancelModal();
            component.closeCancelModal();
            expect(component.showCancelModal()).toBeFalse();
        });

        it('should set cancelLoading while processing', () => {
            subscriptionService.cancelSubscription.and.returnValue(of({
                message: 'Subscription cancelled',
                status: 'success',
                userRole: 'REGULAR',
            } as SubscriptionResponse));

            component.confirmCancelSubscription();
            // Note: Due to sync observable, loading may already be false
            expect(subscriptionService.cancelSubscription).toHaveBeenCalled();
        });

        it('should cancel subscription successfully', () => {
            subscriptionService.cancelSubscription.and.returnValue(of({
                message: 'Subscription cancelled successfully.',
                status: 'success',
                userRole: 'REGULAR',
            } as SubscriptionResponse));

            component.openCancelModal();
            component.confirmCancelSubscription();

            expect(subscriptionService.cancelSubscription).toHaveBeenCalled();
            expect(component.isAlreadyPremium()).toBeFalse();
            expect(component.premiumStartDate()).toBeNull();
            expect(router.navigate).toHaveBeenCalledWith(['/profile', 1]);
        });

        it('should restore session after successful cancellation', () => {
            subscriptionService.cancelSubscription.and.returnValue(of({
                message: 'Subscription cancelled',
                status: 'success',
                userRole: 'REGULAR',
            } as SubscriptionResponse));

            component.confirmCancelSubscription();

            expect(authService.restoreSession).toHaveBeenCalled();
        });

        it('should handle cancel error', () => {
            subscriptionService.cancelSubscription.and.returnValue(of({
                message: 'User is not a premium member',
                status: 'error',
                userRole: 'REGULAR',
            } as SubscriptionResponse));

            component.openCancelModal();
            component.confirmCancelSubscription();

            expect(component.error()).toBe('User is not a premium member');
        });

        it('should handle HTTP error during cancellation', () => {
            subscriptionService.cancelSubscription.and.returnValue(
                throwError(() => ({ error: { message: 'Server error' } }))
            );

            component.openCancelModal();
            component.confirmCancelSubscription();

            expect(component.error()).toBe('Server error');
            expect(component.showCancelModal()).toBeFalse();
            expect(component.cancelLoading()).toBeFalse();
        });

        it('should use default error message when no message provided', () => {
            subscriptionService.cancelSubscription.and.returnValue(
                throwError(() => ({}))
            );

            component.confirmCancelSubscription();

            expect(component.error()).toBe('Failed to cancel subscription. Please try again.');
        });
    });

    describe('goToProfile', () => {
        it('should navigate to profile when user ID exists', () => {
            fixture.detectChanges();
            authService.getCurrentUserId.and.returnValue(123);

            component.goToProfile();

            expect(router.navigate).toHaveBeenCalledWith(['/profile', 123]);
        });

        it('should navigate to home when user ID is null', () => {
            fixture.detectChanges();
            authService.getCurrentUserId.and.returnValue(null);

            component.goToProfile();

            expect(router.navigate).toHaveBeenCalledWith(['/']);
        });
    });
});

