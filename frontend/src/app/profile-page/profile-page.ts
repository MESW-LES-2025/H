import { Component, inject, OnInit } from '@angular/core';
import { FavoritesResponse } from './viewmodels/user-viewmodel';
import { ProfilePageService } from './services/profile-page-service';
import { UserViewmodel } from './viewmodels/user-viewmodel';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { EditProfileRequest } from './viewmodels/edit-profile-request';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [RouterOutlet, CommonModule, ReactiveFormsModule],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css',
})
export class ProfilePage implements OnInit {
  private profilePageService = inject(ProfilePageService);
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);

  protected user: UserViewmodel | null = null;
  protected showEditModal = false;
  protected editProfileForm: FormGroup = undefined as any;

  protected activeTab: 'universities' | 'courses' | 'countries' | 'other' =
    'universities';

  protected universities: {
    id: number;
    image: string;
    name: string;
    city: string;
    country: string;
    isFavorite: boolean;
  }[] = [];

  protected courses: {
    id: number;
    name: string;
    type: string;
    isFavorite: boolean;
  }[] = [];

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = idParam ? Number(idParam) : NaN;

    if (!isNaN(id)) {
      this.profilePageService
        .getUserProfile(id)
        .subscribe((user) => (this.user = user));
    } else {
      this.profilePageService
        .getOwnProfile()
        .subscribe((user) => (this.user = user));
    }

    this.loadFavorites();
    this.initForm();
  }

  get isOwner(): boolean {
    const stored = localStorage.getItem('userId');
    if (!this.user || !stored) return false;
    return Number(stored) === this.user.id;
  }

  private initForm(): void {
    this.editProfileForm = this.fb.group({
      id: this.fb.control<number | null>(null, Validators.required),
      name: this.fb.control<string | null>(null, Validators.required),
      age: this.fb.control<number | null>(null),
      gender: this.fb.control<string | null>(null, Validators.required),
      location: this.fb.control<string | null>(null),
      jobTitle: this.fb.control<string | null>(null),
    });
  }

  protected openEditModal(): void {
    if (!this.user || !this.isOwner) return;

    this.editProfileForm.patchValue({
      id: this.user.id,
      name: this.user.name,
      age: this.user.age,
      gender: this.user.gender,
      location: this.user.location,
      jobTitle: this.user.jobTitle,
    });

    this.showEditModal = true;
  }

  protected closeEditModal(): void {
    this.showEditModal = false;
    this.editProfileForm.reset();
  }

  protected onSubmitEdit(): void {
    if (this.editProfileForm.valid) {
      const raw = this.editProfileForm.value as EditProfileRequest;

      this.profilePageService.updateProfile(raw).subscribe({
        next: (updatedUser) => {
          this.user = updatedUser;
          this.closeEditModal();
        },
        error: (error) => {
          console.error('Failed to update profile:', error);
          alert('Failed to update profile. Please try again.');
        },
      });
    } else {
      this.editProfileForm.markAllAsTouched();
    }
  }

  private loadFavorites(): void {
    this.profilePageService.getOwnFavorites().subscribe({
      next: (favs: FavoritesResponse) => {
        this.universities = (favs.universities ?? []).map((u) => ({
          id: u.id,
          image: '/images/oxford-university-banner.jpg',
          name: u.name,
          city: u.location?.city ?? 'Unknown',
          country: u.location?.country ?? 'Unknown',
          isFavorite: true,
        }));

        this.courses = (favs.courses ?? []).map((c) => ({
          id: c.id,
          name: c.name,
          type: c.courseType,
          isFavorite: true,
        }));
      },
      error: (err) => console.error('Error loading favorites', err),
    });
  }

  protected setTab(
    tab: 'universities' | 'courses' | 'countries' | 'other',
  ): void {
    this.activeTab = tab;
  }

  protected trackById(index: number, item: any): number {
    return item.id;
  }
  
  protected confirmDelete(): void {
    // Only owner may delete account
    if (!this.user || !this.isOwner) {
      return;
    }

    const sure = window.confirm(
      'Are you sure you want to delete your account? This action cannot be undone.',
    );
    if (!sure) return;

    this.profilePageService.deleteAccount(this.user.id).subscribe({
      next: () => {
        alert('Account deleted successfully.');
        localStorage.removeItem('userId');
        window.location.href = '/home';
      },
      error: (err) => {
        console.error('Error deleting account', err);
        alert('Failed to delete account.');
      },
    });
  }

  protected toggleUniversityFavorite(uni: any): void {
    if (!uni.isFavorite) {
      this.profilePageService.addFavoriteUniversity(uni.id).subscribe({
        next: () => (uni.isFavorite = true),
        error: (err) => console.error(err),
      });
    } else {
      this.profilePageService.removeFavoriteUniversity(uni.id).subscribe({
        next: () => {
          this.universities = this.universities.filter((u) => u.id !== uni.id);
        },
        error: (err) => console.error(err),
      });
    }
  }

  protected toggleCourseFavorite(course: any): void {
    if (!course.isFavorite) {
      this.profilePageService.addFavoriteCourse(course.id).subscribe({
        next: () => (course.isFavorite = true),
        error: (err) => console.error(err),
      });
    } else {
      this.profilePageService.removeFavoriteCourse(course.id).subscribe({
        next: () => {
          this.courses = this.courses.filter((c) => c.id !== course.id);
        },
        error: (err) => console.error(err),
      });
    }
  }
  protected removeFavoriteUniversity(id: number): void {
    this.profilePageService.removeFavoriteUniversity(id).subscribe({
      next: () => {
        this.universities = this.universities.filter((uni) => uni.id !== id);
      },
      error: (err) => console.error('Error removing favorite university', err),
    });
  }
}
