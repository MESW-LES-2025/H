import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { SubscriptionService } from './subscription.service';
import { AuthService } from '../auth/auth.service';

@Component({
    selector: 'app-subscription',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './subscription.component.html',
    styleUrl: './subscription.component.css',
})
export class SubscriptionComponent implements OnInit {
    loading = signal(false);
    error = signal<string | null>(null);
    selectedPaymentMethod = signal<string | null>(null);
    subscriptionSuccess = signal(false);
    successMessage = signal('');
    isAlreadyPremium = signal(false);
    premiumStartDate = signal<string | null>(null);
    showCardForm = signal(false);
    processingStep = signal<string | null>(null);

    // Card form fields
    cardNumber = '';
    cardName = '';
    expiryMonth = '';
    expiryYear = '';
    cvv = '';

    constructor(
        private subscriptionService: SubscriptionService,
        private authService: AuthService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.checkPremiumStatus();
    }

    private checkPremiumStatus(): void {
        this.subscriptionService.getStatus().subscribe({
            next: (response) => {
                if (response.userRole === 'PREMIUM') {
                    this.isAlreadyPremium.set(true);
                    this.premiumStartDate.set(response.premiumStartDate || null);
                }
            },
            error: () => {
                // User might not be logged in, ignore error
            }
        });
    }

    selectPaymentMethod(method: string): void {
        this.selectedPaymentMethod.set(method);
        this.showCardForm.set(method === 'MOCK_CARD');
        this.error.set(null);
    }

    // Format card number with spaces (4-4-4-4)
    formatCardNumber(): void {
        let value = this.cardNumber.replace(/\s/g, '').replace(/\D/g, '');
        value = value.substring(0, 16);
        const parts = value.match(/.{1,4}/g) || [];
        this.cardNumber = parts.join(' ');
    }

    // Validate card number (simple Luhn check for realism)
    isCardNumberValid(): boolean {
        const num = this.cardNumber.replace(/\s/g, '');
        return num.length === 16 && /^\d+$/.test(num);
    }

    isExpiryValid(): boolean {
        const month = parseInt(this.expiryMonth, 10);
        const year = parseInt(this.expiryYear, 10);
        if (!month || !year) return false;
        if (month < 1 || month > 12) return false;

        const now = new Date();
        const currentYear = now.getFullYear() % 100;
        const currentMonth = now.getMonth() + 1;

        if (year < currentYear) return false;
        if (year === currentYear && month < currentMonth) return false;

        return true;
    }

    isCvvValid(): boolean {
        return /^\d{3,4}$/.test(this.cvv);
    }

    isCardFormValid(): boolean {
        return this.isCardNumberValid() &&
            this.cardName.trim().length >= 2 &&
            this.isExpiryValid() &&
            this.isCvvValid();
    }

    getCardType(): string {
        const num = this.cardNumber.replace(/\s/g, '');
        if (num.startsWith('4')) return 'visa';
        if (num.startsWith('5') || num.startsWith('2')) return 'mastercard';
        if (num.startsWith('3')) return 'amex';
        return 'unknown';
    }

    getCardIcon(): string {
        const type = this.getCardType();
        switch (type) {
            case 'visa': return '💳 Visa';
            case 'mastercard': return '💳 Mastercard';
            case 'amex': return '💳 Amex';
            default: return '💳';
        }
    }

    async processPayment(): Promise<void> {
        if (this.selectedPaymentMethod() === 'MOCK_CARD' && !this.isCardFormValid()) {
            this.error.set('Please fill in all card details correctly');
            return;
        }

        this.loading.set(true);
        this.error.set(null);

        try {
            // Simulate payment processing steps
            this.processingStep.set('Validating card...');
            await this.delay(800);

            this.processingStep.set('Connecting to payment gateway...');
            await this.delay(600);

            this.processingStep.set('Processing payment...');
            await this.delay(1000);

            this.processingStep.set('Confirming transaction...');
            await this.delay(500);

            // Call actual subscription API
            this.subscriptionService.subscribe(this.selectedPaymentMethod()!).subscribe({
                next: (response) => {
                    this.loading.set(false);
                    this.processingStep.set(null);
                    if (response.status === 'success') {
                        this.subscriptionSuccess.set(true);
                        this.successMessage.set(response.message);
                        // Refresh user session to update role
                        this.authService.restoreSession().subscribe();
                    } else {
                        this.error.set(response.message);
                    }
                },
                error: (err) => {
                    this.loading.set(false);
                    this.processingStep.set(null);
                    this.error.set(err.error?.message || 'Failed to process subscription. Please try again.');
                }
            });
        } catch {
            this.loading.set(false);
            this.processingStep.set(null);
            this.error.set('Payment processing failed. Please try again.');
        }
    }

    private delay(ms: number): Promise<void> {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    subscribe(): void {
        this.processPayment();
    }

    goToProfile(): void {
        const userId = this.authService.getCurrentUserId();
        if (userId) {
            this.router.navigate(['/profile', userId]);
        } else {
            this.router.navigate(['/']);
        }
    }

    // Cancel subscription modal state
    showCancelModal = signal(false);
    cancelLoading = signal(false);

    openCancelModal(): void {
        this.showCancelModal.set(true);
    }

    closeCancelModal(): void {
        this.showCancelModal.set(false);
    }

    confirmCancelSubscription(): void {
        this.cancelLoading.set(true);
        this.subscriptionService.cancelSubscription().subscribe({
            next: (response) => {
                this.cancelLoading.set(false);
                this.showCancelModal.set(false);
                if (response.status === 'success') {
                    this.isAlreadyPremium.set(false);
                    this.premiumStartDate.set(null);
                    // Refresh user session to update role
                    this.authService.restoreSession().subscribe();
                    // Redirect to profile
                    this.goToProfile();
                } else {
                    this.error.set(response.message);
                }
            },
            error: (err) => {
                this.cancelLoading.set(false);
                this.showCancelModal.set(false);
                this.error.set(err.error?.message || 'Failed to cancel subscription. Please try again.');
            }
        });
    }
}
