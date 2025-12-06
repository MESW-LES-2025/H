import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserViewmodel } from '../profile-page/viewmodels/user-viewmodel';
import { UniversityLight } from '../universities/viewmodels/university-light';
import { CourseLight } from '../shared/viewmodels/course-light';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private base = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<UserViewmodel[]> {
    return this.http.get<UserViewmodel[]>(`${this.base}/api/admin/users`);
  }

  getUniversities(): Observable<UniversityLight[]> {
    return this.http.get<UniversityLight[]>(
      `${this.base}/api/admin/universities`,
    );
  }

  getCourses(): Observable<CourseLight[]> {
    return this.http.get<CourseLight[]>(`${this.base}/api/admin/courses`);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/admin/users/${id}`);
  }

  getAll(): Observable<{
    users: UserViewmodel[];
    universities: UniversityLight[];
    courses: CourseLight[];
  }> {
    return forkJoin({
      users: this.getUsers(),
      universities: this.getUniversities(),
      courses: this.getCourses(),
    });
  }
}
