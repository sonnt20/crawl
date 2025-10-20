import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { News, NewsPage } from '../models/news.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private readonly API_URL = `${environment.apiUrl}/news`;

  constructor(private http: HttpClient) {}

  getNews(page: number = 0, size: number = 20): Observable<NewsPage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<NewsPage>(this.API_URL, { params });
  }

  searchNews(keyword: string, page: number = 0, size: number = 20): Observable<NewsPage> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<NewsPage>(`${this.API_URL}/search`, { params });
  }

  getNewsBySource(source: string, page: number = 0, size: number = 20): Observable<NewsPage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<NewsPage>(`${this.API_URL}/source/${source}`, { params });
  }

  getNewsById(id: number): Observable<News> {
    return this.http.get<News>(`${this.API_URL}/${id}`);
  }
}

