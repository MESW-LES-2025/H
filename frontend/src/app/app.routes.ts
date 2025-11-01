import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: 'profile/:id',
        loadComponent: () => import('./profile-page/profile-page').then(m => m.ProfilePage)
    }
];
