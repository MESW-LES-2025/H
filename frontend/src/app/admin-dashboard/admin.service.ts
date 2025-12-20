import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserViewmodel } from '../profile-page/viewmodels/user-viewmodel';
import { UniversityLight } from '../universities/viewmodels/university-light';
import { CourseLight } from '../shared/viewmodels/course-light';
import { Review } from '../university-page/viewmodels/review';

export interface PopularItem {
  id: number;
  name: string;
  favoriteCount: number;
}

export interface Analytics {
  totalUsers: number;
  totalCourses: number;
  totalUniversities: number;
  totalCourseReviews: number;
  totalUniversityReviews: number;
  totalScholarships: number;
  popularCourses: PopularItem[];
  popularUniversities: PopularItem[];
}

export interface UniversityUpsert {
  id?: number;
  name: string;
  description?: string;
  contactInfo?: string;
  website?: string;
  address?: string;
  logo?: string;
  location?: { id: number } | null;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private base = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getUsers(): Observable<UserViewmodel[]> {
    return this.http.get<UserViewmodel[]>(`${this.base}/api/admin/users`, {
      withCredentials: true,
    });
  }

  getUniversities(): Observable<UniversityLight[]> {
    return this.http.get<UniversityLight[]>(
      `${this.base}/api/admin/universities`,
      {
        withCredentials: true,
      },
    );
  }

  getCourses(): Observable<CourseLight[]> {
    return this.http.get<CourseLight[]>(`${this.base}/api/admin/courses`, {
      withCredentials: true,
    });
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/users/${id}`, {
      withCredentials: true,
    });
  }

  getAnalytics(): Observable<Analytics> {
    return this.http.get<Analytics>(`${this.base}/api/admin/analytics`, {
      withCredentials: true,
    });
  }

  resetUserPassword(id: number): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.base}/api/admin/users/${id}/reset-password`,
      {},
      { withCredentials: true }
    );
  }

  getReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.base}/api/admin/reviews`, {
      withCredentials: true,
    });
  }

  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/reviews/${id}`, {
      withCredentials: true,
    });
  }

  getAll(): Observable<{
    users: UserViewmodel[];
    universities: UniversityLight[];
    courses: CourseLight[];
    reviews: Review[];
  }> {
    return forkJoin({
      users: this.getUsers(),
      universities: this.getUniversities(),
      courses: this.getCourses(),
      reviews: this.getReviews(),
    });
  }

  // create university
  createUniversity(payload: UniversityUpsert): Observable<UniversityLight> {
    return this.http.post<UniversityLight>(
      `${this.base}/api/admin/universities`,
      payload,
      { withCredentials: true },
    );
  }

  // update university
  updateUniversity(id: number, payload: UniversityUpsert): Observable<UniversityLight> {
    return this.http.put<UniversityLight>(
      `${this.base}/api/admin/universities/${id}`,
      payload,
      { withCredentials: true },
    );
  }

  // delete university
  deleteUniversity(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.base}/api/admin/universities/${id}`,
      { withCredentials: true },
    );
  }
}
