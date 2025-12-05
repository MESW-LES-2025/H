import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgIf, NgFor } from '@angular/common';

import { ProfilePageService } from './services/profile-page-service';
import {
  UserViewmodel,
  FavoritesResponse,
  FavoriteUniversityDTO,
  FavoriteCourseDTO
} from './viewmodels/user-viewmodel';
import { ProfilePageService } from './services/profile-page-service';
import { UserViewmodel } from './viewmodels/user-viewmodel';
import { ActivatedRoute, RouterLink, RouterOutlet } from '@angular/router';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [NgIf, NgFor],
  imports: [DatePipe, RouterLink, RouterOutlet],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css',
})
export class ProfilePage implements OnInit {

  private profilePageService = inject(ProfilePageService);
  private route = inject(ActivatedRoute);

  protected user: UserViewmodel | null = null;

  protected activeTab: 'universities' | 'courses' | 'countries' | 'other' = 'universities';

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
        .subscribe(user => (this.user = user));
    } else {
      this.profilePageService
        .getOwnProfile()
        .subscribe(user => (this.user = user));
    }

    this.loadFavorites();
  }

  private loadFavorites(): void {
    this.profilePageService.getOwnFavorites().subscribe({
      next: (favs: FavoritesResponse) => {
        this.universities = (favs.universities ?? []).map(u => ({
          id: u.id,
          image: '/images/oxford-university-banner.jpg',
          name: u.name,
          city: u.location?.city ?? 'Unknown',
          country: u.location?.country ?? 'Unknown',
          isFavorite: true,
        }));

        this.courses = (favs.courses ?? []).map(c => ({
          id: c.id,
          name: c.name,
          type: c.courseType,
          isFavorite: true,
        }));
      },
      error: err => console.error('Error loading favorites', err),
    });
  }

  protected setTab(tab: 'universities' | 'courses' | 'countries' | 'other'): void {
    this.activeTab = tab;
  }

  protected trackById(index: number, item: any): number {
    return item.id;
  }
  protected confirmDelete(): void {
    if (!this.user) { return; }

    const sure = window.confirm('Are you sure you want to delete your account? This action cannot be undone.');
    if (!sure) return;

    this.profilePageService.deleteAccount(this.user.id).subscribe({
      next: () => {
        alert('Account deleted successfully.');
        localStorage.removeItem('userId');
        window.location.href = '/home';
      },
      error: err => {
        console.error('Error deleting account', err);
        alert('Failed to delete account.');
      },
    });
  }


  // -------------------------------------------
  // TOGGLE UNIVERSITY FAVORITE FROM PROFILE
  // -------------------------------------------
  protected toggleUniversityFavorite(uni: any): void {
    if (!uni.isFavorite) {
      this.profilePageService.addFavoriteUniversity(uni.id).subscribe({
        next: () => (uni.isFavorite = true),
        error: err => console.error(err),
      });
    } else {
      this.profilePageService.removeFavoriteUniversity(uni.id).subscribe({
        next: () => {
          this.universities = this.universities.filter(u => u.id !== uni.id);
        },
        error: err => console.error(err),
      });
    }
  }

  // -------------------------------------------
  // TOGGLE COURSE FAVORITE FROM PROFILE
  // -------------------------------------------
  protected toggleCourseFavorite(course: any): void {
    if (!course.isFavorite) {
      this.profilePageService.addFavoriteCourse(course.id).subscribe({
        next: () => (course.isFavorite = true),
        error: err => console.error(err),
      });
    } else {
      this.profilePageService.removeFavoriteCourse(course.id).subscribe({
        next: () => {
          this.courses = this.courses.filter(c => c.id !== course.id);
        },
        error: err => console.error(err),
      });
    }
  }
  protected removeFavoriteUniversity(id: number): void {
    this.profilePageService.removeFavoriteUniversity(id).subscribe({
      next: () => {
        this.universities = this.universities.filter(uni => uni.id !== id);
      },
      error: err => console.error('Error removing favorite university', err),
    });
  }
}
