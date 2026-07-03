export interface Category {
  id: string;
  name: string;
  description: string | null;
  isActive: boolean;
  isDeleted: boolean;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface CategoryCreateRequest {
  name: string;
  description?: string;
  isActive?: boolean;
}

export interface CategoryUpdateRequest {
  name: string;
  description?: string;
  isActive?: boolean;
}
