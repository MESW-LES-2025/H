import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UniversityViewmodel } from '../viewmodels/university-viewmodel';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

interface LocationDTO {
  id: number;
  city: string;
  country: string;
  costOfLiving: number;
}

interface CourseLightDTO {
  id: number;
  name: string;
  courseType: string;
}

interface ScholarshipDTO {
  id: number;
  name: string;
  description: string;
  amount: number;
  courseType: string;
}

interface UniversityDTO {
  id: number;
  name: string;
  description: string;
  contactInfo: string;
  website: string;
  address: string;
  logo: string;
  location: LocationDTO;
  courses: CourseLightDTO[];
  scholarships: ScholarshipDTO[];
}

@Injectable({
  providedIn: 'root',
})
export class UniversityPageService {
  private readonly baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getUniversityProfile(id: number): Observable<UniversityViewmodel> {
    return this.http.get<UniversityDTO>(`${this.baseUrl}/api/university/${id}`)
      .pipe(
        map(dto => this.mapToViewmodel(dto))
      );
  }

  private mapToViewmodel(dto: UniversityDTO): UniversityViewmodel {
    const location = dto.location
      ? `${dto.location.city}, ${dto.location.country}`
      : 'Location not available';

    return {
      id: dto.id,
      name: dto.name,
      location: location,
      logo: dto.logo || 'https://via.placeholder.com/100',
      bannerImage: 'https://images.unsplash.com/photo-1541339907198-e08756dedf3f?q=80&w=1170&auto=format&fit=crop',
      description: dto.description || 'No description available',
      studentCount: 0,
      foundedYear: 0,
      courses: dto.courses.map(course => ({
        id: course.id,
        name: course.name,
        area: course.courseType || 'General',
        university: {
          id: dto.id,
          name: dto.name,
          location: {
            id: dto.location?.id || 0,
            city: dto.location?.city || '',
            country: dto.location?.country || ''
          },
          logo: dto.logo || null
        }
      })),
      scholarships: dto.scholarships || [],
      address: dto.address || 'N/A',
      contactInfo: dto.contactInfo || 'N/A',
      website: dto.website || 'N/A'
    };
  }
}
