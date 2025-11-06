import { Component, inject, OnInit } from '@angular/core';
import { CoursePageService } from './services/course-page-service';
import { CourseViewmodel } from './viewmodels/course-viewmodel';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-course-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './course-page.html',
  styleUrl: './course-page.css',
})
export class CoursePage implements OnInit {
  private coursePageService: CoursePageService = inject(CoursePageService);
  private route: ActivatedRoute = inject(ActivatedRoute);
  protected course: CourseViewmodel | null = null;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.coursePageService.getCourseProfile(id)
      .subscribe(data => this.course = data);
  }
}
