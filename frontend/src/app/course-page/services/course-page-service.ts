import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CourseViewmodel } from '../viewmodels/course-viewmodel';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CoursePageService {

  constructor(private http: HttpClient) {}

  public getCourseProfile(id: number): Observable<CourseViewmodel> {
    return this.http.get<CourseViewmodel>(`${environment.apiUrl}/api/courses/${id}`);
  }
}
