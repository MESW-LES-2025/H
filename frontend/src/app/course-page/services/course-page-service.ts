import { Injectable } from '@angular/core';
import { CourseViewmodel } from '../viewmodels/course-viewmodel';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CoursePageService {

  public getCourseProfile(id: number): Observable<CourseViewmodel> {
    return of();
  }
}
