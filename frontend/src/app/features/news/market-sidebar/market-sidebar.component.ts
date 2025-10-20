import { Component, signal, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

interface StockData {
  symbol: string;
  name: string;
  price: number;
  change: number;
  changePercent: number;
  volume: number;
}

@Component({
  selector: 'app-market-sidebar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="market-sidebar">
      <h3>📊 Thị trường chứng khoán</h3>

      <div class="tabs">
        <button
          class="tab"
          [class.active]="activeTab() === 'stocks'"
          (click)="activeTab.set('stocks')"
        >
          Cổ phiếu
        </button>
        <button
          class="tab"
          [class.active]="activeTab() === 'bonds'"
          (click)="activeTab.set('bonds')"
        >
          Trái phiếu
        </button>
        <button
          class="tab"
          [class.active]="activeTab() === 'funds'"
          (click)="activeTab.set('funds')"
        >
          Chứng chỉ quỹ
        </button>
      </div>

      <div class="market-data">
        @if (activeTab() === 'stocks') {
          <div class="data-list">
            @for (stock of stocks(); track stock.symbol) {
              <div class="data-item">
                <div class="item-header">
                  <span class="symbol">{{ stock.symbol }}</span>
                  <span class="price" [class.positive]="stock.change > 0" [class.negative]="stock.change < 0">
                    {{ stock.price | number:'1.2-2' }}
                  </span>
                </div>
                <div class="item-body">
                  <span class="name">{{ stock.name }}</span>
                  <span class="change" [class.positive]="stock.change > 0" [class.negative]="stock.change < 0">
                    {{ stock.change > 0 ? '+' : '' }}{{ stock.changePercent | number:'1.2-2' }}%
                  </span>
                </div>
                <div class="item-footer">
                  <span class="volume">KL: {{ formatVolume(stock.volume) }}</span>
                </div>
              </div>
            }
          </div>
        }

        @if (activeTab() === 'bonds') {
          <div class="data-list">
            @for (bond of bonds(); track bond.symbol) {
              <div class="data-item">
                <div class="item-header">
                  <span class="symbol">{{ bond.symbol }}</span>
                  <span class="price" [class.positive]="bond.change > 0" [class.negative]="bond.change < 0">
                    {{ bond.price | number:'1.2-2' }}
                  </span>
                </div>
                <div class="item-body">
                  <span class="name">{{ bond.name }}</span>
                  <span class="change" [class.positive]="bond.change > 0" [class.negative]="bond.change < 0">
                    {{ bond.change > 0 ? '+' : '' }}{{ bond.changePercent | number:'1.2-2' }}%
                  </span>
                </div>
                <div class="item-footer">
                  <span class="volume">KL: {{ formatVolume(bond.volume) }}</span>
                </div>
              </div>
            }
          </div>
        }

        @if (activeTab() === 'funds') {
          <div class="data-list">
            @for (fund of funds(); track fund.symbol) {
              <div class="data-item">
                <div class="item-header">
                  <span class="symbol">{{ fund.symbol }}</span>
                  <span class="price" [class.positive]="fund.change > 0" [class.negative]="fund.change < 0">
                    {{ fund.price | number:'1.2-2' }}
                  </span>
                </div>
                <div class="item-body">
                  <span class="name">{{ fund.name }}</span>
                  <span class="change" [class.positive]="fund.change > 0" [class.negative]="fund.change < 0">
                    {{ fund.change > 0 ? '+' : '' }}{{ fund.changePercent | number:'1.2-2' }}%
                  </span>
                </div>
                <div class="item-footer">
                  <span class="volume">NAV: {{ formatVolume(fund.volume) }}</span>
                </div>
              </div>
            }
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .market-sidebar {
      background: white;
      border-radius: 8px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      height: fit-content;
      position: sticky;
      top: 80px;
    }

    h3 {
      margin: 0 0 20px 0;
      font-size: 18px;
      color: #333;
    }

    .tabs {
      display: flex;
      gap: 8px;
      margin-bottom: 20px;
      border-bottom: 2px solid #e0e0e0;
    }

    .tab {
      flex: 1;
      padding: 10px 16px;
      border: none;
      background: transparent;
      cursor: pointer;
      font-size: 14px;
      color: #666;
      border-bottom: 2px solid transparent;
      margin-bottom: -2px;
      transition: all 0.3s ease;
    }

    .tab:hover {
      color: #007bff;
      background: #f8f9fa;
    }

    .tab.active {
      color: #007bff;
      border-bottom-color: #007bff;
      font-weight: 600;
    }

    .market-data {
      max-height: calc(100vh - 250px);
      overflow-y: auto;
    }

    .data-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .data-item {
      padding: 12px;
      border: 1px solid #e0e0e0;
      border-radius: 6px;
      transition: all 0.3s ease;
    }

    .data-item:hover {
      border-color: #007bff;
      box-shadow: 0 2px 8px rgba(0,123,255,0.1);
      transform: translateY(-2px);
    }

    .item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
    }

    .symbol {
      font-weight: 700;
      font-size: 16px;
      color: #333;
    }

    .price {
      font-weight: 600;
      font-size: 16px;
    }

    .price.positive {
      color: #28a745;
    }

    .price.negative {
      color: #dc3545;
    }

    .item-body {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;
    }

    .name {
      font-size: 13px;
      color: #666;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-right: 8px;
    }

    .change {
      font-size: 13px;
      font-weight: 600;
    }

    .change.positive {
      color: #28a745;
    }

    .change.negative {
      color: #dc3545;
    }

    .item-footer {
      display: flex;
      justify-content: flex-end;
    }

    .volume {
      font-size: 12px;
      color: #999;
    }

    /* Scrollbar styling */
    .market-data::-webkit-scrollbar {
      width: 6px;
    }

    .market-data::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }

    .market-data::-webkit-scrollbar-thumb {
      background: #888;
      border-radius: 3px;
    }

    .market-data::-webkit-scrollbar-thumb:hover {
      background: #555;
    }
  `]
})
export class MarketSidebarComponent implements OnInit {
  private http = inject(HttpClient);

  activeTab = signal<'stocks' | 'bonds' | 'funds'>('stocks');
  stocks = signal<StockData[]>([]);
  bonds = signal<StockData[]>([]);
  funds = signal<StockData[]>([]);
  loading = signal<boolean>(false);

  ngOnInit() {
    this.loadMarketData();

    // Auto refresh mỗi 30 giây
    setInterval(() => {
      this.loadMarketData();
    }, 30000);
  }

  loadMarketData() {
    this.loading.set(true);

    // Load stocks
    this.http.get<any>(`${environment.apiUrl}/market/stocks`).subscribe({
      next: (response) => {
        if (response.data) {
          this.stocks.set(response.data);
        }
      },
      error: (error) => {
        console.error('Error loading stocks:', error);
      }
    });

    // Load bonds
    this.http.get<any>(`${environment.apiUrl}/market/bonds`).subscribe({
      next: (response) => {
        if (response.data) {
          this.bonds.set(response.data);
        }
      },
      error: (error) => {
        console.error('Error loading bonds:', error);
      }
    });

    // Load funds
    this.http.get<any>(`${environment.apiUrl}/market/funds`).subscribe({
      next: (response) => {
        if (response.data) {
          this.funds.set(response.data);
        }
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading funds:', error);
        this.loading.set(false);
      }
    });
  }

  formatVolume(volume: number): string {
    if (volume >= 1000000) {
      return (volume / 1000000).toFixed(1) + 'M';
    } else if (volume >= 1000) {
      return (volume / 1000).toFixed(1) + 'K';
    }
    return volume.toString();
  }
}

