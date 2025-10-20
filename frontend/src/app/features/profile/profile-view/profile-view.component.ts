import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-profile-view',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <h1>👤 Thông tin tài khoản</h1>

      @if (authService.currentUser(); as user) {
        <div class="card">
          <div class="profile-info">
            <div class="info-row">
              <label>Họ và tên:</label>
              <span>{{ user.fullName }}</span>
            </div>

            <div class="info-row">
              <label>Email:</label>
              <span>{{ user.email }}</span>
            </div>

            <div class="info-row">
              <label>Gói dịch vụ:</label>
              <span class="subscription-badge" [class]="'badge-' + user.subscriptionTier.toLowerCase()">
                {{ getSubscriptionName(user.subscriptionTier) }}
              </span>
            </div>

            <div class="info-row">
              <label>Vai trò:</label>
              <span>{{ user.role }}</span>
            </div>
          </div>
        </div>

        <div class="card">
          <h3>Nâng cấp gói dịch vụ</h3>
          <div class="subscription-plans">
            <div class="plan"
                 [class.current]="user.subscriptionTier === 'FREE'"
                 [class.disabled]="user.subscriptionTier !== 'FREE'">
              <h4>FREE</h4>
              <p class="price">$0/tháng</p>
              <ul>
                <li>Crawl mỗi 30-60 phút</li>
                <li>Xem tin 7 ngày</li>
                <li>Không có cảnh báo</li>
              </ul>
              <div class="button-container">
                @if (user.subscriptionTier === 'FREE') {
                  <button class="btn btn-secondary" disabled>Gói hiện tại</button>
                } @else {
                  <span class="badge-upgraded">✓ Đã nâng cấp</span>
                }
              </div>
            </div>

            <div class="plan"
                 [class.current]="user.subscriptionTier === 'PRO'"
                 [class.disabled]="user.subscriptionTier === 'PREMIUM'">
              <h4>PRO</h4>
              <p class="price">$10-20/tháng</p>
              <ul>
                <li>Crawl mỗi 5 phút</li>
                <li>Xem tin 30 ngày</li>
                <li>5 keyword alerts</li>
                <li>Email/Telegram alerts</li>
              </ul>
              <div class="button-container">
                @if (user.subscriptionTier === 'PRO') {
                  <button class="btn btn-secondary" disabled>Gói hiện tại</button>
                } @else if (user.subscriptionTier === 'PREMIUM') {
                  <span class="badge-upgraded">✓ Đã nâng cấp</span>
                } @else {
                  <button class="btn btn-primary" (click)="upgradeToPro()">Nâng cấp</button>
                }
              </div>
            </div>

            <div class="plan"
                 [class.current]="user.subscriptionTier === 'PREMIUM'">
              <h4>PREMIUM</h4>
              <p class="price">$50-200/tháng</p>
              <ul>
                <li>Crawl mỗi 1-2 phút</li>
                <li>Xem tin 90 ngày</li>
                <li>Unlimited alerts</li>
                <li>Webhook support</li>
                <li>API access</li>
              </ul>
              <div class="button-container">
                @if (user.subscriptionTier === 'PREMIUM') {
                  <button class="btn btn-secondary" disabled>Gói hiện tại</button>
                } @else {
                  <button class="btn btn-success" (click)="upgradeToPremium()">Nâng cấp</button>
                }
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .profile-info {
      .info-row {
        display: flex;
        padding: 15px 0;
        border-bottom: 1px solid #eee;

        &:last-child {
          border-bottom: none;
        }

        label {
          font-weight: 600;
          width: 150px;
          color: #666;
        }

        span {
          flex: 1;
          color: #333;
        }
      }
    }

    .subscription-badge {
      display: inline-block;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;

      &.badge-free {
        background: #e9ecef;
        color: #495057;
      }

      &.badge-pro {
        background: #007bff;
        color: white;
      }

      &.badge-premium {
        background: #ffd700;
        color: #333;
      }
    }

    .subscription-plans {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-top: 20px;

      .plan {
        border: 2px solid #ddd;
        border-radius: 8px;
        padding: 20px;
        text-align: center;
        transition: all 0.3s ease;
        cursor: pointer;
        display: flex;
        flex-direction: column;

        &:hover:not(.disabled) {
          transform: translateY(-5px);
          box-shadow: 0 8px 16px rgba(0,0,0,0.15);
          border-color: #007bff;
        }

        &.current {
          border-color: #28a745;
          background: #f0fff4;
          position: relative;

          &:hover {
            box-shadow: 0 8px 20px rgba(40,167,69,0.3);
          }

          &::before {
            content: "✓ Đang sử dụng";
            position: absolute;
            top: -10px;
            right: 20px;
            background: #28a745;
            color: white;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
          }
        }

        h4 {
          margin-bottom: 10px;
          color: #333;
        }

        .price {
          font-size: 24px;
          font-weight: bold;
          color: #007bff;
          margin-bottom: 20px;
        }

        ul {
          list-style: none;
          padding: 0;
          margin-bottom: 20px;
          text-align: left;
          flex: 1;

          li {
            padding: 8px 0;
            color: #666;

            &:before {
              content: "✓ ";
              color: #28a745;
              font-weight: bold;
            }
          }
        }

        .button-container {
          display: flex;
          align-items: stretch;

          button, .badge-upgraded {
            flex: 1;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0;
          }
        }

        button {
          width: 100%;
          transition: all 0.3s ease;
          cursor: pointer;

          &:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
          }

          &:active:not(:disabled) {
            transform: translateY(0);
          }

          &:disabled {
            cursor: not-allowed;
            opacity: 0.6;
          }
        }

        &.disabled {
          opacity: 0.7;

          button {
            cursor: not-allowed;
          }
        }

        .badge-upgraded {
          background: #e9ecef;
          color: #28a745;
          border-radius: 4px;
          font-size: 14px;
          font-weight: 600;
          border: none;
        }
      }
    }
  `]
})
export class ProfileViewComponent {
  authService = inject(AuthService);

  getSubscriptionName(tier: string): string {
    const names: { [key: string]: string } = {
      'FREE': 'Miễn phí',
      'PRO': 'Chuyên nghiệp',
      'PREMIUM': 'Cao cấp'
    };
    return names[tier] || tier;
  }

  upgradeToPro(): void {
    alert('🚀 Chức năng nâng cấp lên PRO!\n\nTính năng thanh toán Stripe sẽ được tích hợp sau.\n\nBạn sẽ được chuyển đến trang thanh toán...');
    // TODO: Integrate Stripe payment
    console.log('Upgrade to PRO clicked');
  }

  upgradeToPremium(): void {
    alert('💎 Chức năng nâng cấp lên PREMIUM!\n\nTính năng thanh toán Stripe sẽ được tích hợp sau.\n\nBạn sẽ được chuyển đến trang thanh toán...');
    // TODO: Integrate Stripe payment
    console.log('Upgrade to PREMIUM clicked');
  }
}

