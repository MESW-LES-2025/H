import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UniversityViewmodel } from '../viewmodels/university-viewmodel';

@Injectable({
  providedIn: 'root',
})
export class UniversityPageService {
  private base = `${environment.apiUrl}/api/university`; 

  constructor(private http: HttpClient) {}

  public getUniversityProfile(id: number): Observable<UniversityViewmodel> {
    return this.http.get<UniversityViewmodel>(`${this.base}/${id}`, { withCredentials: true });
  }
}
