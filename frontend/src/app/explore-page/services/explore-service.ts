import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { UniversityDTO, CollegeVM } from '../viewmodels/explore-viewmodel';
import { Page, PageRequest } from '../../shared/viewmodels/pagination';

export interface FavoritesResponse {
  universities: { id: number }[];
  courses: { id: number }[];
}

@Injectable({ providedIn: 'root' })
export class ExploreService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  search(
    query: string,
    country: string,
    costMax: number | null,
    scholarship: string,
    pageRequest: PageRequest,
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

    return this.http
      .get<Page<UniversityDTO>>(`${this.baseUrl}/api/university`, { params })
      .pipe(
        map((page) => ({
          content: page.content.map(this.toCollegeVM),
          totalElements: page.totalElements,
          totalPages: page.totalPages,
          size: page.size,
          number: page.number,
        })),
      );
  }

  toCollegeVM(dto: UniversityDTO): CollegeVM {
    return {
      id: dto.id.toString(),
      title: dto.name,
      blurb: dto.description || 'No description available',
      photo:
        'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop',
      color: '#7DB19F',
      country: dto.location?.country || 'Unknown',
      city: dto.location?.city || 'Unknown',
      costOfLiving: dto.location?.costOfLiving || 0,
      isFavorite: false,
    };
  }

  addFavoriteUniversity(id: number): Observable<void> {
    const storedId = localStorage.getItem('userId');
    if (!storedId) {
      return throwError(() => new Error('User not logged in'));
    }

    const params = new HttpParams().set('userId', storedId);

    return this.http.post<void>(
      `${this.baseUrl}/api/favorites/universities/${id}`,
      {},
      { params },
    );
  }

  removeFavoriteUniversity(id: number): Observable<void> {
    const storedId = localStorage.getItem('userId');
    if (!storedId) {
      return throwError(() => new Error('User not logged in'));
    }

    const params = new HttpParams().set('userId', storedId);

    return this.http.delete<void>(
      `${this.baseUrl}/api/favorites/universities/${id}`,
      { params },
    );
  }

  getFavorites(): Observable<FavoritesResponse> {
    const storedId = localStorage.getItem('userId');
    if (!storedId) {
      return throwError(() => new Error('User not logged in'));
    }

    const params = new HttpParams().set('userId', storedId);

    return this.http.get<FavoritesResponse>(`${this.baseUrl}/api/favorites`, {
      params,
    });
  }

  getFavoriteUniversities(userId: number): Observable<number[]> {
    const params = new HttpParams().set('userId', userId);

    return this.http
      .get<{
        universities: { id: number }[];
      }>(`${this.baseUrl}/api/favorites`, { params })
      .pipe(map((response) => response.universities.map((u) => u.id)));
  }
}
