import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NewsService } from '../../../core/services/news.service';
import { News, NewsPage } from '../../../core/models/news.model';
import { MarketSidebarComponent } from '../market-sidebar/market-sidebar.component';

@Component({
  selector: 'app-news-list',
  standalone: true,
  imports: [CommonModule, FormsModule, MarketSidebarComponent],
  template: `
    <div class="news-layout">
      <app-market-sidebar class="sidebar"></app-market-sidebar>

      <div class="container">
      <div class="news-header">
        <div class="header-top">
          <h1>📰 Tin tức mới nhất</h1>
          <button class="btn btn-success" (click)="triggerCrawl()" [disabled]="crawling()">
            @if (crawling()) {
              ⏳ Đang crawl...
            } @else {
              🔄 Crawl ngay
            }
          </button>
        </div>

        <div class="filters">
          <div class="filter-group">
            <label>Thời gian:</label>
            <select [(ngModel)]="selectedDays" (change)="applyFilters()">
              <option value="1">Hôm nay</option>
              <option value="7">7 ngày</option>
              <option value="30">30 ngày</option>
              <option value="90">90 ngày</option>
            </select>
          </div>

          <div class="filter-group">
            <label>Nguồn:</label>
            <select [(ngModel)]="selectedSource" (change)="applyFilters()">
              <option value="">Tất cả</option>
              <option value="CAFEF">CafeF</option>
              <option value="VIETSTOCK">VietStock</option>
              <option value="SSI">SSI</option>
            </select>
          </div>
        </div>

        <div class="search-box">
          <input
            type="text"
            [(ngModel)]="searchKeyword"
            (keyup.enter)="search()"
            placeholder="Tìm kiếm tin tức..."
          />
          <button class="btn btn-primary" (click)="search()">Tìm kiếm</button>
        </div>
      </div>

      @if (loading()) {
        <div class="loading">Đang tải tin tức...</div>
      } @else if (errorMessage()) {
        <div class="error">{{ errorMessage() }}</div>
      } @else {
        <div class="news-grid">
          @for (news of newsList(); track news.id) {
            <div class="news-card">
              @if (news.imageUrl) {
                <img [src]="news.imageUrl" [alt]="news.title" class="news-image" />
              }

              <div class="news-content">
                <div class="news-meta">
                  <span class="source">{{ news.source }}</span>
                  <span class="date" title="Giờ đăng: {{ formatDateTime(news.publishedAt) }}">
                    📅 {{ formatDate(news.publishedAt) }}
                  </span>
                  <span class="crawled-date" title="Giờ crawl: {{ formatDateTime(news.crawledAt) }}">
                    🕐 Crawl: {{ formatTime(news.crawledAt) }}
                  </span>
                </div>

                <h3 class="news-title">
                  <a [href]="news.url" target="_blank">{{ news.title }}</a>
                </h3>

                @if (news.description) {
                  <p class="news-description">{{ news.description }}</p>
                }

                @if (news.tags && news.tags.length > 0) {
                  <div class="news-tags">
                    @for (tag of news.tags; track tag) {
                      <span class="tag">{{ tag }}</span>
                    }
                  </div>
                }
              </div>
            </div>
          }
        </div>

        @if (newsPage()) {
          <div class="pagination">
            <button
              class="btn btn-secondary"
              [disabled]="currentPage() === 0"
              (click)="previousPage()"
            >
              ← Trước
            </button>

            <span class="page-info">
              Trang {{ currentPage() + 1 }} / {{ newsPage()!.totalPages }}
            </span>

            <button
              class="btn btn-secondary"
              [disabled]="currentPage() >= newsPage()!.totalPages - 1"
              (click)="nextPage()"
            >
              Sau →
            </button>
          </div>
        }
      }
      </div>
    </div>
  `,
  styles: [`
    .news-layout {
      display: grid;
      grid-template-columns: 300px 1fr;
      gap: 30px;
      max-width: 1400px;
      margin: 0 auto;
      padding: 20px;
    }

    .sidebar {
      display: block;
    }

    .container {
      min-width: 0;
    }

    @media (max-width: 1024px) {
      .news-layout {
        grid-template-columns: 1fr;
      }

      .sidebar {
        display: none;
      }
    }

    .news-header {
      margin-bottom: 30px;

      .header-top {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        h1 {
          margin: 0;
        }
      }
    }

    .filters {
      display: flex;
      gap: 20px;
      margin-bottom: 20px;
      flex-wrap: wrap;

      .filter-group {
        display: flex;
        align-items: center;
        gap: 10px;

        label {
          font-weight: 600;
          color: #333;
        }

        select {
          padding: 8px 12px;
          border: 1px solid #ddd;
          border-radius: 4px;
          font-size: 14px;
          cursor: pointer;

          &:focus {
            outline: none;
            border-color: #007bff;
          }
        }
      }
    }

    .search-box {
      display: flex;
      gap: 10px;
      max-width: 600px;

      input {
        flex: 1;
        padding: 10px;
        border: 1px solid #ddd;
        border-radius: 4px;
        font-size: 14px;
      }
    }

    .news-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }

    .news-card {
      background: white;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      transition: transform 0.2s, box-shadow 0.2s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      }
    }

    .news-image {
      width: 100%;
      height: 200px;
      object-fit: cover;
    }

    .news-content {
      padding: 20px;
    }

    .news-meta {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      margin-bottom: 10px;
      font-size: 12px;
      color: #666;

      .source {
        font-weight: 600;
        color: #007bff;
      }

      .date {
        color: #28a745;
        cursor: help;
      }

      .crawled-date {
        color: #6c757d;
        font-size: 11px;
        cursor: help;
      }
    }

    .news-title {
      margin-bottom: 10px;
      font-size: 18px;
      line-height: 1.4;

      a {
        color: #333;
        text-decoration: none;

        &:hover {
          color: #007bff;
        }
      }
    }

    .news-description {
      color: #666;
      font-size: 14px;
      line-height: 1.6;
      margin-bottom: 15px;
    }

    .news-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .tag {
        background: #e9ecef;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
        color: #495057;
      }
    }

    .pagination {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 20px;
      margin: 40px 0;

      .page-info {
        font-weight: 500;
      }
    }
  `]
})
export class NewsListComponent implements OnInit {
  private newsService = inject(NewsService);
  private http = inject(HttpClient);

  newsList = signal<News[]>([]);
  newsPage = signal<NewsPage | null>(null);
  currentPage = signal(0);
  loading = signal(false);
  crawling = signal(false);
  errorMessage = signal('');
  searchKeyword = '';
  selectedDays = '7';
  selectedSource = '';

  ngOnInit(): void {
    // Tự động crawl khi vào trang
    this.triggerCrawl();

    // Load news sau 3 giây (đợi crawl xong)
    setTimeout(() => {
      this.loadNews();
    }, 3000);
  }

  applyFilters(): void {
    this.currentPage.set(0);
    this.loadNews();
  }

  triggerCrawl(): void {
    this.crawling.set(true);
    this.errorMessage.set('');

    this.http.post<any>('http://localhost:8080/api/crawl/user-trigger', {}).subscribe({
      next: (response) => {
        console.log('Crawl triggered:', response);

        // Đợi 3 giây rồi reload news
        setTimeout(() => {
          this.loadNews();
          this.crawling.set(false);
        }, 3000);
      },
      error: (error) => {
        console.error('Error triggering crawl:', error);
        this.crawling.set(false);

        // Handle rate limit error
        if (error.status === 429) {
          const message = error.error?.message || 'Vui lòng đợi trước khi crawl tiếp';
          this.errorMessage.set(message);

          // Clear error sau 5 giây
          setTimeout(() => {
            this.errorMessage.set('');
          }, 5000);
        } else {
          this.errorMessage.set('Lỗi khi crawl dữ liệu. Vui lòng thử lại.');
        }
      }
    });
  }

  loadNews(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.newsService.getNews(this.currentPage(), 20).subscribe({
      next: (page) => {
        this.newsPage.set(page);
        this.newsList.set(page.content);
        this.loading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Không thể tải tin tức. Vui lòng thử lại.');
        this.loading.set(false);
      }
    });
  }

  search(): void {
    if (this.searchKeyword.trim()) {
      this.loading.set(true);
      this.currentPage.set(0);

      this.newsService.searchNews(this.searchKeyword, 0, 20).subscribe({
        next: (page) => {
          this.newsPage.set(page);
          this.newsList.set(page.content);
          this.loading.set(false);
        },
        error: (error) => {
          this.errorMessage.set('Không thể tìm kiếm. Vui lòng thử lại.');
          this.loading.set(false);
        }
      });
    } else {
      this.loadNews();
    }
  }

  nextPage(): void {
    this.currentPage.update(page => page + 1);
    this.loadNews();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  previousPage(): void {
    this.currentPage.update(page => Math.max(0, page - 1));
    this.loadNews();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 60) {
      return `${diffMins} phút trước`;
    } else if (diffHours < 24) {
      return `${diffHours} giờ trước`;
    } else if (diffDays < 7) {
      return `${diffDays} ngày trước`;
    } else {
      return date.toLocaleDateString('vi-VN');
    }
  }

  formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }

  formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('vi-VN', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}

