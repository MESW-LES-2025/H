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
}

export function toCollegeVM(dto: UniversityDTO): CollegeVM {
  return {
    id: dto.id.toString(),
    title: dto.name,
    blurb: dto.description || 'No description available',
    photo: 'https://images.unsplash.com/photo-1605470207062-b72b5cbe2a87?q=80&w=1170&auto=format&fit=crop', //TODO: replace with real photo from DB
    color: '#7DB19F',
    country: dto.location?.country || 'Unknown',
    city: dto.location?.city || 'Unknown',
    costOfLiving: dto.location?.costOfLiving || 0
  };
}

export interface UniversityFilters {
  countries?: string[];
  costOfLivingMax?: number;
  hasScholarship?: boolean;
}
