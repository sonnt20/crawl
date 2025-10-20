export interface User {
  id?: number;
  email: string;
  fullName: string;
  role: string;
  subscriptionTier: string;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  fullName: string;
  role: string;
  subscriptionTier: string;
}

