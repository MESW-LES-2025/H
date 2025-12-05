import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import {
  UserViewmodel,
  FavoritesResponse
} from '../viewmodels/user-viewmodel';
import { environment } from '../../../environments/environment';
import { EditProfileRequest } from '../edit-profile/viewmodels/edit-profile-request';

@Injectable({
  providedIn: 'root',
})
export class ProfilePageService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ----------------------------
  // USER PROFILE
  // ----------------------------

  public getUserProfile(id: number): Observable<UserViewmodel> {
    return this.http.get<UserViewmodel>(`${this.apiUrl}/api/profile/${id}`, {
      withCredentials: true,
    });
  }

  public getOwnProfile(): Observable<UserViewmodel> {
    return this.http.get<UserViewmodel>(`${this.apiUrl}/api/profile`, {
      withCredentials: true,
    });
  }

  // ----------------------------
  // LOAD ALL FAVORITES FOR CURRENT USER
  // ----------------------------

  /**
   * Se não houver userId em localStorage, devolvemos favoritos vazios.
   * Isto evita erros em testes e em sessões não autenticadas.
   */
  public getOwnFavorites(): Observable<FavoritesResponse> {
    const storedId = localStorage.getItem('userId');

    if (!storedId) {
      return of({
        universities: [],
        courses: []
      } as FavoritesResponse);
    }

    const params = new HttpParams().set('userId', storedId);

    return this.http.get<FavoritesResponse>(`${this.apiUrl}/api/favorites`, {
      params,
      withCredentials: true,
    });
  }

  // ----------------------------
  // UNIVERSITIES FAVORITES
  // ----------------------------

  public addFavoriteUniversity(id: number): Observable<void> {
    const storedId = localStorage.getItem('userId');

    if (!storedId) {
      return of(undefined as void);
    }

    const params = new HttpParams().set('userId', storedId);

    return this.http.post<void>(
      `${this.apiUrl}/api/favorites/universities/${id}`,
      {},
      { params, withCredentials: true }
    );
  }

  public removeFavoriteUniversity(id: number): Observable<void> {
    const storedId = localStorage.getItem('userId');

    if (!storedId) {
      return of(undefined as void);
    }

    const params = new HttpParams().set('userId', storedId);

    return this.http.delete<void>(
      `${this.apiUrl}/api/favorites/universities/${id}`,
      { params, withCredentials: true }
    );
  }

  // ----------------------------
  // COURSES FAVORITES
  // ----------------------------

  public addFavoriteCourse(id: number): Observable<void> {
    const storedId = localStorage.getItem('userId');

    if (!storedId) {
      return of(undefined as void);
    }

    const params = new HttpParams().set('userId', storedId);

    return this.http.post<void>(
      `${this.apiUrl}/api/favorites/courses/${id}`,
      {},
      { params, withCredentials: true }
    );
  }
  public getUserProfile(id: number): Observable<UserViewmodel> {
    return this.http.get<UserViewmodel>(`${this.apiUrl}/api/profile/${id}`, { withCredentials: true });
  }

  public removeFavoriteCourse(id: number): Observable<void> {
    const storedId = localStorage.getItem('userId');
  public updateProfile(user: EditProfileRequest): Observable<UserViewmodel> {
    const { id, ...userData } = user;
    return this.http.put<UserViewmodel>(`${this.apiUrl}/api/profile/${id}/update-profile`, { id, ...userData });
  }

    if (!storedId) {
      return of(undefined as void);
    }

    const params = new HttpParams().set('userId', storedId);

    return this.http.delete<void>(
      `${this.apiUrl}/api/favorites/courses/${id}`,
      { params, withCredentials: true }
    );
  }

  // ----------------------------
  // DELETE ACCOUNT
  // ----------------------------

  public deleteAccount(userId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/api/profile/delete/${userId}`,
      { withCredentials: true }
    );
  }
}
