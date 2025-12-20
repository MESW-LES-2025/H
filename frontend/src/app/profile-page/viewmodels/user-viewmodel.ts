import { UserCourse } from '../../shared/viewmodels/user-course';

export interface UserViewmodel {
  id: number;
  name: string;
  email: string;
  age: number;
  gender: string;
  location: string;
  profilePicture: string;
  jobTitle: string;
  academicHistory: UserCourse[];
  userRole: string;
  provider?: string;
  premiumStartDate?: string;
  premium?: boolean;
  // Academic profile
  academicGrade?: number;
  educationLevel?: string;
  studyArea?: string;
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
  universityName?: string;
  cost?: number;
  credits?: number;
  description?: string;
}

export interface FavoritesResponse {
  universities: FavoriteUniversityDTO[];
  courses: FavoriteCourseDTO[];
}
