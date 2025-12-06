import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {
  ScholarshipDTO,
  ScholarshipVM,
  toScholarshipVM,
} from '../viewmodels/scholarship-viewmodel';
import { Page, PageRequest } from '../../shared/viewmodels/pagination';

@Injectable({ providedIn: 'root' })
export class ScholarshipService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  search(
    query: string,
    courseType: string,
    minAmount: number | null,
    maxAmount: number | null,
    pageRequest: PageRequest,
  ): Observable<Page<ScholarshipVM>> {
    let params = new HttpParams()
      .set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString());

    if (pageRequest.sort) {
      params = params.set('sort', pageRequest.sort);
    }

    if (query && query.trim() !== '') {
      params = params.set('search', query.trim());
    }

    if (courseType && courseType !== 'Any') {
      params = params.set('courseType', courseType);
    }

    if (minAmount !== null) {
      params = params.set('minAmount', minAmount.toString());
    }

    if (maxAmount !== null) {
      params = params.set('maxAmount', maxAmount.toString());
    }

    return this.http
      .get<Page<ScholarshipDTO>>(`${this.baseUrl}/api/scholarship`, { params })
      .pipe(
        map((page) => ({
          content: page.content.map(toScholarshipVM),
          totalElements: page.totalElements,
          totalPages: page.totalPages,
          size: page.size,
          number: page.number,
        })),
      );
  }
}
