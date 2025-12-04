import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { UniversityDTO, CollegeVM, toCollegeVM, UniversityFilters } from '../viewmodels/explore-viewmodel';
import { Page, PageRequest } from '../../shared/viewmodels/pagination';

@Injectable({ providedIn: 'root' })
export class ExploreService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  search(
    query: string,
    country: string,
    costMax: number | null,
    scholarship: string,
    pageRequest: PageRequest
  ): Observable<Page<CollegeVM>> {
    let params = new HttpParams()
      .set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString());

    if (pageRequest.sort) {
      params = params.set('sort', pageRequest.sort);
    }

    if (query && query.trim() !== '') {
      params = params.set('name', query.trim());
    }

    if (country && country !== 'Any') {
      params = params.append('countries', country);
    }

    if (costMax !== null) {
      params = params.set('costOfLivingMax', costMax.toString());
    }

    if (scholarship !== 'Any') {
      const hasScholarship = scholarship === 'Yes';
      params = params.set('hasScholarship', String(hasScholarship));
    }

    return this.http.get<Page<UniversityDTO>>(`${this.baseUrl}/api/university`, { params })
      .pipe(
        map(page => ({
          content: page.content.map(toCollegeVM),
          totalElements: page.totalElements,
          totalPages: page.totalPages,
          size: page.size,
          number: page.number
        }))
      );
  }
}
