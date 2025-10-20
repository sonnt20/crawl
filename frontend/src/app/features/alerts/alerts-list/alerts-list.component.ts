import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-alerts-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <h1>🔔 Quản lý cảnh báo</h1>
      
      <div class="card">
        <h3>Tính năng đang phát triển</h3>
        <p>Chức năng quản lý cảnh báo từ khóa sẽ được cập nhật sớm.</p>
        
        <div class="features-list">
          <h4>Tính năng sắp có:</h4>
          <ul>
            <li>✅ Tạo cảnh báo theo từ khóa</li>
            <li>✅ Nhận thông báo qua Email</li>
            <li>✅ Nhận thông báo qua Telegram</li>
            <li>✅ Webhook cho hệ thống tự động</li>
            <li>✅ Hỗ trợ Regex pattern</li>
          </ul>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .features-list {
      margin-top: 20px;
      
      h4 {
        margin-bottom: 15px;
        color: #333;
      }
      
      ul {
        list-style: none;
        padding: 0;
        
        li {
          padding: 8px 0;
          color: #666;
        }
      }
    }
  `]
})
export class AlertsListComponent {}

