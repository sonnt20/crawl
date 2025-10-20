import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/news',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: 'news',
    loadChildren: () => import('./features/news/news.routes').then(m => m.NEWS_ROUTES),
    canActivate: [authGuard]
  },
  {
    path: 'alerts',
    loadChildren: () => import('./features/alerts/alerts.routes').then(m => m.ALERTS_ROUTES),
    canActivate: [authGuard]
  },
  {
    path: 'profile',
    loadChildren: () => import('./features/profile/profile.routes').then(m => m.PROFILE_ROUTES),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/news'
  }
];

