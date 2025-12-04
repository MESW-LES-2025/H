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
    return this.http.get<boolean>(`${this.apiUrl}/eligibility/${universityId}`, { withCredentials: true });
  }

  addReview(review: Review): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, review, { withCredentials: true });
  }

  getCourseReviews(courseId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/course/${courseId}`);
  }

  checkCourseEligibility(courseId: number, userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/course/eligibility/${courseId}`, { withCredentials: true });
  }

  addCourseReview(review: Review): Observable<Review> {
    return this.http.post<Review>(`${this.apiUrl}/course`, review, { withCredentials: true });
  }

  deleteReview(reviewId: number, userId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${reviewId}?userId=${userId}`, { withCredentials: true });
  }

  deleteCourseReview(reviewId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/course/${reviewId}`, { withCredentials: true });
  }
}