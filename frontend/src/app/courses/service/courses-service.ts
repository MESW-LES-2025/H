import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CourseViewmodel } from '../viewmodels/course-viewmodel';

@Injectable({ providedIn: 'root' })
export class CoursesService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Retrieves all available courses from the backend.
   * @returns Observable that emits an array of CourseViewmodel objects
   */
  public getAllCourses(): Observable<CourseViewmodel[]> {
    return this.http.get<CourseViewmodel[]>(`${this.baseUrl}/api/courses`);
  }
}
