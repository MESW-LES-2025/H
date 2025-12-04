import { Component, inject, OnInit } from '@angular/core';
import { ProfilePageService } from './services/profile-page-service';
import { UserViewmodel, FavoritesResponse, FavoriteUniversityDTO } from './viewmodels/user-viewmodel';
import { ActivatedRoute } from '@angular/router';
import { DatePipe, NgIf, NgFor } from '@angular/common';

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

  // ---------------------------
  // UI STATE
  // ---------------------------
  protected activeTab: 'universities' | 'courses' | 'countries' | 'other' = 'universities';

  // ---------------------------
  // FAVORITE UNIVERSITIES
  // ---------------------------
  protected universities: {
    id: number;
    image: string;
    name: string;
    city: string;
    country: string;
  }[] = [];

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    // perfil
    this.profilePageService
      .getUserProfile(id)
      .subscribe(data => this.user = data);

    // favoritos
    this.profilePageService
      .getOwnFavorites()
      .subscribe((favs: FavoritesResponse) => {
        this.universities = favs.universities.map((uni: FavoriteUniversityDTO) => ({
          id: uni.id,
          image: '/images/oxford-university-banner.jpg',
          name: uni.name,
          city: uni.location?.city ?? 'Unknown',
          country: uni.location?.country ?? 'Unknown',
        }));
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
        this.universities = this.universities.filter(u => u.id !== id);
      },
      error: err => {
        console.error('Error removing favorite:', err);
      }
    });
  }
}
