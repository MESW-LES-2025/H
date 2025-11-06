import {Course} from './course';

export interface UserCourse {
  id: string;
  startDate: string;
  endDate: string;
  course: Course;
}
