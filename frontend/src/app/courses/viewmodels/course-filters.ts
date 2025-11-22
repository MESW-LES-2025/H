export interface CourseFilters {
  name: string | null;
  courseTypes: string[];
  areasOfStudy: string[];
  onlyRemote: boolean;
  costMax: number | null;
  duration: number | null;
  languages: string[];
  countries: string[];
}
