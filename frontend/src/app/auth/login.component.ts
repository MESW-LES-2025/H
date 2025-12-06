import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { AuthService, LoginResponse } from './auth.service';
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
          localStorage.setItem('userId', res.user.id.toString());
          if (res.user.name) {
            localStorage.setItem('username', res.user.name);
          }
          if (res.user.userRole) {
            localStorage.setItem('userRole', res.user.userRole);
          }

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
}
