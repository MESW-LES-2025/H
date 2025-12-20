import { Component, OnInit, inject, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RecommendationService, Suggestion } from './recommendation.service';
import { RecommendationCardComponent } from './recommendation-card.component';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-recommendations',
  standalone: true,
  imports: [CommonModule, RouterLink, RecommendationCardComponent],
  template: `
    <section class="recommendations-section">
      @if (!isPremium()) {
        <!-- Upgrade Prompt for Non-Premium Users -->
        <div class="upgrade-prompt">
          <div class="upgrade-icon">🔒</div>
          <h4 class="upgrade-title">Personalized Recommendations</h4>
          <p class="upgrade-text">Upgrade to Premium to get course and university suggestions tailored to your interests!</p>
          <a routerLink="/subscribe" class="upgrade-btn">
            ⭐ Upgrade to Premium
          </a>
        </div>
      } @else {
        <!-- Premium User Content -->
        <div class="section-header">
          <h4 class="section-title">
            <span class="sparkle">✨</span> Recommended for You
          </h4>
          <span class="premium-badge">Premium</span>
        </div>

        @if (loading()) {
          <!-- Loading State -->
          <div class="row g-3">
            @for (i of [1, 2, 3, 4]; track i) {
              <div class="col-md-6 col-lg-3">
                <div class="loading-card">
                  <div class="placeholder-glow">
                    <span class="placeholder col-4 mb-2"></span>
                    <span class="placeholder col-8 mb-2"></span>
                    <span class="placeholder col-12 mb-2"></span>
                    <span class="placeholder col-6"></span>
                  </div>
                </div>
              </div>
            }
          </div>
        } @else if (error()) {
          <!-- Error State -->
          <div class="error-message">
            {{ error() }}
          </div>
        } @else if (recommendations().length === 0) {
          <!-- Empty State -->
          <div class="empty-state">
            <div class="empty-icon">🔍</div>
            <p class="empty-text">
              Start adding courses and universities to your favorites to get personalized recommendations!
            </p>
          </div>
        } @else {
          <!-- Recommendations Grid -->
          <div class="row g-3">
            @for (suggestion of recommendations(); track suggestion.id) {
              <div class="col-md-6 col-lg-3">
                <app-recommendation-card [suggestion]="suggestion" />
              </div>
            }
          </div>
        }
      }
    </section>
  `,
  styles: [`
    .recommendations-section {
      margin-bottom: 2rem;
      padding: 1.5rem;
      background: #e3ebdf;
      border-radius: 18px;
      border: 1px solid rgba(0, 0, 0, 0.04);
    }

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.25rem;
    }

    .section-title {
      color: #3f2419;
      font-weight: 700;
      font-size: 1.25rem;
      margin: 0;
    }

    .sparkle {
      margin-right: 0.5rem;
    }

    .premium-badge {
      background: #f3e6bc;
      color: #5a3226;
      padding: 6px 14px;
      border-radius: 999px;
      font-size: 0.8rem;
      font-weight: 700;
    }

    .upgrade-prompt {
      background: linear-gradient(135deg, rgba(125, 177, 159, 0.15) 0%, rgba(206, 220, 167, 0.15) 100%);
      border: 1px solid rgba(125, 177, 159, 0.3);
      border-radius: 16px;
      padding: 2rem;
      text-align: center;
    }

    .upgrade-icon {
      font-size: 2.5rem;
      margin-bottom: 1rem;
    }

    .upgrade-title {
      color: #3f2419;
      font-weight: 700;
      margin-bottom: 0.75rem;
    }

    .upgrade-text {
      color: #5a3226;
      margin-bottom: 1.25rem;
    }

    .upgrade-btn {
      display: inline-block;
      background: #7db19f;
      color: white;
      padding: 10px 24px;
      border-radius: 999px;
      font-weight: 700;
      text-decoration: none;
      transition: background 0.2s;
    }

    .upgrade-btn:hover {
      background: #6a9e8c;
      color: white;
    }

    .loading-card {
      background: #fdf4e7;
      border-radius: 16px;
      padding: 1rem;
    }

    .error-message {
      background: rgba(220, 53, 69, 0.1);
      border: 1px solid rgba(220, 53, 69, 0.3);
      color: #dc3545;
      padding: 1rem;
      border-radius: 12px;
    }

    .empty-state {
      text-align: center;
      padding: 2rem;
    }

    .empty-icon {
      font-size: 2rem;
      margin-bottom: 0.75rem;
    }

    .empty-text {
      color: #5a3226;
      margin: 0;
    }
  `]
})
export class RecommendationsComponent implements OnInit {
  private recommendationService = inject(RecommendationService);
  private authService = inject(AuthService);

  /** Optional filter: 'course' shows only courses, 'university' shows only universities, undefined shows all */
  @Input() typeFilter?: 'course' | 'university';

  recommendations = signal<Suggestion[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  isPremium = signal(false);

  ngOnInit(): void {
    this.isPremium.set(this.authService.isPremium());

    if (this.isPremium()) {
      this.loadRecommendations();
    } else {
      this.loading.set(false);
    }
  }

  private loadRecommendations(): void {
    this.recommendationService.getRecommendations().subscribe({
      next: (suggestions) => {
        // Apply type filter if specified
        const filtered = this.typeFilter
          ? suggestions.filter(s => s.type === this.typeFilter)
          : suggestions;
        this.recommendations.set(filtered);
        this.loading.set(false);
      },
      error: (err) => {
        if (err.status === 403) {
          this.isPremium.set(false);
        } else {
          this.error.set('Failed to load recommendations. Please try again later.');
        }
        this.loading.set(false);
      }
    });
  }
}

