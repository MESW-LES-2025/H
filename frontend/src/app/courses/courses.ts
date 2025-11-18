import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import { CourseViewmodel } from './viewmodels/course-viewmodel';
import { CoursesService } from './service/courses-service';
import {Subject, takeUntil} from 'rxjs';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-courses',
  imports: [
    DatePipe
  ],
  templateUrl: './courses.html',
  styleUrl: './courses.css',
})
export class Courses implements OnInit, OnDestroy {
  private courseService: CoursesService = inject(CoursesService);
  protected courses: CourseViewmodel[] = [];

  private destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.courseService.getAllCourses()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: CourseViewmodel[]) => this.courses = data,
        error: (error) => {
          console.error('Failed to load courses:', error);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
