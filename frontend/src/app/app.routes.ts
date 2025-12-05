import { Routes } from '@angular/router';

export const routes: Routes = [
  // redirect inicial
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  {
    path: 'login',
    loadComponent: () =>
      import('./auth/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./auth/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'profile/:id',
    loadComponent: () =>
      import('./profile-page/profile-page').then((m) => m.ProfilePage),
  },
  {
    path: 'university/:id',
    loadComponent: () =>
      import('./university-page/university-page').then((m) => m.UniversityPage),
  },
  {
    path: 'course/:id',
    loadComponent: () =>
      import('./course-page/course-page').then((m) => m.CoursePage),
  },
  {
    path: 'explore',
    loadComponent: () =>
      import('./explore-page/explore.component').then(
        (m) => m.ExploreComponent,
      ),
  },
  {
    path: 'courses',
    loadComponent: () => import('./courses/courses').then((m) => m.Courses),
  },
  {
    path: 'scholarships',
    loadComponent: () =>
        import('./scholarships/scholarships.component').then(m => m.ScholarshipsComponent),
  },
  {
    path: 'home',
    loadComponent: () =>
      import('./home/home.component').then((m) => m.HomeComponent),
  },

  // 🔥 NOVO: rota About Us
  {
    path: 'about',
    loadComponent: () =>
      import('./about/about.component').then((m) => m.AboutComponent),
  },

  // wildcard (opcional)
  { path: '**', redirectTo: 'home' },
];
