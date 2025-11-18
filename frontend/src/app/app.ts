import {Component} from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

import { NavbarComponent } from './shared/navbar/navbar.component';
import {DataService} from './shared/services/data-service';

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

  constructor(private router: Router, private dataService: DataService) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        const url = event.urlAfterRedirects || event.url;

        const hideOn = ['/login', '/register'];
        this.showNavbar = !hideOn.includes(url);
      });
  }

  ngOnInit(): void {
    this.dataService.loadFilterLists();
  }
}
