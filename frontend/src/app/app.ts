import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { Title } from '@angular/platform-browser';

import { NavbarComponent } from './shared/navbar/navbar.component';
import { DataService } from './shared/services/data-service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit, OnDestroy {
  showNavbar = true;
  private destroy$: Subject<void> = new Subject<void>();

  constructor(
    private router: Router,
    private titleService: Title,
    private dataService: DataService,
  ) {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntil(this.destroy$),
      )
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

  ngOnInit(): void {
    this.dataService.loadFilterLists();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
