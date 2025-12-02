import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Review } from '../../university-page/viewmodels/review';
import { ReviewService } from '../../university-page/services/review-service';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-course-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './course-reviews.component.html',
  styleUrls: ['./course-reviews.component.css']
})
export class CourseReviewsComponent implements OnInit {
  @Input() courseId!: number;
  reviews: Review[] = [];
  newReview: Review = {
    userId: 0,
    courseId: 0,
    rating: 5,
    title: '',
    description: ''
  };
  isLoggedIn = false;
  isEligible = false;

  constructor(
    private reviewService: ReviewService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadReviews();
    this.authService.currentUser$.subscribe(user => {
        this.isLoggedIn = !!user;
        if (user) {
            this.newReview.userId = user.id;
            this.checkEligibility(user.id);
        }
    });
  }

  checkEligibility(userId: number) {
    if (this.courseId) {
      this.reviewService.checkCourseEligibility(this.courseId, userId).subscribe(eligible => {
        this.isEligible = eligible;
      });
    }
  }

  loadReviews() {
    if (this.courseId) {
      this.reviewService.getCourseReviews(this.courseId).subscribe(
        data => {
          this.reviews = data;
        },
        error => {
          console.error('Error loading reviews:', error);
        }
      );
    }
  }

  submitReview() {
    this.newReview.courseId = this.courseId;
    this.reviewService.addCourseReview(this.newReview).subscribe(savedReview => {
      this.reviews.unshift(savedReview);
      this.newReview.title = '';
      this.newReview.description = '';
      this.newReview.rating = 5;
    });
  }
}