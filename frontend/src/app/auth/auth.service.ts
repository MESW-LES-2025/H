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

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.restoreSession();
  }

  private restoreSession(): void {
    this.http.get<User>(`${this.baseUrl}/api/auth/me`, { withCredentials: true }).subscribe({
      next: (user) => {
        if (user && user.id) {
          this.currentUserSubject.next(user);
        } else {
          this.currentUserSubject.next(null);
        }
      },
      error: () => {
        this.currentUserSubject.next(null);
      }
    });
  }

  login(body: LoginRequest): Observable<LoginResponse> {
    let headers = new HttpHeaders();
    if (this.csrfToken && this.csrfHeaderName) {
      headers = headers.set(this.csrfHeaderName, this.csrfToken);
    }
    return this.http.post<LoginResponse>(`${this.baseUrl}/api/auth/login`, body, { withCredentials: true })
      .pipe(tap(res => {
        if (res.status === 'success' && res.userId) {
          this.currentUserSubject.next({ id: res.userId });
        }
      }));
  }

  register(body: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.baseUrl}/api/auth/register`, body);
  }
}
