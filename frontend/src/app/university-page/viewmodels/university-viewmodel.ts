import { Course } from '../../shared/viewmodels/course';

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
