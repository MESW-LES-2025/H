import { Injectable } from '@angular/core';
import { UniversityViewmodel } from '../viewmodels/university-viewmodel';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UniversityPageService {

  public getUniversityProfile(id: number): Observable<UniversityViewmodel> {
    return of();
  }
}
