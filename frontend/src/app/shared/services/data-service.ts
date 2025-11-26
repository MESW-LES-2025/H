import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, forkJoin, map, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AreasOfStudy } from '../viewmodels/area-of-study';

@Injectable({ providedIn: 'root' })
export class DataService {
  private readonly baseUrl = environment.apiUrl;

  private areasOfStudySubject = new BehaviorSubject<string[]>([]);
  private languagesSubject = new BehaviorSubject<string[]>([]);
  private countriesSubject = new BehaviorSubject<string[]>([]);

  private userAtualId: number | null = null;

  areasOfStudy$: Observable<string[]> = this.areasOfStudySubject.asObservable();
  languages$: Observable<string[]> = this.languagesSubject.asObservable();
  countries$: Observable<string[]> = this.countriesSubject.asObservable();

  constructor(private http: HttpClient) {}

  loadFilterLists(): void {
    forkJoin({
      areas: this.http.get<AreasOfStudy[]>(`${this.baseUrl}/api/area-of-study`).pipe(
        map(areas => areas.map(area => area.name))
      ),
      languages: this.http.get<string[]>(`${this.baseUrl}/api/courses/languages`),
      countries: this.http.get<string[]>(`${this.baseUrl}/api/university/countries`)
    }).pipe(
      catchError(error => {
        console.error('Failed to load filter lists', error);
        return of({ areas: [], languages: [], countries: [] });
      })
    ).subscribe(({ areas, languages, countries }) => {
      this.areasOfStudySubject.next(areas);
      this.languagesSubject.next(languages);
      this.countriesSubject.next(countries);
    });
  }

  public getUserAtualId(): number | null {
    return this.userAtualId;
  }

  public setUserAtualId(userId: number): void {
    this.userAtualId = userId;
  }

}
