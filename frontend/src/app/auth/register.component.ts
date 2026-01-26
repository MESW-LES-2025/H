import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest, RegisterResponse } from './auth.service';

import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
  ReactiveFormsModule,
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
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService,
  ) {
    this.form = this.fb.group(
      {
        username: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirm: ['', [Validators.required]],
      },
      { validators: matchPasswords },
    );
  }

  onSubmit() {
    console.log(
      'Submit clicked. Form valid?',
      this.form.valid,
      this.form.value,
    );
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const { username, email, password } = this.form.value;

    const body: RegisterRequest = {
      name: username,
      username,
      email,
      password,
    };

    this.auth.register(body).subscribe({
      next: (res: RegisterResponse) => {
        if (res.status === 'success') {
          if (res.userId) {
            localStorage.setItem('pendingProfileUserId', res.userId.toString());
          }
          localStorage.setItem('pendingLoginEmail', email);
          localStorage.setItem('pendingLoginPassword', password);
          this.router.navigate(['/register/profile']);
        } else {
          this.errorMessage = res.message || 'Registration failed';
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Registration error';
      },
    });
  }

  goTo(path: 'register' | 'login', ev: Event) {
    ev.preventDefault();
    const nav = () => this.router.navigate([`/${path}`]);
    const doc: any = document;
    doc.startViewTransition ? doc.startViewTransition(() => nav()) : nav();
  }

  registerWithGoogle(): void {
    this.auth.loginWithGoogle();
  }
}
