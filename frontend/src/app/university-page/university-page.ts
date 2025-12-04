import { Component, inject, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { UniversityPageService } from './services/university-page-service';
import { UniversityViewmodel } from './viewmodels/university-viewmodel';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';

import { ExploreService } from '../explore-page/services/explore-service';
import { ReviewsComponent } from './reviews/reviews.component';

@Component({
  selector: 'app-university-page',
  standalone: true,
  imports: [CommonModule, NgbNavModule, ReviewsComponent],
  templateUrl: './university-page.html',
  styleUrls: ['./university-page.css'],
})
export class UniversityPage implements OnInit {
  private universityPageService = inject(UniversityPageService);
  private exploreService = inject(ExploreService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  protected university: UniversityViewmodel | null = null;
  protected isFavorite: boolean = false;
  active: number = 1;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.universityPageService.getUniversityProfile(id).subscribe({
      next: uni => {
        this.university = uni;
        this.loadFavoriteState();
      }
    });
  }

  // ---------------------------------------
  // 1. Carregar estado de favorito do backend
  // ---------------------------------------
  private loadFavoriteState(): void {
    const storedId = localStorage.getItem('userId');
    if (!storedId || !this.university) {
      this.isFavorite = false;
      return;
    }

    const userId = Number(storedId);

    this.exploreService.getFavoriteUniversities(userId).subscribe({
      next: ids => {
        this.isFavorite = ids.includes(this.university!.id);
      },
      error: err => console.error('Favorite load error:', err)
    });
  }

  onToggleFavorite(): void {
    if (!this.university) return;

    const uniId = this.university.id;

    if (!this.isFavorite) {
      this.exploreService.addFavoriteUniversity(uniId).subscribe({
        next: () => this.isFavorite = true,
        error: err => console.error('Error adding favorite:', err)
      });
    } else {
      this.exploreService.removeFavoriteUniversity(uniId).subscribe({
        next: () => this.isFavorite = false,
        error: err => console.error('Error removing favorite:', err)
      });
    }
  }

  goToCourse(courseId: number): void {
    this.router.navigate(['/course', courseId]);
  }
}
