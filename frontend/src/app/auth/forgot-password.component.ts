import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService, PasswordResetTokenResponse } from './auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  form!: FormGroup;
  loading = false;
  feedback?: string;
  error?: string;

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  submit() {
    if (this.form.invalid || this.loading) return;

    this.loading = true;
    this.error = undefined;
    this.feedback = undefined;

    this.authService.forgotPassword(this.form.value.email!.trim()).subscribe({
      next: (res: PasswordResetTokenResponse) => {
        this.feedback = res.message;
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to process request right now.';
        this.loading = false;
      },
    });
  }
}