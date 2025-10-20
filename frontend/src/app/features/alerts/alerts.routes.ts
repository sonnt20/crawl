import { Routes } from '@angular/router';

export const ALERTS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./alerts-list/alerts-list.component').then(m => m.AlertsListComponent)
  }
];

