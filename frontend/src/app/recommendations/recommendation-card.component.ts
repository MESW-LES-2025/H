import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Suggestion } from './recommendation.service';

@Component({
  selector: 'app-recommendation-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <a [routerLink]="getLink()" class="card h-100 text-decoration-none border-0 recommendation-card">
      <div class="card-body d-flex flex-column">
        <div class="d-flex justify-content-between align-items-start mb-2">
          <span class="type-badge" [class.course]="suggestion.type === 'course'" [class.university]="suggestion.type === 'university'">
            {{ suggestion.type === 'course' ? '📚 Course' : '🏛️ University' }}
          </span>
        </div>
        
        <h5 class="card-title">{{ suggestion.title }}</h5>
        
        <p class="card-text flex-grow-1" *ngIf="suggestion.description">
          {{ truncateDescription(suggestion.description) }}
        </p>
        
        <div class="meta-info mt-auto">
          <div class="meta-item" *ngIf="suggestion.location">
            <span class="meta-icon">📍</span> {{ suggestion.location }}
          </div>
          <div class="meta-item" *ngIf="suggestion.universityName">
            <span class="meta-icon">🏛️</span> {{ suggestion.universityName }}
          </div>
          <div class="meta-item" *ngIf="suggestion.courseType">
            <span class="meta-icon">🎓</span> {{ suggestion.courseType }}
          </div>
        </div>
      </div>
    </a>
  `,
  styles: [`
    .recommendation-card {
      background: #fdf4e7;
      border-radius: 16px;
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
      border: 1px solid rgba(0, 0, 0, 0.05);
    }
    
    .recommendation-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
    }

    .card-title {
      color: #3f2419;
      font-weight: 700;
      font-size: 1rem;
      margin-bottom: 0.5rem;
    }

    .card-text {
      color: #5a3226;
      font-size: 0.85rem;
      line-height: 1.4;
    }

    .type-badge {
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 0.75rem;
      font-weight: 600;
    }

    .type-badge.course {
      background: #cedca7;
      color: #3f2419;
    }

    .type-badge.university {
      background: #7db19f;
      color: white;
    }

    .match-badge {
      background: #f3e6bc;
      color: #5a3226;
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 0.75rem;
      font-weight: 700;
    }

    .meta-info {
      border-top: 1px solid rgba(0, 0, 0, 0.05);
      padding-top: 0.75rem;
    }

    .meta-item {
      color: #5a3226;
      font-size: 0.8rem;
      margin-bottom: 0.25rem;
    }

    .meta-icon {
      margin-right: 4px;
    }
  `]
})
export class RecommendationCardComponent {
  @Input() suggestion!: Suggestion;

  getLink(): string[] {
    if (this.suggestion.type === 'course') {
      return ['/course', this.suggestion.id.toString()];
    } else {
      return ['/university', this.suggestion.id.toString()];
    }
  }

  truncateDescription(text: string): string {
    if (text.length <= 100) return text;
    return text.substring(0, 100) + '...';
  }
}

