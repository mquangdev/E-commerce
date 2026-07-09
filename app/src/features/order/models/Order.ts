export type OrderStatus =
  | 'PENDING'
  | 'PROCESSING'
  | 'COMPLETED'
  | 'CANCELLING'
  | 'CANCELLED'
  | 'FAILED';

export interface OrderItemResponse {
  id: string;
  productId: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderResponse {
  id: string;
  userId: string;
  totalAmount: number;
  shippingAddress: string;
  email: string;
  status: OrderStatus;
  version: number;
  createdAt: string;
  updatedAt: string;
  items: OrderItemResponse[];
}

export interface OrderItemRequest {
  productId: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderRequest {
  userId?: string;
  shippingAddress: string;
  email: string;
  items: OrderItemRequest[];
}
