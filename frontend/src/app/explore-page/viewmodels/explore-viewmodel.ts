export interface LocationDTO {
  id: number;
  city: string;
  country: string;
  costOfLiving: number;
}

export interface UniversityDTO {
  id: number;
  name: string;
  description: string;
  location: LocationDTO | null;
}

export interface CollegeVM {
  id: string;
  title: string;
  blurb: string;
  photo: string;
  color: string;
  country: string;
  city: string;
  costOfLiving: number;
  isFavorite?: boolean;
}
