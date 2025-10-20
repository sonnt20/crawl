import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>Đăng ký tài khoản</h2>
        
        @if (errorMessage) {
          <div class="error">{{ errorMessage }}</div>
        }

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div class="form-group">
            <label for="fullName">Họ và tên</label>
            <input 
              id="fullName" 
              type="text" 
              formControlName="fullName"
              placeholder="Nguyễn Văn A"
            />
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input 
              id="email" 
              type="email" 
              formControlName="email"
              placeholder="your@email.com"
            />
          </div>

          <div class="form-group">
            <label for="password">Mật khẩu</label>
            <input 
              id="password" 
              type="password" 
              formControlName="password"
              placeholder="••••••••"
            />
            @if (registerForm.get('password')?.invalid && registerForm.get('password')?.touched) {
              <small class="error-text">Mật khẩu phải có ít nhất 6 ký tự</small>
            }
          </div>

          <button 
            type="submit" 
            class="btn btn-primary btn-block"
            [disabled]="registerForm.invalid || loading"
          >
            {{ loading ? 'Đang đăng ký...' : 'Đăng ký' }}
          </button>
        </form>

        <p class="auth-link">
          Đã có tài khoản? <a routerLink="/auth/login">Đăng nhập</a>
        </p>
      </div>
    </div>
  `,
  styles: [`
    .auth-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: calc(100vh - 60px);
      padding: 20px;
    }

    .auth-card {
      background: white;
      padding: 40px;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      width: 100%;
      max-width: 400px;

      h2 {
        margin-bottom: 30px;
        text-align: center;
        color: #333;
      }
    }

    .btn-block {
      width: 100%;
      margin-top: 10px;
    }

    .auth-link {
      text-align: center;
      margin-top: 20px;
      color: #666;

      a {
        color: #007bff;
        text-decoration: none;
        font-weight: 500;

        &:hover {
          text-decoration: underline;
        }
      }
    }

    .error-text {
      color: #dc3545;
      font-size: 12px;
      margin-top: 5px;
      display: block;
    }
  `]
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor() {
    this.registerForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.loading = true;
      this.errorMessage = '';

      this.authService.register(this.registerForm.value).subscribe({
        next: () => {
          this.router.navigate(['/news']);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Đăng ký thất bại. Vui lòng thử lại.';
          this.loading = false;
        }
      });
    }
  }
}

