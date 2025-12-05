import { Component, inject, OnInit } from '@angular/core';
import { CoursePageService } from './services/course-page-service';
import { CourseViewmodel } from './viewmodels/course-viewmodel';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import {NgbNavModule} from '@ng-bootstrap/ng-bootstrap';
import { CourseReviewsComponent } from './reviews/course-reviews.component';

@Component({
  selector: 'app-course-page',
  standalone: true,
  imports: [CommonModule, NgbNavModule, CourseReviewsComponent],
  templateUrl: './course-page.html',
  styleUrls: ['./course-page.css'],
})
export class CoursePage implements OnInit {
  private svc = inject(CoursePageService);
  private route = inject(ActivatedRoute);

  course: CourseViewmodel | null = null;
  active = 1;
  isFavorite = false;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.svc.getCourseProfile(id).subscribe(course => {
      this.course = course;

      // Load favorite courses for user
      const stored = localStorage.getItem('userId');
      if (!stored) return;

      this.svc.getFavoriteCourses(Number(stored)).subscribe(ids => {
        this.isFavorite = ids.includes(id);
      });
    });
  }

  toggleFavorite(): void {
    if (!this.course) return;
    const id = this.course.id;

    if (this.isFavorite) {
      this.svc.removeFavoriteCourse(id).subscribe(() => {
        this.isFavorite = false;
      });
    } else {
      this.svc.addFavoriteCourse(id).subscribe(() => {
        this.isFavorite = true;
      });
    }
  }
}
