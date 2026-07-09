import api from '@/services/api';
import { ORDER_BASE_URL } from '@/constants/api.const';
import { OrderResponse, OrderRequest } from './models/Order';
import { decodeJwt } from '@/utils/jwtHelper';

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

// === ORDERS SERVICE ===

export const getOrders = async (
  keyword: string,
  page: number = 0,
  size: number = 10
): Promise<PageResponse<OrderResponse>> => {
  const response = await api.get<PageResponse<OrderResponse>>(
    `${ORDER_BASE_URL}/api/v1/orders/search`,
    {
      params: { keyword, page, size },
    }
  );
  return response.data;
};

export const getOrderById = async (orderId: string): Promise<OrderResponse> => {
  const response = await api.get<OrderResponse>(
    `${ORDER_BASE_URL}/api/v1/orders/${orderId}`
  );
  return response.data;
};

export const createOrder = async (request: OrderRequest): Promise<OrderResponse> => {
  const token = localStorage.getItem('token');
  if (token) {
    const decoded = decodeJwt(token);
    if (decoded?.userId) {
      request.userId = decoded.userId;
    }
  }
  const response = await api.post<OrderResponse>(
    `${ORDER_BASE_URL}/api/v1/orders`,
    request
  );
  return response.data;
};
