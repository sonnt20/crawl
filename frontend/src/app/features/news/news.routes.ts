import { Routes } from '@angular/router';

export const NEWS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./news-list/news-list.component').then(m => m.NewsListComponent)
  }
];

