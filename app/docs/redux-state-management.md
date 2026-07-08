# Quy chuẩn Quản lý Trạng thái với Redux Toolkit (redux-state-management.md)

Tài liệu này đặc tả quy định kiến trúc bắt buộc của dự án: **Mọi tính năng (feature) trong hệ thống đều phải sử dụng Redux Toolkit để quản lý trạng thái thông qua Redux Slice.** Việc sử dụng local state (React `useState`) cho các dữ liệu nghiệp vụ quan trọng từ API là không được phép.

---

## 1. Nguyên tắc Kiến trúc Bắt buộc

Tất cả các tính năng đặt tại `src/features/<feature-name>/` phải tuân thủ mô hình 3 lớp sau:

1. **Service Layer (`<feature>Service.ts`)**:
   * Chỉ chứa các hàm thuần túy gửi HTTP Request (sử dụng Axios client) và trả về Promise dữ liệu.
   * Không lưu trữ dữ liệu, không xử lý loading/error trực tiếp.
2. **State Layer (`<feature>Slice.ts`)**:
   * Chứa trạng thái của tính năng trong Redux Store (gồm danh sách thực thể, trạng thái `loading`, thông báo `error`, phân trang, bộ lọc).
   * Sử dụng `createAsyncThunk` để bọc các hàm Service không đồng bộ.
   * Cập nhật trạng thái thông qua các reducers (`extraReducers` cho async thunks).
3. **UI Layer (Components/Pages)**:
   * Chỉ lấy dữ liệu thông qua hook `useAppSelector`.
   * Thực hiện các hành động thay đổi dữ liệu thông qua việc `dispatch` các thunks holặc actions bằng `useAppDispatch`.
   * Không tự ý gọi trực tiếp các hàm trong Service Layer tại Component.

---

## 2. Tiêu chuẩn Thiết kế một Redux Slice

Một Redux Slice chuẩn mực trong dự án cần có cấu trúc như sau:

### Bước 1: Định nghĩa State Interface
Mọi Slice đều phải có các thuộc tính quản lý vòng đời của request:
```typescript
export interface FeatureState {
  items: EntityType[];
  loading: boolean;
  error: string | null;
  // Các thông tin bổ sung như phân trang
  totalElements: number;
  totalPages: number;
  pageNumber: number;
}
```

### Bước 2: Khai báo Async Thunks
Bọc các dịch vụ API qua `createAsyncThunk`. Định nghĩa rõ ràng kiểu dữ liệu trả về và kiểu dữ liệu payload:
```typescript
export const fetchItems = createAsyncThunk(
  'feature/fetchItems',
  async ({ keyword, page, size }: { keyword: string; page: number; size: number }, thunkAPI) => {
    try {
      return await getItemsService(keyword, page, size);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Có lỗi xảy ra';
      return thunkAPI.rejectWithValue(message);
    }
  }
);
```

### Bước 3: Tạo Slice với `extraReducers`
```typescript
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

const initialState: FeatureState = {
  items: [],
  loading: false,
  error: null,
  totalElements: 0,
  totalPages: 0,
  pageNumber: 0,
};

const featureSlice = createSlice({
  name: 'feature',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Xử lý fetch items
      .addCase(fetchItems.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchItems.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.totalPages = action.payload.totalPages;
        state.pageNumber = action.payload.pageNumber;
      })
      .addCase(fetchItems.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearError } = featureSlice.actions;
export default featureSlice.reducer;
```

---

## 3. Quy trình Tích hợp và Sử dụng

### 1. Đăng ký Slice vào Redux Store
Mọi Slice mới tạo ra bắt buộc phải được đăng ký ngay trong tệp [store.ts](file:///home/tran-minh-quang/Workspace/E-commerce/app/src/store/store.ts):
```typescript
import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@/features/auth/authSlice';
import featureReducer from '@/features/feature/featureSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    feature: featureReducer, // Đăng ký tại đây
  },
});
```

### 2. Sử dụng trong Component
Sử dụng các hook custom `useAppDispatch` và `useAppSelector` từ `@/store/hooks` thay vì các hook mặc định của React-Redux:
```tsx
import React, { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { fetchItems } from '../featureSlice';

export const FeaturePage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { items, loading, error } = useAppSelector((state) => state.feature);

  useEffect(() => {
    dispatch(fetchItems({ keyword: '', page: 0, size: 10 }));
  }, [dispatch]);

  if (loading) return <div>Đang tải...</div>;
  if (error) return <div>Lỗi: {error}</div>;

  return (
    <ul>
      {items.map((item) => (
        <li key={item.id}>{item.name}</li>
      ))}
    </ul>
  );
};
```
