import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ProfilePageService} from '../services/profile-page-service';
import { EditProfileRequest } from './viewmodels/edit-profile-request';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-profile.html',
  styleUrls: ['./edit-profile.css'],
})
export class EditProfile implements OnInit {
  userIdAtual: number | null = null;

  editProfileForm: FormGroup = undefined as any;

  constructor(
    private fb: FormBuilder,
    protected router: Router,
    private profilePageService: ProfilePageService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    const userIdParam = this.route.snapshot.paramMap.get('id');
    this.userIdAtual = userIdParam ? Number(userIdParam) : null;

    this.editProfileForm = this.fb.group({
      id: this.fb.control<number | null>(this.userIdAtual, Validators.required),
      name: this.fb.control<string | null>(null, Validators.required),
      age: this.fb.control<number | null>(null),
      gender: this.fb.control<string | null>(null, Validators.required),
      location: this.fb.control<string | null>(null),
      jobTitle: this.fb.control<string | null>(null)
    });
  }

  onSubmit(): void {
    if (this.editProfileForm.valid && this.userIdAtual) {
      const raw = this.editProfileForm.value as EditProfileRequest;

      this.profilePageService.updateProfile(raw).subscribe({
        next: () => {
          this.router.navigate(['/profile', this.userIdAtual!]);
        },
        error: (error) => {
          console.error('Failed to update profile:', error);
        }
      });
    } else {
      this.editProfileForm.markAllAsTouched();
    }
  }
}
