import { Component, inject, OnInit } from '@angular/core';
import { ProfilePageService } from './services/profile-page-service';
import { UserViewmodel } from './viewmodels/user-viewmodel';
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
  // MOCK DATA (cards)
  // ---------------------------
  protected universities = [
    {
      id: 1,
      image: '/images/oxford-university-banner.jpg',
      name: 'Oxford University',
      city: 'Oxford',
      country: 'United Kingdom',
    },
    {
      id: 2,
      image: '/images/oxford-university-banner.jpg',
      name: 'Royal Holloway University',
      city: 'Egham',
      country: 'United Kingdom',
    },
    {
      id: 3,
      image: '/images/oxford-university-banner.jpg',
      name: 'Yale University',
      city: 'New Haven',
      country: 'United States',
    }
  ];

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.profilePageService
      .getUserProfile(id)
      .subscribe(data => this.user = data);
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

}
