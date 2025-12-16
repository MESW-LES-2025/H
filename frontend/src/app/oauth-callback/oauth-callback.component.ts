import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { DataService } from '../shared/services/data-service';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { UserViewmodel } from '../profile-page/viewmodels/user-viewmodel';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-oauth-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="display: flex; justify-content: center; align-items: center; height: 100vh;">
      <div style="text-align: center;">
        <h2>Completing login...</h2>
        <p>Please wait while we redirect you.</p>
      </div>
    </div>
  `,
})
export class OAuthCallbackComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private router: Router,
    private dataService: DataService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    // Restore session after OAuth redirect
    this.authService.restoreSession().subscribe({
      next: (user) => {
        if (user && user.id) {
          // Fetch full user profile and update localStorage
          this.http.get<UserViewmodel>(`${environment.apiUrl}/api/profile/${user.id}`, { withCredentials: true })
            .subscribe({
              next: (userProfile) => {
                this.dataService.setUserAtual(userProfile);
                localStorage.setItem('userId', userProfile.id.toString());
                if (userProfile.name) {
                  localStorage.setItem('username', userProfile.name);
                }
                if (userProfile.userRole) {
                  localStorage.setItem('role', userProfile.userRole);
                }
                // Redirect to user profile
                this.router.navigate(['/profile', userProfile.id]);
              },
              error: () => {
                // Fallback: just navigate to home if profile fetch fails
                this.router.navigate(['/']);
              }
            });
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        console.error('OAuth callback error:', error);
        // If session restoration fails, redirect to login
        this.router.navigate(['/login']);
      }
    });
  }
}
