import {
  Component,
  Input,
  OnInit,
  OnDestroy,
  TemplateRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Review } from '../../university-page/viewmodels/review';
import { ReviewService } from '../../university-page/services/review-service';
import { AuthService } from '../../auth/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-course-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './course-reviews.component.html',
  styleUrls: ['./course-reviews.component.css'],
})
export class CourseReviewsComponent implements OnInit, OnDestroy {
  @Input() courseId!: number;
  reviews: Review[] = [];
  newReview: Review = {
    userId: 0,
    universityId: 0,
    courseId: 0,
    rating: 5,
    title: '',
    description: '',
  };
  editingReview: Review = { ...this.newReview };

  isLoggedIn = false;
  isEligible = false;
  currentUserId: number | null = null;
  private userSubscription: Subscription | undefined;

  constructor(
    private reviewService: ReviewService,
    private authService: AuthService,
    private modalService: NgbModal,
  ) {}

  ngOnInit(): void {
    this.loadReviews();
    this.userSubscription = this.authService.currentUser$.subscribe((user) => {
      this.isLoggedIn = !!user;
      if (user) {
        this.currentUserId = user.id;
        this.newReview.userId = user.id;
        this.checkEligibility(user.id);
      } else {
        this.currentUserId = null;
      }
    });
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  loadReviews() {
    if (this.courseId) {
      this.reviewService.getCourseReviews(this.courseId).subscribe((data) => {
        this.reviews = data;
      });
    }
  }

  checkEligibility(userId: number) {
    if (this.courseId) {
      this.reviewService
        .checkCourseEligibility(this.courseId)
        .subscribe((eligible) => {
          this.isEligible = eligible;
        });
    }
  }

  submitReview() {
    const reviewToSend = { ...this.newReview };
    delete reviewToSend.id;
    (reviewToSend as any).courseId = this.courseId;

    this.reviewService.addCourseReview(reviewToSend).subscribe({
      next: (savedReview) => {
        this.reviews.unshift(savedReview);
        this.newReview.title = '';
        this.newReview.description = '';
        this.newReview.rating = 5;
      },
      error: (error) => {
        console.error('Error posting review:', error);
        const errorMessage =
          error.error?.message || 'An unexpected error occurred.';
        alert('Failed to post review: ' + errorMessage);
      },
    });
  }

  openDeleteModal(content: TemplateRef<any>, reviewId: number | undefined) {
    if (!reviewId) return;

    this.modalService.open(content, { centered: true }).result.then(
      (result) => {
        if (result === 'delete') {
          this.deleteReview(reviewId);
        }
      },
      () => {},
    );
  }

  deleteReview(reviewId: number) {
    this.reviewService.deleteCourseReview(reviewId).subscribe({
      next: () => {
        this.reviews = this.reviews.filter((r) => r.id !== reviewId);
      },
      error: (error) => {
        console.error('Error deleting review:', error);
        const errorMessage =
          error.error?.message || 'An unexpected error occurred.';
        alert('Failed to delete review: ' + errorMessage);
      },
    });
  }

  openEditModal(content: TemplateRef<any>, review: Review) {
    this.editingReview = { ...review };
    this.modalService.open(content, { centered: true }).result.then(
      (result) => {
        if (result === 'save') {
          this.saveEditedReview();
        }
      },
      () => {},
    );
  }

  saveEditedReview() {
    if (!this.editingReview.id) return;

    this.reviewService
      .updateCourseReview(this.editingReview.id, this.editingReview)
      .subscribe({
        next: (updatedReview) => {
          const index = this.reviews.findIndex(
            (r) => r.id === updatedReview.id,
          );
          if (index !== -1) {
            this.reviews[index] = updatedReview;
          }
        },
        error: (error) => {
          console.error('Error updating review:', error);
          const errorMessage =
            error.error?.message || 'An unexpected error occurred.';
          alert('Failed to update review: ' + errorMessage);
        },
      });
  }
}
