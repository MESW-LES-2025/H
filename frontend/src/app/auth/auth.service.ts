import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface LoginRequest { text: string; password: string; }
export interface LoginResponse { message: string; status: string; userId?: number;}
export interface RegisterRequest { username: string; email: string; password: string; }
export interface RegisterResponse { message: string; status: string; }
export interface CsrfResponse {
  parameterName: string;
  headerName: string;
  token: string;
}

export interface User {
    id: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = environment.apiUrl;
  csrfToken: string | null = null;
  csrfHeaderName: string | null = null;

  // Add state management for the current user
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(body: LoginRequest): Observable<LoginResponse> {
    let headers = new HttpHeaders();
    if (this.csrfToken && this.csrfHeaderName) {
      headers = headers.set(this.csrfHeaderName, this.csrfToken);
    }
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, body, { withCredentials: true })
      .pipe(tap(res => {
        // Update the subject when login is successful
        if (res.status === 'success' && res.userId) {
          this.currentUserSubject.next({ id: res.userId });
        }
      }));
  }

  register(body: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.baseUrl}/register`, body);
  }
}
