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
import { Review } from '../viewmodels/review';
import { ReviewService } from '../services/review-service';
import { AuthService } from '../../auth/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.css'],
})
export class ReviewsComponent implements OnInit, OnDestroy {
  @Input() universityId!: number;
  reviews: Review[] = [];
  newReview: Review = {
    userId: 0,
    universityId: 0,
    rating: 5,
    title: '',
    description: '',
  };
  isLoggedIn = false;
  isEligible = false;
  currentUserId: number | null = null;
  private userSubscription: Subscription | undefined;

  editingReview: Review = { ...this.newReview };

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

  checkEligibility(userId: number) {
    if (this.universityId) {
      this.reviewService
        .checkEligibility(this.universityId)
        .subscribe((eligible) => {
          this.isEligible = eligible;
        });
    }
  }

  loadReviews() {
    if (this.universityId) {
      this.reviewService.getReviews(this.universityId).subscribe((data) => {
        this.reviews = data;
      });
    }
  }

  submitReview() {
    const reviewToSend = { ...this.newReview };
    delete reviewToSend.id;

    reviewToSend.universityId = this.universityId;

    this.reviewService.addReview(reviewToSend).subscribe({
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
    this.reviewService.deleteReview(reviewId).subscribe({
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
      .updateReview(this.editingReview.id, this.editingReview)
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
