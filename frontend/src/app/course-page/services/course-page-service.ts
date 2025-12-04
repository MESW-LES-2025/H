import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CourseViewmodel } from '../viewmodels/course-viewmodel';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

interface LocationDTO {
  id: number;
  city: string;
  country: string;
  costOfLiving: number;
}

interface UniversityDTOLight {
  id: number;
  name: string;
  description: string;
  location: LocationDTO;
}

interface AreaOfStudyDTO {
  id: number;
  name: string;
}

interface CourseDTO {
  id: number;
  name: string;
  description: string;
  courseType: string;
  isRemote: boolean;
  minAdmissionGrade: number;
  cost: number;
  duration: number;
  credits: number;
  language: string;
  startDate: string;
  applicationDeadline: string;
  website: string;
  contactEmail: string;
  university: UniversityDTOLight;
  areasOfStudy: AreaOfStudyDTO[];
}

@Injectable({
  providedIn: 'root',
})
export class CoursePageService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // -------------------------------------
  // GET COURSE PROFILE
  // -------------------------------------
  public getCourseProfile(id: number): Observable<CourseViewmodel> {
    return this.http
      .get<CourseDTO>(`${this.baseUrl}/api/courses/${id}`)
      .pipe(map(dto => this.mapToViewmodel(dto)));
  }

  // -------------------------------------
  // FAVORITES - GET USER'S FAVORITE COURSES
  // -------------------------------------
  public getFavoriteCourses(userId: number): Observable<number[]> {
    const params = new HttpParams().set('userId', userId);

    return this.http
      .get<{ courses: { id: number }[] }>(
        `${this.baseUrl}/api/favorites`,
        { params }
      )
      .pipe(map(res => res.courses.map(c => c.id)));
  }

  // -------------------------------------
  // ADD FAVORITE COURSE
  // -------------------------------------
  public addFavoriteCourse(courseId: number): Observable<void> {
    const storedId = localStorage.getItem('userId');
    if (!storedId) throw new Error("User not logged in");

    const params = new HttpParams().set('userId', storedId);

    return this.http.post<void>(
      `${this.baseUrl}/api/favorites/courses/${courseId}`,
      {},
      { params }
    );
  }

  // -------------------------------------
  // REMOVE FAVORITE COURSE
  // -------------------------------------
  public removeFavoriteCourse(courseId: number): Observable<void> {
    const storedId = localStorage.getItem('userId');
    if (!storedId) throw new Error("User not logged in");

    const params = new HttpParams().set('userId', storedId);

    return this.http.delete<void>(
      `${this.baseUrl}/api/favorites/courses/${courseId}`,
      { params }
    );
  }

  // -------------------------------------
  // MAPPER
  // -------------------------------------
  private mapToViewmodel(dto: CourseDTO): CourseViewmodel {
    return {
      id: dto.id,
      name: dto.name,
      area: dto.areasOfStudy.length > 0 ? dto.areasOfStudy[0].name : 'General',
      description: dto.description || 'No description available',
      duration: dto.duration ? `${dto.duration} months` : 'N/A',
      level: dto.courseType || 'General',
      language: dto.language || 'Not specified',
      credits: dto.credits || 0,
      bannerImage:
        'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop',

      university: {
        id: dto.university.id,
        name: dto.university.name,
        description: dto.university.description,
        location: {
          id: dto.university.location.id,
          city: dto.university.location.city,
          country: dto.university.location.country,
          costOfLiving: dto.university.location.costOfLiving,
        } as any,

      },

      topics: dto.areasOfStudy.map(a => a.name),

      requirements: dto.minAdmissionGrade
        ? [`Minimum admission grade: ${dto.minAdmissionGrade}`]
        : [],

      isFavorite: false
    };
  }

}
