import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Review } from '../viewmodels/review';
import { ReviewService } from '../services/review-service';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.css']
})
export class ReviewsComponent implements OnInit {
  @Input() universityId!: number;
  reviews: Review[] = [];
  newReview: Review = {
    userId: 0,
    universityId: 0,
    rating: 5,
    title: '',
    description: ''
  };
  isLoggedIn = false;

  constructor(
    private reviewService: ReviewService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadReviews();
    this.authService.currentUser$.subscribe(user => {
        if (user) {
            this.isLoggedIn = true;
            this.newReview.userId = user.id;
        }
    });
  }

  loadReviews() {
    if (this.universityId) {
      this.reviewService.getReviews(this.universityId).subscribe(data => {
        this.reviews = data;
      });
    }
  }

  submitReview() {
    this.newReview.universityId = this.universityId;
    this.reviewService.addReview(this.newReview).subscribe(savedReview => {
      this.reviews.unshift(savedReview);
      this.newReview.title = '';
      this.newReview.description = '';
      this.newReview.rating = 5;
    });
  }
}