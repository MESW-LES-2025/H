import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from '../viewmodels/review';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/api/reviews`;

  constructor(private http: HttpClient) { }

  getReviews(universityId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/university/${universityId}`);
  }

  checkEligibility(universityId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/eligibility/${universityId}?userId=${userId}`);
  }

  addReview(review: Review): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, review, { withCredentials: true });
  }
}