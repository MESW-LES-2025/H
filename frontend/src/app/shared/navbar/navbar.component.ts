import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {

  links = [
    { label: 'Home', path: '/home' },
    { label: 'Explore', path: '/explore' },
    { label: 'About Us', path: '/about' },
  ];

  protected userId: number | null = null;

  private router = inject(Router);

  ngOnInit(): void {
    const storedId = localStorage.getItem('userId');
    this.userId = storedId ? Number(storedId) : null;
  }


  logout() {
    this.authService.logout();
  }

  ngOnDestroy() {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();

  goToProfile(): void {
    const storedId = localStorage.getItem('userId');

    if (storedId) {
      const id = Number(storedId);
      this.router.navigate(['/profile', id]);
    } else {
      this.router.navigate(['/login']);
    }
  }
}
