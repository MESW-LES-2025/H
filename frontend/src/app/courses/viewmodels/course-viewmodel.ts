import { University } from '../../shared/viewmodels/university';
import { AreasOfStudy } from '../../shared/viewmodels/area-of-study';

export interface CourseViewmodel {
  id: number;
  name: string;
  description: string;
  courseType: string;
  isRemote: boolean;
  minAdmissionGrade: number;
  cost: number;
  duration: string;
  credits: number;
  language: string;
  startDate: string;
  applicationDeadline: string;
  website: string;
  contactEmail: string;
  university: University;
  areasOfStudy: AreasOfStudy[];
}
