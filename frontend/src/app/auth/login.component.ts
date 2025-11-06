import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

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

  constructor(private fb: FormBuilder, private router: Router) {
    this.form = this.fb.group({
      text: ['', Validators.required],
      password: ['', Validators.required],
      remember: [false],
    });
  }

  toggle() { this.show = !this.show; }

  onSubmit() {
    if (this.form.invalid) return;
    const { text: identifier, password, remember } = this.form.value;
    console.log('Login', { identifier, password, remember });
  }

  goTo(path: 'register' | 'login', ev: Event) {
    ev.preventDefault();
    const nav = () => this.router.navigate([`/${path}`]);
    const doc: any = document;
    doc.startViewTransition ? doc.startViewTransition(() => nav()) : nav();
  }
}
