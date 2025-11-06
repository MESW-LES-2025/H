import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import {
  FormBuilder, FormGroup, Validators,
  AbstractControl, ValidationErrors, ReactiveFormsModule
} from '@angular/forms';

function matchPasswords(group: AbstractControl): ValidationErrors | null {
  const pass = group.get('password')?.value;
  const conf = group.get('confirm')?.value;
  return pass && conf && pass !== conf ? { mismatch: true } : null;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  form!: FormGroup;
  show1 = false;
  show2 = false;

  constructor(private fb: FormBuilder, private router: Router) {
    this.form = this.fb.group(
        {
          email: ['', [Validators.required, Validators.email]],
          password: ['', [Validators.required, Validators.minLength(6)]],
          confirm: ['', [Validators.required]],
        },
        { validators: matchPasswords }
    );
  }

  onSubmit() {
    if (this.form.invalid) return;
    console.log('Register', this.form.value);
  }

  goTo(path: 'register' | 'login', ev: Event) {
    ev.preventDefault();
    const nav = () => this.router.navigate([`/${path}`]);
    const doc: any = document;
    if (doc.startViewTransition) doc.startViewTransition(() => nav());
    else nav();
  }
}
