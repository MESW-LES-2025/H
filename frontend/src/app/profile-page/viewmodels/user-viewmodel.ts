import { UserCourse } from '../../shared/viewmodels/user-course';

export interface UserViewmodel {
  id: number;
  name: string;
  email: string;
  age: number;
  gender: string;
  location: string;
  profileImage: string;
  jobTitle: string;
  academicHistory: UserCourse[];
  userRole: string;
}

export interface FavoriteUniversityDTO {
  id: number;
  name: string;
  description: string;
  location: {
    id: number;
    city: string;
    country: string;
    costOfLiving: number;
  } | null;
}

export interface FavoriteCourseDTO {
  id: number;
  name: string;
  courseType: string;
}

export interface FavoritesResponse {
  universities: FavoriteUniversityDTO[];
  courses: FavoriteCourseDTO[];
}
