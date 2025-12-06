import { UniversityDTO } from '../../explore-page/viewmodels/explore-viewmodel';

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
  university: UniversityDTO;
  topics: string[];
  requirements: string[];

  isFavorite?: boolean;
}
