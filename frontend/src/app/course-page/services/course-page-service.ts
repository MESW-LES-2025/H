import { Injectable } from '@angular/core';
import { CourseViewmodel } from '../viewmodels/course-viewmodel';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CoursePageService {
  private course: CourseViewmodel = {
    id: 201,
    name: 'Bachelor of Science in Computer Science',
    area: 'Computer Science',
    description: 'The Computer Science program provides a comprehensive foundation in computing theory, software development, and problem-solving. Students learn programming, algorithms, data structures, and computer systems while gaining hands-on experience through projects and internships.',
    duration: '4 years',
    level: 'Undergraduate',
    language: 'English',
    credits: 240,
    bannerImage: 'images/oxford-university-logo.jpg',
    university: {
      id: 1,
      name: 'University of Oxford',
      location: 'Oxford, United Kingdom',
      logo: 'images/oxford-university-logo.jpg',
    },
    topics: [
      'Programming Fundamentals',
      'Data Structures and Algorithms',
      'Database Systems',
      'Software Engineering',
      'Computer Networks',
      'Operating Systems',
      'Artificial Intelligence',
      'Machine Learning',
      'Web Development',
      'Cybersecurity'
    ],
    requirements: [
      'High school diploma or equivalent',
      'Strong background in Mathematics',
      'Basic programming knowledge (recommended)',
      'English language proficiency',
      'Minimum GPA of 3.0'
    ]
  };

  public getCourseProfile(id: number): Observable<CourseViewmodel> {
    return of(this.course);
  }
}
