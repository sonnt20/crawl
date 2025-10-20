import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <header class="header">
      <div class="container">
        <div class="header-content">
          <div class="logo">
            <a routerLink="/">📈 Crawl</a>
          </div>

          @if (authService.isAuthenticated()) {
            <nav class="nav">
              <a routerLink="/news" routerLinkActive="active">Tin tức</a>
              <a routerLink="/alerts" routerLinkActive="active">Cảnh báo</a>
              <a routerLink="/profile" routerLinkActive="active">Tài khoản</a>
              <button class="btn-logout" (click)="logout()">Đăng xuất</button>
            </nav>
          } @else {
            <nav class="nav">
              <a routerLink="/auth/login" routerLinkActive="active">Đăng nhập</a>
              <a routerLink="/auth/register" routerLinkActive="active" class="btn-register">Đăng ký</a>
            </nav>
          }
        </div>
      </div>
    </header>
  `,
  styles: [`
    .header {
      background: white;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      position: sticky;
      top: 0;
      z-index: 1000;
    }

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px 0;
    }

    .logo a {
      font-size: 24px;
      font-weight: bold;
      color: #007bff;
      text-decoration: none;
    }

    .nav {
      display: flex;
      gap: 20px;
      align-items: center;

      a {
        text-decoration: none;
        color: #333;
        font-weight: 500;
        transition: color 0.3s;

        &:hover, &.active {
          color: #007bff;
        }

        &.btn-register {
          background: #007bff;
          color: white;
          padding: 8px 16px;
          border-radius: 4px;

          &:hover {
            background: #0056b3;
            color: white;
          }
        }
      }

      .btn-logout {
        background: #dc3545;
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 4px;
        cursor: pointer;
        font-weight: 500;

        &:hover {
          background: #c82333;
        }
      }
    }
  `]
})
export class HeaderComponent {
  authService = inject(AuthService);

  logout(): void {
    this.authService.logout();
  }
}

