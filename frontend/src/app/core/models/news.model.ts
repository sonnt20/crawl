export interface News {
  id: number;
  title: string;
  url: string;
  source: string;
  description?: string;
  imageUrl?: string;
  tags: string[];
  publishedAt: string;
  crawledAt: string;
}

export interface NewsPage {
  content: News[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

