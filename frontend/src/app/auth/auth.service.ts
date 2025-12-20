import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
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
  userId?: number;
}

export interface CsrfResponse {
  parameterName: string;
  headerName: string;
  token: string;
}

export interface User {
  id: number;
  name?: string;
  email?: string;
  userRole?: string;
}

export interface PasswordResetTokenResponse {
  message: string;
  token?: string | null;
  expiresAt?: string | null;
}

export interface ResetPasswordPayload {
  token: string;
  newPassword: string;
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
    this.restoreSession().subscribe();
  }

  // Public method to restore session (used by OAuth callback)
  restoreSession(): Observable<User> {
    return this.http
      .get<User>(`${this.baseUrl}/api/auth/me`, { withCredentials: true })
      .pipe(
        tap((user) => {
          if (user && user.id) {
            this.currentUserSubject.next(user);
          } else {
            this.currentUserSubject.next(null);
          }
        }),
        catchError(() => {
          this.currentUserSubject.next(null);
          return of({} as User);
        })
      );
  }

  loginWithGoogle(): void {
    window.location.href = `${this.baseUrl}/oauth2/authorization/google`;
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
                name: response.user.name,
                email: response.user.email,
                userRole: response.user.userRole,
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
    return this.http.get<UserViewmodel>(`${this.baseUrl}/api/profile/${userId}`, { withCredentials: true });
  }

  updateUser(userId: number, userData: any): Observable<any> {
    // Uses public endpoint for post-registration profile completion
    return this.http.put(`${this.baseUrl}/api/users/${userId}`, userData, { withCredentials: true });
  }

  logout(): void {
    this.http
      .post(`${this.baseUrl}/logout`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
          this.currentUserSubject.next(null);
          // Clear localStorage on logout
          localStorage.removeItem('userId');
          localStorage.removeItem('username');
          localStorage.removeItem('role');
          this.router.navigate(['/']);
        },
        error: () => {
          this.currentUserSubject.next(null);
          // Clear localStorage even on error
          localStorage.removeItem('userId');
          localStorage.removeItem('username');
          localStorage.removeItem('role');
          this.router.navigate(['/']);
        },
      });
  }

  forgotPassword(email: string) {
    return this.http.post<PasswordResetTokenResponse>(
      `${this.baseUrl}/api/auth/password/forgot`,
      { email }
    );
  }

  resetPassword(payload: ResetPasswordPayload) {
    return this.http.post<void>(
      `${this.baseUrl}/api/auth/password/reset`,
      payload
    );
  }

  getCurrentUserId(): number | null {
    const user = this.currentUserSubject.value;
    return user ? user.id : null;
  }

  getCurrentUserRole(): string | null {
    const user = this.currentUserSubject.value;
    return user?.userRole || null;
  }

  isAdmin(): boolean {
    return this.getCurrentUserRole() === 'ADMIN';
  }

  isPremium(): boolean {
    return this.getCurrentUserRole() === 'PREMIUM';
  }
}
