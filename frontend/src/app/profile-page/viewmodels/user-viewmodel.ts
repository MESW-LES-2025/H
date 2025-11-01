import {UserCourse} from '../../shared/viewmodels/user-course';

export interface UserViewmodel {
  id: number;
  name: string;
  age: number;
  gender: string;
  location: string;
  profileImage: string;
  jobTitle: string;
  academicHistory: UserCourse[];
}
