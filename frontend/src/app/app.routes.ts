import { Routes } from '@angular/router';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    {
        path: 'login',
        loadComponent: () =>
        import('./auth/login.component').then(m => m.LoginComponent),
    },
    {
        path: 'register',
        loadComponent: () =>
            import('./auth/register.component').then(m => m.RegisterComponent),
    },
    {
        path: 'profile/:id',
        loadComponent: () => import('./profile-page/profile-page').then(m => m.ProfilePage)
    }
];
