import { Component, OnInit, OnDestroy, inject, HostListener } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit, OnDestroy {
  links = [
    { label: 'Explore', path: '/explore' },
    { label: 'Home', path: '/home' },
    { label: 'About Us', path: '/about' },
  ];

  protected userId: number | null = null;
  private userSubscription: Subscription | null = null;

  // dropdown state
  exploreOpen = false;

  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe((user) => {
      this.userId = user ? user.id : null;
    });
  }

  // fecha dropdown ao clicar fora
  @HostListener('document:click')
  onDocumentClick() {
    this.exploreOpen = false;
  }

  // abre/fecha com click no botão
  toggleExplore(ev: MouseEvent) {
    ev.stopPropagation();
    this.exploreOpen = !this.exploreOpen;
  }

  closeExplore() {
    this.exploreOpen = false;
  }

  logout() {
    this.authService.logout();
  }

  ngOnDestroy() {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  goToProfile(): void {
    if (this.userId) {
      this.router.navigate(['/profile', this.userId]);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
