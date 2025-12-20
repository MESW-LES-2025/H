import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface SubscriptionRequest {
    paymentMethod: string;
}

export interface SubscriptionResponse {
    message: string;
    status: string;
    userRole?: string;
    premiumStartDate?: string;
}

@Injectable({ providedIn: 'root' })
export class SubscriptionService {
    private readonly baseUrl = environment.apiUrl;

    constructor(private http: HttpClient) { }

    subscribe(paymentMethod: string): Observable<SubscriptionResponse> {
        const request: SubscriptionRequest = { paymentMethod };
        return this.http.post<SubscriptionResponse>(
            `${this.baseUrl}/api/subscriptions`,
            request,
            { withCredentials: true }
        );
    }

    getStatus(): Observable<SubscriptionResponse> {
        return this.http.get<SubscriptionResponse>(
            `${this.baseUrl}/api/users/me/status`,
            { withCredentials: true }
        );
    }

    cancelSubscription(): Observable<SubscriptionResponse> {
        return this.http.delete<SubscriptionResponse>(
            `${this.baseUrl}/api/subscriptions`,
            { withCredentials: true }
        );
    }
}
