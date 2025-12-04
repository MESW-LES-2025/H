import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserViewmodel } from '../viewmodels/user-viewmodel';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProfilePageService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}


  public getUserProfile(id: number): Observable<UserViewmodel> {
    return this.http.get<UserViewmodel>(`${this.apiUrl}/api/profile/${id}`, { withCredentials: true });
  }

  public updateProfile(user: UserViewmodel): Observable<UserViewmodel> {
    const { id, ...userData } = user;
    return this.http.put<UserViewmodel>(`${this.apiUrl}/api/profile/${id}/update-profile`, { id, ...userData });
  }

}
