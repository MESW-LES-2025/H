import { Component, inject, OnInit } from '@angular/core';
import { CourseViewmodel } from './viewmodels/course-viewmodel';
import { CoursesService } from './service/courses-service';

@Component({
  selector: 'app-courses',
  imports: [],
  templateUrl: './courses.html',
  styleUrl: './courses.css',
})
export class Courses implements OnInit {
  private courseService: CoursesService = inject(CoursesService);
  protected courses: CourseViewmodel[] = [];

  ngOnInit(): void {
    this.courseService.getAllCourses().subscribe((data: CourseViewmodel[]) => this.courses = data);
  }

}
