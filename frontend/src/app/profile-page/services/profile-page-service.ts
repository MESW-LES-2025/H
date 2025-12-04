import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserViewmodel, FavoritesResponse } from '../viewmodels/user-viewmodel';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProfilePageService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getUserProfile(id: number): Observable<UserViewmodel> {
    return this.http.get<UserViewmodel>(
      `${this.apiUrl}/api/profile/${id}`,
      { withCredentials: true }
    );
  }

  public getOwnProfile(): Observable<UserViewmodel> {
    return this.http.get<UserViewmodel>(
      `${this.apiUrl}/api/profile`,
      { withCredentials: true }
    );
  }

  public getOwnFavorites(): Observable<FavoritesResponse> {
    const storedId = localStorage.getItem('userId');
    if (!storedId) {
      throw new Error('User not logged in');
    }

    const params = { userId: storedId };

    return this.http.get<FavoritesResponse>(
      `${this.apiUrl}/api/favorites`,
      { params }
    );
  }



  public removeFavoriteUniversity(id: number): Observable<void> {
    const storedId = localStorage.getItem('userId');
    if (!storedId) {
      throw new Error('User not logged in');
    }

    const params = { userId: storedId };

    return this.http.delete<void>(
      `${this.apiUrl}/api/favorites/universities/${id}`,
      { params }
    );
  }

}
