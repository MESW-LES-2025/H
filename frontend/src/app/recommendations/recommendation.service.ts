import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface Suggestion {
    id: number;
    title: string;
    description: string;
    type: 'course' | 'university';
    matchScore: number;
    imageUrl?: string;
    location?: string;
    courseType?: string;
    universityName?: string;
}

export interface RecommendationError {
    message: string;
    requiresPremium?: boolean;
}

@Injectable({ providedIn: 'root' })
export class RecommendationService {
    private baseUrl = environment.apiUrl;

    constructor(private http: HttpClient) { }

    getRecommendations(): Observable<Suggestion[]> {
        return this.http.get<Suggestion[]>(`${this.baseUrl}/api/recommendations`, {
            withCredentials: true,
        });
    }
}
