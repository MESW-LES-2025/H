import { FormControl } from '@angular/forms';

export interface CourseFiltersForm {
  name: FormControl<string | null>;
  courseTypes: FormControl<string[]>;
  areasOfStudy: FormControl<string[]>;
  onlyRemote: FormControl<boolean>;
  costMax: FormControl<number | null>;
  duration: FormControl<number | null>;
  languages: FormControl<string[]>;
  countries: FormControl<string[]>;
}
