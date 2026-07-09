import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { OrderResponse, OrderRequest } from './models/Order';
import { getOrders, getOrderById, createOrder } from './orderService';

export interface OrderState {
  orders: OrderResponse[];
  loading: boolean;
  error: string | null;
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;

  currentOrder: OrderResponse | null;
  currentOrderLoading: boolean;
  currentOrderError: string | null;

  createLoading: boolean;
  createError: string | null;
  createSuccess: boolean;
}

const initialState: OrderState = {
  orders: [],
  loading: false,
  error: null,
  totalElements: 0,
  totalPages: 0,
  pageNumber: 0,
  pageSize: 10,

  currentOrder: null,
  currentOrderLoading: false,
  currentOrderError: null,

  createLoading: false,
  createError: null,
  createSuccess: false,
};

// === ASYNC THUNKS ===

export const fetchOrders = createAsyncThunk(
  'order/fetchOrders',
  async ({ keyword, page, size }: { keyword: string; page: number; size: number }, thunkAPI) => {
    try {
      return await getOrders(keyword, page, size);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể tải danh sách đơn hàng.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const fetchOrderById = createAsyncThunk(
  'order/fetchOrderById',
  async (orderId: string, thunkAPI) => {
    try {
      return await getOrderById(orderId);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không tìm thấy chi tiết đơn hàng.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const createOrderThunk = createAsyncThunk(
  'order/createOrder',
  async (request: OrderRequest, thunkAPI) => {
    try {
      return await createOrder(request);
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể tạo đơn hàng.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

// === SLICE ===

const orderSlice = createSlice({
  name: 'order',
  initialState,
  reducers: {
    resetCreateState: (state) => {
      state.createLoading = false;
      state.createError = null;
      state.createSuccess = false;
    },
    clearError: (state) => {
      state.error = null;
      state.currentOrderError = null;
    },
    setCurrentOrder: (state, action: PayloadAction<OrderResponse | null>) => {
      state.currentOrder = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Orders
      .addCase(fetchOrders.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchOrders.fulfilled, (state, action) => {
        state.loading = false;
        state.orders = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.totalPages = action.payload.totalPages;
        state.pageNumber = action.payload.pageNumber;
        state.pageSize = action.payload.pageSize;
      })
      .addCase(fetchOrders.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      // Fetch Order By Id
      .addCase(fetchOrderById.pending, (state) => {
        state.currentOrderLoading = true;
        state.currentOrderError = null;
      })
      .addCase(fetchOrderById.fulfilled, (state, action) => {
        state.currentOrderLoading = false;
        state.currentOrder = action.payload;
      })
      .addCase(fetchOrderById.rejected, (state, action) => {
        state.currentOrderLoading = false;
        state.currentOrderError = action.payload as string;
      })

      // Create Order
      .addCase(createOrderThunk.pending, (state) => {
        state.createLoading = true;
        state.createError = null;
        state.createSuccess = false;
      })
      .addCase(createOrderThunk.fulfilled, (state) => {
        state.createLoading = false;
        state.createSuccess = true;
      })
      .addCase(createOrderThunk.rejected, (state, action) => {
        state.createLoading = false;
        state.createError = action.payload as string;
      });
  },
});

export const { resetCreateState, clearError, setCurrentOrder } = orderSlice.actions;
export default orderSlice.reducer;
