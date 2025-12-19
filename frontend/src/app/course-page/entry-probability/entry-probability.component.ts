import { Component, Input, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../auth/auth.service';

export interface ProbabilityResponse {
  courseId: number;
  percentage: number;
  confidenceLevel: string;
  label: string;
  factors: { [key: string]: string };
}

@Component({
  selector: 'app-entry-probability',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './entry-probability.component.html',
  styleUrls: ['./entry-probability.component.css']
})
export class EntryProbabilityComponent implements OnInit {
  @Input() courseId!: number;

  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private baseUrl = environment.apiUrl;

  probability = signal<ProbabilityResponse | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  isPremium = signal(false);

  ngOnInit(): void {
    this.isPremium.set(this.authService.isPremium());

    if (this.isPremium() && this.courseId) {
      this.loadProbability();
    } else {
      this.loading.set(false);
    }
  }

  private loadProbability(): void {
    this.http.get<ProbabilityResponse>(
      `${this.baseUrl}/api/courses/${this.courseId}/entry-probability`,
      { withCredentials: true }
    ).subscribe({
      next: (response) => {
        this.probability.set(response);
        this.loading.set(false);
      },
      error: (err) => {
        if (err.status === 403) {
          this.isPremium.set(false);
        } else {
          this.error.set('Unable to calculate probability. Please complete your academic profile.');
        }
        this.loading.set(false);
      }
    });
  }

  getStrokeDashArray(): string {
    const circumference = 2 * Math.PI * 50;
    const percentage = this.probability()?.percentage ?? 0;
    const offset = (percentage / 100) * circumference;
    return `${offset} ${circumference}`;
  }

  getFactorEntries(): { key: string; value: string }[] {
    const factors = this.probability()?.factors ?? {};
    return Object.entries(factors).map(([key, value]) => ({ key, value }));
  }
}
