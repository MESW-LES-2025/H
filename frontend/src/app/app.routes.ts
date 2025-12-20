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
    path: 'register/profile',
    loadComponent: () =>
      import('./auth/register-profile.component').then(
        (m) => m.RegisterProfileComponent,
      ),
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
      import('./scholarships/scholarships.component').then(
        (m) => m.ScholarshipsComponent,
      ),
  },
  {
    path: 'home',
    loadComponent: () =>
      import('./home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'about',
    loadComponent: () =>
      import('./about/about.component').then((m) => m.AboutComponent),
  },
  {
    path: 'faqs',
    loadComponent: () =>
      import('./faq/faq.component').then((m) => m.FaqComponent),
  },
  {
    path: 'reset-password',
    loadComponent: () =>
      import('./auth/reset-password.component').then(
        (m) => m.ResetPasswordComponent,
      ),
  },
  {
    path: 'oauth/callback',
    loadComponent: () =>
      import('./oauth-callback/oauth-callback.component').then(
        (m) => m.OAuthCallbackComponent,
      ),
  },
  {
    path: 'subscribe',
    loadComponent: () =>
      import('./subscription/subscription.component').then(
        (m) => m.SubscriptionComponent,
      ),
  },
  {
    path: 'admin',
    // TODO: canActivate: [AdminGuard],
    loadComponent: () =>
      import('./admin-dashboard/admin-dashboard.component').then(
        (m) => m.AdminDashboardComponent,
      ),
  },

  // wildcard (opcional)
  { path: '**', redirectTo: 'home' },
];
