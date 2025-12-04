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

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [NgIf, NgFor],
  templateUrl: './profile-page.html',
  styleUrl: './profile-page.css',
})
export class ProfilePage implements OnInit {

  private profilePageService = inject(ProfilePageService);
  private route = inject(ActivatedRoute);

  protected user: UserViewmodel | null = null;

  // Tab actual
  protected activeTab: 'universities' | 'courses' | 'countries' | 'other' = 'universities';

  // Favoritos (preenchidos a partir do backend)
  protected universities: {
    id: number;
    image: string;
    name: string;
    city: string;
    country: string;
  }[] = [];

  protected courses: {
    id: number;
    name: string;
    type: string;
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

    // Favoritos do user autenticado (universities + courses)
    this.profilePageService
      .getOwnFavorites()
      .subscribe({
        next: (favs: FavoritesResponse) => {
          this.universities = (favs.universities || []).map(
            (u: FavoriteUniversityDTO) => ({
              id: u.id,
              image: '/images/oxford-university-banner.jpg', // placeholder
              name: u.name,
              city: u.location?.city ?? 'Unknown',
              country: u.location?.country ?? 'Unknown',
            })
          );

          this.courses = (favs.courses || []).map(
            (c: FavoriteCourseDTO) => ({
              id: c.id,
              name: c.name,
              type: c.courseType,
            })
          );
        },
        error: err => {
          console.error('Error loading favorites', err);
        },
      });
  }

  protected setTab(tab: 'universities' | 'courses' | 'countries' | 'other'): void {
    this.activeTab = tab;
  }

  protected trackById(index: number, item: any): number {
    return item.id;
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
