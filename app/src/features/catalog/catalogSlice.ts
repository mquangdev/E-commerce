import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Category, CategoryCreateRequest, CategoryUpdateRequest } from './models/Category';
import { Product, ProductCreateRequest, ProductUpdateRequest } from './models/Product';
import {
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory,
  getProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  PageResponse,
} from './catalogService';

export interface CatalogState {
  categories: Category[];
  categoriesLoading: boolean;
  categoriesError: string | null;
  categoriesTotal: number;
  categoriesPages: number;
  categoriesPageNum: number;

  products: Product[];
  productsLoading: boolean;
  productsError: string | null;
  productsTotal: number;
  productsPages: number;
  productsPageNum: number;
}

const initialState: CatalogState = {
  categories: [],
  categoriesLoading: false,
  categoriesError: null,
  categoriesTotal: 0,
  categoriesPages: 0,
  categoriesPageNum: 0,

  products: [],
  productsLoading: false,
  productsError: null,
  productsTotal: 0,
  productsPages: 0,
  productsPageNum: 0,
};

// === CATEGORIES THUNKS ===
export const fetchCategories = createAsyncThunk(
  'catalog/fetchCategories',
  async ({ keyword, page, size }: { keyword: string; page: number; size: number }, thunkAPI) => {
    try {
      return await getCategories(keyword, page, size);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể tải danh sách danh mục.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const createCategoryThunk = createAsyncThunk(
  'catalog/createCategory',
  async (payload: CategoryCreateRequest, thunkAPI) => {
    try {
      return await createCategory(payload);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể tạo danh mục.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const updateCategoryThunk = createAsyncThunk(
  'catalog/updateCategory',
  async ({ id, payload }: { id: string; payload: CategoryUpdateRequest }, thunkAPI) => {
    try {
      return await updateCategory(id, payload);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể cập nhật danh mục.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const deleteCategoryThunk = createAsyncThunk(
  'catalog/deleteCategory',
  async (id: string, thunkAPI) => {
    try {
      await deleteCategory(id);
      return id;
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể xóa danh mục.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

// === PRODUCTS THUNKS ===
export const fetchProducts = createAsyncThunk(
  'catalog/fetchProducts',
  async ({ keyword, page, size }: { keyword: string; page: number; size: number }, thunkAPI) => {
    try {
      return await getProducts(keyword, page, size);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể tải danh sách sản phẩm.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const createProductThunk = createAsyncThunk(
  'catalog/createProduct',
  async (payload: ProductCreateRequest, thunkAPI) => {
    try {
      return await createProduct(payload);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể tạo sản phẩm.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const updateProductThunk = createAsyncThunk(
  'catalog/updateProduct',
  async ({ id, payload }: { id: string; payload: ProductUpdateRequest }, thunkAPI) => {
    try {
      return await updateProduct(id, payload);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể cập nhật sản phẩm.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const deleteProductThunk = createAsyncThunk(
  'catalog/deleteProduct',
  async (id: string, thunkAPI) => {
    try {
      await deleteProduct(id);
      return id;
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể xóa sản phẩm.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

const catalogSlice = createSlice({
  name: 'catalog',
  initialState,
  reducers: {
    clearErrors: (state) => {
      state.categoriesError = null;
      state.productsError = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Categories
      .addCase(fetchCategories.pending, (state) => {
        state.categoriesLoading = true;
        state.categoriesError = null;
      })
      .addCase(fetchCategories.fulfilled, (state, action: PayloadAction<PageResponse<Category>>) => {
        state.categoriesLoading = false;
        state.categories = action.payload.content.filter((cat) => !cat.isDeleted);
        state.categoriesTotal = action.payload.totalElements;
        state.categoriesPages = action.payload.totalPages;
        state.categoriesPageNum = action.payload.number;
      })
      .addCase(fetchCategories.rejected, (state, action) => {
        state.categoriesLoading = false;
        state.categoriesError = action.payload as string;
      })

      // Fetch Products
      .addCase(fetchProducts.pending, (state) => {
        state.productsLoading = true;
        state.productsError = null;
      })
      .addCase(fetchProducts.fulfilled, (state, action: PayloadAction<PageResponse<Product>>) => {
        state.productsLoading = false;
        state.products = action.payload.content.filter((prod) => !prod.isDeleted);
        state.productsTotal = action.payload.totalElements;
        state.productsPages = action.payload.totalPages;
        state.productsPageNum = action.payload.number;
      })
      .addCase(fetchProducts.rejected, (state, action) => {
        state.productsLoading = false;
        state.productsError = action.payload as string;
      });
  },
});

export const { clearErrors } = catalogSlice.actions;
export default catalogSlice.reducer;
