import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { WalletRequest, getWalletByUserId, depositWallet } from './paymentService';

export interface WalletState {
  balance: number | null;
  loading: boolean;
  error: string | null;
}

const initialState: WalletState = {
  balance: null,
  loading: false,
  error: null,
};

export const fetchWalletBalance = createAsyncThunk(
  'wallet/fetchBalance',
  async (userId: string, thunkAPI) => {
    try {
      const data = await getWalletByUserId(userId);
      return data.balance;
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể tải số dư ví.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const depositWalletThunk = createAsyncThunk(
  'wallet/deposit',
  async (payload: WalletRequest, thunkAPI) => {
    try {
      const data = await depositWallet(payload);
      return data.balance;
    } catch (error: any) {
      const message = error.response?.data?.message || 'Không thể nạp tiền vào ví.';
      return thunkAPI.rejectWithValue(message);
    }
  }
);

const walletSlice = createSlice({
  name: 'wallet',
  initialState,
  reducers: {
    clearWallet: (state) => {
      state.balance = null;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Wallet Balance
      .addCase(fetchWalletBalance.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchWalletBalance.fulfilled, (state, action: PayloadAction<number>) => {
        state.loading = false;
        state.balance = action.payload;
        state.error = null;
      })
      .addCase(fetchWalletBalance.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      // Deposit Wallet
      .addCase(depositWalletThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(depositWalletThunk.fulfilled, (state, action: PayloadAction<number>) => {
        state.loading = false;
        state.balance = action.payload;
        state.error = null;
      })
      .addCase(depositWalletThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearWallet } = walletSlice.actions;
export default walletSlice.reducer;
