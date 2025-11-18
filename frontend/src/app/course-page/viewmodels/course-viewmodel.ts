import { UniversityLight } from '../../universities/viewmodels/university-light';

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
  university: UniversityLight;
  topics: string[];
  requirements: string[];
}
