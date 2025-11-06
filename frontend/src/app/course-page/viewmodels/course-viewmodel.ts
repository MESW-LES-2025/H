import { University } from '../../shared/viewmodels/university';

export interface CourseViewmodel {
  id: number;
  name: string;
  area: string;
  description: string;
  duration: string;
  level: string;
  language: string;
  credits: number;
  bannerImage: string;
  university: University;
  topics: string[];
  requirements: string[];
}
