import { Injectable } from '@angular/core';
import { UniversityViewmodel } from '../viewmodels/university-viewmodel';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UniversityPageService {
  private university: UniversityViewmodel = {
    id: 1,
    name: 'University of Oxford',
    location: 'Oxford, United Kingdom',
    logo: 'images/oxford-university-logo.jpg',
    bannerImage: 'images/oxford-university-banner.jpg',
    description: 'Founded in 1096, the University of Oxford is one of the oldest and most prestigious universities in the world. Located in the historic city of Oxford, it comprises 39 autonomous colleges and various academic departments.',
    studentCount: 24500,
    foundedYear: 1096,
    courses: [
      {
        id: 201,
        name: 'Bachelor of Arts in Philosophy, Politics and Economics',
        area: 'Social Sciences',
        university: {
          id: 1,
          name: 'University of Oxford',
          location: 'Oxford, United Kingdom',
          logo: 'images/oxford-university-logo.jpg',
        }
      },
      {
        id: 202,
        name: 'Bachelor of Science in Computer Science',
        area: 'Computer Science',
        university: {
          id: 1,
          name: 'University of Oxford',
          location: 'Oxford, United Kingdom',
          logo: 'images/oxford-university-logo.jpg',
        }
      },
      {
        id: 203,
        name: 'Master of Science in Advanced Computer Science',
        area: 'Computer Science',
        university: {
          id: 1,
          name: 'University of Oxford',
          location: 'Oxford, United Kingdom',
          logo: 'images/oxford-university-logo.jpg',
        }
      },
      {
        id: 204,
        name: 'Bachelor of Arts in Mathematics',
        area: 'Mathematics',
        university: {
          id: 1,
          name: 'University of Oxford',
          location: 'Oxford, United Kingdom',
          logo: 'images/oxford-university-logo.jpg',
        }
      },
      {
        id: 205,
        name: 'Bachelor of Arts in History',
        area: 'History',
        university: {
          id: 1,
          name: 'University of Oxford',
          location: 'Oxford, United Kingdom',
          logo: 'images/oxford-university-logo.jpg',
        }
      }
    ]
  };

  public getUniversityProfile(id: number): Observable<UniversityViewmodel> {
    return of(this.university);
  }
}
