import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService, ResetPasswordPayload } from './auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent {
  form!: FormGroup;
  loading = false;
  success?: string;
  error?: string;
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {
    this.form = this.fb.group({
      token: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
    });

    const tokenFromUrl = this.route.snapshot.queryParamMap.get('token');
    if (tokenFromUrl) {
      this.form.patchValue({ token: tokenFromUrl });
    }
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  submit() {
    if (this.form.invalid || this.loading) return;

    this.loading = true;
    this.success = undefined;
    this.error = undefined;

    this.authService.resetPassword(this.form.value as ResetPasswordPayload).subscribe({
      next: () => {
        this.success = 'Password updated successfully. You can now sign in.';
        this.loading = false;
      },
      error: () => {
        this.error = 'Invalid or expired token. Please request a new one.';
        this.loading = false;
      },
    });
  }
}