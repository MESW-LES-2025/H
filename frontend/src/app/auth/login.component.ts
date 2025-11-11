import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from './auth.service';

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

  constructor(private fb: FormBuilder, private router: Router, private auth: AuthService) {
    this.form = this.fb.group({
      text: ['', Validators.required],
      password: ['', Validators.required],
      remember: [false],
    });
  }

  toggle() { this.show = !this.show; }

  onSubmit() {
    if (this.form.invalid) return;
    const { text, password } = this.form.value;
    this.auth.login({ text, password }).subscribe({
      next: (res) => {
        console.log('Login OK', res);
        if (res.status === 'success') this.router.navigate(['/profile', 1]);
      },
      error: (err) => console.error('Login erro', err)
    });
  }

  goTo(path: 'register' | 'login', ev: Event) {
    ev.preventDefault();
    const nav = () => this.router.navigate([`/${path}`]);
    const doc: any = document;
    doc.startViewTransition ? doc.startViewTransition(() => nav()) : nav();
  }
}
