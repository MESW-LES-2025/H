import {UserCourse} from '../../../shared/viewmodels/user-course';
import {FormControl} from '@angular/forms';

export interface EditProfileForm {
  id: FormControl<number | null>;
  name: FormControl<string | null>;
  age: FormControl<number | null>;
  gender: FormControl<string | null>;
  location: FormControl<string | null>;
  jobTitle: FormControl<string | null>;
}
