import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CourseViewmodel } from '../viewmodels/course-viewmodel';
import { Page, PageRequest } from '../../shared/viewmodels/pagination';
import { CourseFilters } from '../viewmodels/course-filters';

@Injectable({ providedIn: 'root' })
export class CoursesService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getCourses(
    filters: CourseFilters,
    pageRequest: PageRequest,
  ): Observable<Page<CourseViewmodel>> {
    let params = new HttpParams()
      .set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString());

    if (pageRequest.sort) {
      params = params.set('sort', pageRequest.sort);
    }

    if (filters.name != null && filters.name !== '') {
      params = params.set('name', filters.name);
    }

    if (filters.courseTypes && filters.courseTypes.length > 0) {
      filters.courseTypes.forEach((type) => {
        params = params.append('courseTypes', type);
      });
    }

    if (filters.onlyRemote !== undefined && filters.onlyRemote !== null) {
      params = params.set('onlyRemote', String(filters.onlyRemote));
    }

    if (filters.costMax != null) {
      params = params.set('maxCost', filters.costMax.toString());
    }

    if (filters.duration != null) {
      params = params.set('duration', filters.duration.toString());
    }

    if (filters.languages && filters.languages.length > 0) {
      filters.languages.forEach((lang) => {
        params = params.append('languages', lang);
      });
    }

    if (filters.countries && filters.countries.length > 0) {
      filters.countries.forEach((country) => {
        params = params.append('countries', country);
      });
    }

    if (filters.areasOfStudy && filters.areasOfStudy.length > 0) {
      filters.areasOfStudy.forEach((area) => {
        params = params.append('areasOfStudy', area);
      });
    }

    return this.http.get<Page<CourseViewmodel>>(
      `${this.baseUrl}/api/courses`,
      { params },
    );
  }
}
