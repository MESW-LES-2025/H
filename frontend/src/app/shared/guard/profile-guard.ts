import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { DataService } from '../services/data-service';

@Injectable({ providedIn: 'root' })
export class ProfileGuard implements CanActivate {
  constructor(
    private dataService: DataService,
    private router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const loggedInUserId = this.dataService.getUserAtualId();
    const routeUserId = +route.params['id'];

    if (loggedInUserId === routeUserId) {
      return true;
    }
    this.router.navigate(['/forbidden']); // or redirect elsewhere
    return false;
  }
}
