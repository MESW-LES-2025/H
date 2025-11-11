import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest { text: string; password: string; }
export interface LoginResponse { message: string; status: string; }
export interface RegisterRequest { username: string; email: string; password: string; }
export interface RegisterResponse { message: string; status: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = '/api';

  constructor(private http: HttpClient) {}

  login(body: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, body);
  }

  register(body: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.baseUrl}/register`, body);
  }
}