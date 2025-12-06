import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { UserViewmodel } from '../profile-page/viewmodels/user-viewmodel';

export interface LoginRequest {
  text: string;
  password: string;
}

export interface LoginResponse {
  message: string;
  status: string;
  user?: UserViewmodel;
}

export interface RegisterRequest {
  name: string;
  username: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  message: string;
  status: string;
}

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

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {
    this.restoreSession();
  }

  private restoreSession(): void {
    this.http
      .get<User>(`${this.baseUrl}/api/auth/me`, { withCredentials: true })
      .subscribe({
        next: (user) => {
          if (user && user.id) {
            this.currentUserSubject.next(user);
          } else {
            this.currentUserSubject.next(null);
          }
        },
        error: () => {
          this.currentUserSubject.next(null);
        },
      });
  }

  login(body: LoginRequest): Observable<LoginResponse> {
    let headers = new HttpHeaders();
    if (this.csrfToken && this.csrfHeaderName) {
      headers = headers.set(this.csrfHeaderName, this.csrfToken);
    }

    return this.http
      .post<LoginResponse>(`${this.baseUrl}/login`, body, {
        withCredentials: true,
        headers,
      })
      .pipe(
        tap((response) => {
          if (response.status === 'success') {
            if (response.user) {
              this.currentUserSubject.next({
                id: response.user.id,
              });
            }
          }
        }),
      );
  }

  register(body: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.baseUrl}/register`, body);
  }

  getUserById(userId: number): Observable<UserViewmodel> {
    return this.http.get<UserViewmodel>(`${this.baseUrl}/api/users/${userId}`);
  }

  updateUser(userId: number, userData: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/api/users/${userId}`, userData);
  }

  logout(): void {
    this.http
      .post(`${this.baseUrl}/api/auth/logout`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
          this.currentUserSubject.next(null);
          this.router.navigate(['/']);
        },
        error: () => {
          this.currentUserSubject.next(null);
          this.router.navigate(['/']);
        },
      });
  }
}
