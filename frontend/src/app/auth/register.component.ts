import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-register',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  show1 = false;
  show2 = false;
  form;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      email: [''],
      password: [''],
      remember: [false],
    });
  }

}
