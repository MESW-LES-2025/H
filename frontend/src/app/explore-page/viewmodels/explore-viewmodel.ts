export interface CollegeDTO {
  id: string;
  name: string;
  blurb: string;
  photoUrl: string;
  accent?: string;
  country: string;
  field: string;
  degree: 'Bachelor' | 'Master' | 'PhD';
  languages?: string[];
}

export interface CollegeVM {
  id: string;
  title: string;
  blurb: string;
  photo: string;
  color: string;
  country: string;
  field: string;
  degree: 'Bachelor' | 'Master' | 'PhD';
  languages: string[];
}

export function toCollegeVM(dto: CollegeDTO): CollegeVM {
  return {
    id: dto.id,
    title: dto.name,
    blurb: dto.blurb,
    photo: dto.photoUrl,
    color: dto.accent ?? '#7DB19F',
    country: dto.country,
    field: dto.field,
    degree: dto.degree,
    languages: dto.languages ? [...dto.languages] : [],
  };
}
