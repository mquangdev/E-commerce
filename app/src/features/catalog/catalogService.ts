import api from '@/services/api';
import { CATALOG_BASE_URL, SEARCH_BASE_URL } from '@/constants/api.const';
import { Category, CategoryCreateRequest, CategoryUpdateRequest } from './models/Category';
import { Product, ProductCreateRequest, ProductUpdateRequest } from './models/Product';

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
    `${SEARCH_BASE_URL}/api/search/categories`,
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

// === PRODUCTS SERVICE ===
export const getProducts = async (
  keyword: string,
  page: number = 0,
  size: number = 10
): Promise<PageResponse<Product>> => {
  const response = await api.get<PageResponse<Product>>(
    `${SEARCH_BASE_URL}/api/search/products`,
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
