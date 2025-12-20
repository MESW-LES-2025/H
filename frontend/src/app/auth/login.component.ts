import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AuthService, LoginResponse, PasswordResetTokenResponse } from './auth.service';
import { DataService } from '../shared/services/data-service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  form!: FormGroup;
  show = false;
  loading = false;
  errorMessage: string | null = null;

  // Forgot Password Modal
  showForgotPasswordModal = false;
  forgotPasswordForm!: FormGroup;
  forgotPasswordLoading = false;
  forgotPasswordFeedback?: string;
  forgotPasswordError?: string;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService,
    private dataService: DataService,
  ) {
    this.form = this.fb.group({
      text: ['', Validators.required],
      password: ['', Validators.required],
      remember: [false],
    });

    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  toggle() {
    this.show = !this.show;
  }

  onSubmit() {
    if (this.form.invalid) return;
    this.errorMessage = null;
    this.loading = true;
    const { text, password } = this.form.value;

    this.auth.login({ text, password }).subscribe({
      next: (res: LoginResponse) => {
        this.loading = false;
        console.log('Login OK', res);

        if (res.status === 'success' && res.user?.id != null) {
          this.dataService.setUserAtual(res.user);
          this.router.navigate(['/profile', res.user.id]);
        } else {
          this.errorMessage = res.message || 'Login failed';
        }
      },
      error: (err) => {
        this.loading = false;
        console.error('Login error', err);
        this.errorMessage = 'Login error';
      },
    });
  }

  goTo(path: 'register' | 'login', ev: Event) {
    ev.preventDefault();
    const nav = () => this.router.navigate([`/${path}`]);
    const doc: any = document;
    doc.startViewTransition ? doc.startViewTransition(() => nav()) : nav();
  }

  openForgotPassword() {
    this.showForgotPasswordModal = true;
    this.forgotPasswordFeedback = undefined;
    this.forgotPasswordError = undefined;
    this.forgotPasswordForm.reset();
  }

  closeForgotPassword() {
    this.showForgotPasswordModal = false;
  }

  submitForgotPassword() {
    if (this.forgotPasswordForm.invalid || this.forgotPasswordLoading) return;

    this.forgotPasswordLoading = true;
    this.forgotPasswordError = undefined;
    this.forgotPasswordFeedback = undefined;

    this.auth.forgotPassword(this.forgotPasswordForm.value.email.trim()).subscribe({
      next: (res: PasswordResetTokenResponse) => {
        this.forgotPasswordFeedback = res.message;
        this.forgotPasswordLoading = false;
      },
      error: () => {
        this.forgotPasswordError = 'Unable to process request right now. Please try again.';
        this.forgotPasswordLoading = false;
      },
    });
  }

  loginWithGoogle() {
    this.auth.loginWithGoogle();
  }
}
