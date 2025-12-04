import { Course } from '../../shared/viewmodels/course';
import { Scholarship } from './scholarship';

export interface Location {
  id: number;
  city: string;
  country: string;
  costOfLiving: number;
}

export interface UniversityViewmodel {
  id: number;
  name: string;
  location: string;
  logo: string;
  bannerImage: string;
  description: string;
  studentCount: number;
  foundedYear: number;
  courses: Course[];
  scholarships: Scholarship[];
  address: string;
  contactInfo: string;
  website: string;
}
