import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { UniversityPageService } from './services/university-page-service';
import { UniversityViewmodel } from './viewmodels/university-viewmodel';
import { ReviewsComponent } from './reviews/reviews.component';
import { ExploreService } from '../explore-page/services/explore-service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-university-page',
  standalone: true,
  imports: [CommonModule, NgbNavModule, DecimalPipe, ReviewsComponent],
  templateUrl: './university-page.html',
  styleUrls: ['./university-page.css'],
})
export class UniversityPage implements OnInit {
  private universityPageService = inject(UniversityPageService);
  private exploreService = inject(ExploreService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  protected university: UniversityViewmodel | null = null;
  protected isFavorite: boolean = false;
  active: number = 1;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.universityPageService.getUniversityProfile(id).subscribe({
      next: (uni) => {
        this.university = uni;
        this.loadFavoriteState();
      },
    });
  }

  // ---------------------------------------
  // 1. Load favorite state from backend
  // ---------------------------------------
  private loadFavoriteState(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId || !this.university) {
      this.isFavorite = false;
      return;
    }

    this.exploreService.getFavoriteUniversities(userId).subscribe({
      next: (ids) => {
        this.isFavorite = ids.includes(this.university!.id);
      },
      error: (err) => console.error('Favorite load error:', err),
    });
  }

  onToggleFavorite(): void {
    if (!this.university) return;

    const uniId = this.university.id;

    if (!this.isFavorite) {
      this.exploreService.addFavoriteUniversity(uniId).subscribe({
        next: () => (this.isFavorite = true),
        error: (err) => console.error('Error adding favorite:', err),
      });
    } else {
      this.exploreService.removeFavoriteUniversity(uniId).subscribe({
        next: () => (this.isFavorite = false),
        error: (err) => console.error('Error removing favorite:', err),
      });
    }
  }

  goToCourse(courseId: number): void {
    this.router.navigate(['/course', courseId]);
  }
}
