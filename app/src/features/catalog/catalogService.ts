import api from '@/services/api';
import { CATALOG_BASE_URL, SEARCH_BASE_URL, INVENTORY_BASE_URL } from '@/constants/api.const';
import { Category, CategoryCreateRequest, CategoryUpdateRequest } from './models/Category';
import { Product, ProductCreateRequest, ProductUpdateRequest } from './models/Product';

export interface InventoryResponse {
  id: string;
  productId: string;
  availableQuantity: number;
  reservedQuantity: number;
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// === CATEGORIES SERVICE ===
export const getCategories = async (
  keyword: string,
  page: number = 0,
  size: number = 10
): Promise<PageResponse<Category>> => {
  const response = await api.get<PageResponse<Category>>(
    `${SEARCH_BASE_URL}/api/categories`,
    {
      params: { keyword, page, size },
    }
  );
  return response.data;
};

export const createCategory = async (payload: CategoryCreateRequest): Promise<Category> => {
  const response = await api.post<Category>(`${CATALOG_BASE_URL}/api/v1/categories`, payload);
  return response.data;
};

export const updateCategory = async (
  id: string,
  payload: CategoryUpdateRequest
): Promise<Category> => {
  const response = await api.put<Category>(`${CATALOG_BASE_URL}/api/v1/categories/${id}`, payload);
  return response.data;
};

export const deleteCategory = async (id: string): Promise<void> => {
  await api.delete(`${CATALOG_BASE_URL}/api/v1/categories/${id}`);
};

export const importCategories = async (file: File): Promise<void> => {
  const formData = new FormData();
  formData.append('file', file);
  await api.post(`${CATALOG_BASE_URL}/api/v1/categories/import`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const exportCategories = async (): Promise<Blob> => {
  const response = await api.get(`${CATALOG_BASE_URL}/api/v1/categories/export`, {
    responseType: 'blob',
  });
  return response.data;
};

// === PRODUCTS SERVICE ===
export const getProducts = async (
  keyword: string,
  page: number = 0,
  size: number = 10
): Promise<PageResponse<Product>> => {
  const response = await api.get<PageResponse<Product>>(
    `${SEARCH_BASE_URL}/api/products`,
    {
      params: { keyword, page, size },
    }
  );
  return response.data;
};

export const createProduct = async (payload: ProductCreateRequest): Promise<Product> => {
  const response = await api.post<Product>(`${CATALOG_BASE_URL}/api/v1/products`, payload);
  return response.data;
};

export const updateProduct = async (
  id: string,
  payload: ProductUpdateRequest
): Promise<Product> => {
  const response = await api.put<Product>(`${CATALOG_BASE_URL}/api/v1/products/${id}`, payload);
  return response.data;
};

export const deleteProduct = async (id: string): Promise<void> => {
  await api.delete(`${CATALOG_BASE_URL}/api/v1/products/${id}`);
};

export const getProductInventory = async (productId: string): Promise<InventoryResponse> => {
  const response = await api.get<InventoryResponse>(
    `${INVENTORY_BASE_URL}/api/v1/inventories/${productId}`
  );
  return response.data;
};

export interface ProductReceiveMoreInventoryRequest {
  addedInventory: number;
}

export const receiveMoreInventory = async (
  id: string,
  payload: ProductReceiveMoreInventoryRequest
): Promise<Product> => {
  const response = await api.put<Product>(
    `${CATALOG_BASE_URL}/api/v1/products/receiveMoreInventory/${id}`,
    payload
  );
  return response.data;
};

export const importProducts = async (file: File): Promise<void> => {
  const formData = new FormData();
  formData.append('file', file);
  await api.post(`${CATALOG_BASE_URL}/api/v1/products/import`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const exportProducts = async (): Promise<Blob> => {
  const response = await api.get(`${CATALOG_BASE_URL}/api/v1/products/export`, {
    responseType: 'blob',
  });
  return response.data;
};
