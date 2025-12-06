import { LocationDTO } from "../../explore-page/viewmodels/explore-viewmodel";

export interface UniversityLight {
  id: number;
  name: string;
  description: string;
  location: LocationDTO | null;
}
