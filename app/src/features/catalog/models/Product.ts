export interface Product {
  id: string;
  categoryId: string;
  categoryName?: string; // Mapped from search service flat properties
  sku: string;
  name: string;
  description: string | null;
  price: number;
  imageUrl: string | null;
  stockQuantity: number;
  isDeleted: boolean;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface ProductCreateRequest {
  categoryId: string;
  sku: string;
  name: string;
  description?: string;
  price: number;
  imageUrl?: string;
  stockQuantity: number;
}

export interface ProductUpdateRequest {
  categoryId: string;
  name: string;
  description?: string;
  price: number;
  imageUrl?: string;
  stockQuantity: number;
}
