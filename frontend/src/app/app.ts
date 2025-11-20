import { Component } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { Title } from '@angular/platform-browser';

import { NavbarComponent } from './shared/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  showNavbar = true;

  constructor(private router: Router, private titleService: Title) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        const url = event.urlAfterRedirects || event.url;

        const hideOn = ['/login', '/register'];
        this.showNavbar = !hideOn.includes(url);

        // Map routes to page titles
        const titles: { [key: string]: string } = {
          '/login': 'Login',
          '/register': 'Register',
        };

        const pageTitle = titles[url] || 'Lernia';
        this.titleService.setTitle(pageTitle);
      });
  }

}
