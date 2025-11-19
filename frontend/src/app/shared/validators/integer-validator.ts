import { AbstractControl, ValidationErrors } from '@angular/forms';

export function integerValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (value == null) return null;
  return Number.isInteger(value) ? null : { integer: true };
}
