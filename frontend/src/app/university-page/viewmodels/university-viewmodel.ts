import { Course } from '../../shared/viewmodels/course';

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
}
