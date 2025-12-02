import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CourseViewmodel } from '../viewmodels/course-viewmodel';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CoursePageService {

  constructor(private http: HttpClient) {}

  public getCourseProfile(id: number): Observable<CourseViewmodel> {
    return this.http.get<any>(`${environment.apiUrl}/api/courses/${id}`).pipe(
      map(dto => {
        return {
          ...dto,
          level: dto.courseType ? dto.courseType.charAt(0) + dto.courseType.slice(1).toLowerCase() : 'N/A',
                    topics: dto.areasOfStudy ? dto.areasOfStudy.map((a: any) => a.name) : [],
          
          requirements: dto.minAdmissionGrade 
            ? [`Minimum Admission Grade: ${dto.minAdmissionGrade}`] 
            : ['No specific requirements listed.']
        } as CourseViewmodel;
      })
    );
  }
}
