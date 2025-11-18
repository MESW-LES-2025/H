import { Injectable } from '@angular/core';
import {BehaviorSubject, firstValueFrom, map, Observable} from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {AreasOfStudy} from '../viewmodels/area-of-study';

@Injectable({ providedIn: 'root' })
export class DataService {
  private readonly baseUrl = environment.apiUrl;

  private areasOfStudySubject = new BehaviorSubject<string[]>([]);
  private languagesSubject = new BehaviorSubject<string[]>([]);
  private countriesSubject = new BehaviorSubject<string[]>([]);

  areasOfStudy$: Observable<string[]> = this.areasOfStudySubject.asObservable();
  languages$: Observable<string[]> = this.languagesSubject.asObservable();
  countries$: Observable<string[]> = this.countriesSubject.asObservable();

  constructor(private http: HttpClient) {}

  loadFilterLists(): Promise<void> {
    return Promise.all([
      firstValueFrom(this.http.get<AreasOfStudy[]>(`${this.baseUrl}/api/area-of-study`).pipe(map(areas => areas.map(area => area.name)))),
      firstValueFrom(this.http.get<string[]>(`${this.baseUrl}/api/courses/languages`)),
      firstValueFrom(this.http.get<string[]>(`${this.baseUrl}/api/university/countries`))
    ]).then(([areas, languages, countries]) => {
      this.areasOfStudySubject.next(areas);
      this.languagesSubject.next(languages);
      this.countriesSubject.next(countries);
    }).catch(error => {
      console.error('Failed to load filter lists', error);
      this.areasOfStudySubject.next([]);
      this.languagesSubject.next([]);
      this.countriesSubject.next([]);
    });
  }
}
