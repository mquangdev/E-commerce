import api from '@/services/api';
import { AUTH_BASE_URL } from '@/constants/api.const';

export interface LoginPayload {
  username: string;
  password: string;
  deviceId: string;
}

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
  fullName: string;
}

// Gọi API đăng nhập, nhận về chuỗi accessToken
export const loginApi = async (payload: LoginPayload): Promise<string> => {
  const response = await api.post<string>(`${AUTH_BASE_URL}/api/v1/login`, payload);
  return response.data;
};

// Gọi API đăng ký, nhận về UUID của User mới đăng ký dưới dạng string
export const registerApi = async (payload: RegisterPayload): Promise<string> => {
  const response = await api.post<string>(`${AUTH_BASE_URL}/api/v1/register`, payload);
  return response.data;
};

// Gọi API refresh token, nhận về chuỗi accessToken mới
export const refreshTokenApi = async (): Promise<string> => {
  const response = await api.post<string>(`${AUTH_BASE_URL}/api/v1/refresh`);
  return response.data;
};
