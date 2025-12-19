import { Component, inject, OnInit } from '@angular/core';
import { CoursePageService } from './services/course-page-service';
import { CourseViewmodel } from './viewmodels/course-viewmodel';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { CourseReviewsComponent } from './reviews/course-reviews.component';
import { AuthService } from '../auth/auth.service';
import { EntryProbabilityComponent } from './entry-probability/entry-probability.component';

@Component({
  selector: 'app-course-page',
  standalone: true,
  imports: [CommonModule, NgbNavModule, CourseReviewsComponent, EntryProbabilityComponent],
  templateUrl: './course-page.html',
  styleUrls: ['./course-page.css'],
})
export class CoursePage implements OnInit {
  private svc = inject(CoursePageService);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);

  course: CourseViewmodel | null = null;
  active = 1;
  isFavorite = false;
  message: string | null = null;
  messageType: 'info' | 'error' | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.svc.getCourseProfile(id).subscribe((course) => {
      this.course = course;

      const userId = this.authService.getCurrentUserId();
      if (!userId) return;

      this.svc.getFavoriteCourses(userId).subscribe((ids) => {
        this.isFavorite = ids.includes(id);
      });
    });
  }

  toggleFavorite(): void {
    if (!this.course) return;
    const userId = this.authService.getCurrentUserId();
    const id = this.course.id;

    if (!userId) {
      this.message = 'Please log in to save courses to your favorites.';
      this.messageType = 'info';
      setTimeout(() => {
        this.message = null;
        this.messageType = null;
      }, 3000);
      return;
    }

    if (this.isFavorite) {
      this.svc.removeFavoriteCourse(id).subscribe({
        next: () => (this.isFavorite = false),
        error: () => {
          this.message = 'Could not remove from favorites. Please try again.';
          this.messageType = 'error';
          setTimeout(() => {
            this.message = null;
            this.messageType = null;
          }, 3000);
        },
      });
    } else {
      this.svc.addFavoriteCourse(id).subscribe({
        next: () => (this.isFavorite = true),
        error: () => {
          this.message = 'Could not add to favorites. Please try again.';
          this.messageType = 'error';
          setTimeout(() => {
            this.message = null;
            this.messageType = null;
          }, 3000);
        },
      });
    }
  }
}
