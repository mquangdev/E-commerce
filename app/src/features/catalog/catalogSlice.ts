import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { Category, CategoryCreateRequest, CategoryUpdateRequest } from './models/Category';
import { Product, ProductCreateRequest, ProductUpdateRequest } from './models/Product';
import {
  getCategories,
  createCategory,
  updateCategory,
  deleteCategory,
  importCategories,
  exportCategories,
  getProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  importProducts,
  exportProducts,
  PageResponse,
  InventoryResponse,
  getProductInventory,
  ProductReceiveMoreInventoryRequest,
  receiveMoreInventory,
} from './catalogService';

export interface CatalogState {
  categories: Category[];
  categoriesLoading: boolean;
  categoriesError: string | null;
  categoriesTotal: number;
  categoriesPages: number;
  categoriesPageNum: number;
  categoriesImporting: boolean;
  categoriesExporting: boolean;

  products: Product[];
  productsLoading: boolean;
  productsError: string | null;
  productsTotal: number;
  productsPages: number;
  productsPageNum: number;
  productsImporting: boolean;
  productsExporting: boolean;

  currentProductInventory: InventoryResponse | null;
  currentProductInventoryLoading: boolean;
  currentProductInventoryError: string | null;
}

const initialState: CatalogState = {
  categories: [],
  categoriesLoading: false,
  categoriesError: null,
  categoriesTotal: 0,
  categoriesPages: 0,
  categoriesPageNum: 0,
  categoriesImporting: false,
  categoriesExporting: false,

  products: [],
  productsLoading: false,
  productsError: null,
  productsTotal: 0,
  productsPages: 0,
  productsPageNum: 0,
  productsImporting: false,
  productsExporting: false,

  currentProductInventory: null,
  currentProductInventoryLoading: false,
  currentProductInventoryError: null,
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

export const importCategoriesThunk = createAsyncThunk(
  'catalog/importCategories',
  async (file: File, thunkAPI) => {
    try {
      await importCategories(file);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể nhập danh sách danh mục.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const exportCategoriesThunk = createAsyncThunk(
  'catalog/exportCategories',
  async (_, thunkAPI) => {
    try {
      return await exportCategories();
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể xuất danh sách danh mục.';
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

export const receiveMoreInventoryThunk = createAsyncThunk(
  'catalog/receiveMoreInventory',
  async (
    { id, payload }: { id: string; payload: ProductReceiveMoreInventoryRequest },
    thunkAPI
  ) => {
    try {
      return await receiveMoreInventory(id, payload);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể nhập thêm sản phẩm.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const fetchProductInventory = createAsyncThunk(
  'catalog/fetchProductInventory',
  async (productId: string, thunkAPI) => {
    try {
      return await getProductInventory(productId);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể tải thông tin tồn kho.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const importProductsThunk = createAsyncThunk(
  'catalog/importProducts',
  async (file: File, thunkAPI) => {
    try {
      await importProducts(file);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể nhập danh sách sản phẩm.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const exportProductsThunk = createAsyncThunk(
  'catalog/exportProducts',
  async (_, thunkAPI) => {
    try {
      return await exportProducts();
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể xuất danh sách sản phẩm.';
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
    clearCurrentInventory: (state) => {
      state.currentProductInventory = null;
      state.currentProductInventoryError = null;
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

      // Add category
      .addCase(createCategoryThunk.fulfilled, (state, action) => {
        state.categories.unshift(action.payload);
        state.categoriesTotal++;
        state.categoriesError = null;
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
      })

      // Add product
      .addCase(createProductThunk.fulfilled, (state, action) => {
        state.products.unshift(action.payload);
        state.productsTotal++;
        state.productsError = null;
      })

      // Receive More Inventory
      .addCase(receiveMoreInventoryThunk.fulfilled, (state, action: PayloadAction<Product>) => {
        const index = state.products.findIndex((p) => p.id === action.payload.id);
        if (index !== -1) {
          state.products[index] = action.payload;
        }
        state.productsError = null;
      })

      // Fetch Product Inventory
      .addCase(fetchProductInventory.pending, (state) => {
        state.currentProductInventoryLoading = true;
        state.currentProductInventoryError = null;
      })
      .addCase(fetchProductInventory.fulfilled, (state, action: PayloadAction<InventoryResponse>) => {
        state.currentProductInventoryLoading = false;
        state.currentProductInventory = action.payload;
        state.currentProductInventoryError = null;
      })
      .addCase(fetchProductInventory.rejected, (state, action) => {
        state.currentProductInventoryLoading = false;
        state.currentProductInventory = null;
        state.currentProductInventoryError = action.payload as string;
      })

      // Import Categories
      .addCase(importCategoriesThunk.pending, (state) => {
        state.categoriesImporting = true;
        state.categoriesError = null;
      })
      .addCase(importCategoriesThunk.fulfilled, (state) => {
        state.categoriesImporting = false;
        state.categoriesError = null;
      })
      .addCase(importCategoriesThunk.rejected, (state, action) => {
        state.categoriesImporting = false;
        state.categoriesError = action.payload as string;
      })

      // Export Categories
      .addCase(exportCategoriesThunk.pending, (state) => {
        state.categoriesExporting = true;
        state.categoriesError = null;
      })
      .addCase(exportCategoriesThunk.fulfilled, (state) => {
        state.categoriesExporting = false;
        state.categoriesError = null;
      })
      .addCase(exportCategoriesThunk.rejected, (state, action) => {
        state.categoriesExporting = false;
        state.categoriesError = action.payload as string;
      })

      // Import Products
      .addCase(importProductsThunk.pending, (state) => {
        state.productsImporting = true;
        state.productsError = null;
      })
      .addCase(importProductsThunk.fulfilled, (state) => {
        state.productsImporting = false;
        state.productsError = null;
      })
      .addCase(importProductsThunk.rejected, (state, action) => {
        state.productsImporting = false;
        state.productsError = action.payload as string;
      })

      // Export Products
      .addCase(exportProductsThunk.pending, (state) => {
        state.productsExporting = true;
        state.productsError = null;
      })
      .addCase(exportProductsThunk.fulfilled, (state) => {
        state.productsExporting = false;
        state.productsError = null;
      })
      .addCase(exportProductsThunk.rejected, (state, action) => {
        state.productsExporting = false;
        state.productsError = action.payload as string;
      });
  },
});

export const { clearErrors, clearCurrentInventory } = catalogSlice.actions;
export default catalogSlice.reducer;
