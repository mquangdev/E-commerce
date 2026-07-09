import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { loginApi, registerApi, LoginPayload, RegisterPayload } from './authService';
import axios from 'axios';
import { message } from 'antd';
import { decodeJwt, JwtPayload } from '@/utils/jwtHelper';

interface AuthState {
  token: string | null;
  user: JwtPayload | null;
  status: 'idle' | 'loading' | 'succeeded' | 'failed';
  error: string | null;
}

const token = localStorage.getItem('token');

const initialState: AuthState = {
  token,
  user: token ? decodeJwt(token) : null,
  status: 'idle',
  error: null,
};

// Thunk đăng nhập người dùng
export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async (payload: LoginPayload, { rejectWithValue }) => {
    try {
      const token = await loginApi(payload);
      return token;
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        return rejectWithValue(
          err.response?.data || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.'
        );
      }
      return rejectWithValue('Đã xảy ra lỗi kết nối hệ thống.');
    }
  }
);

// Thunk đăng ký người dùng
export const registerUser = createAsyncThunk(
  'auth/registerUser',
  async (payload: RegisterPayload, { rejectWithValue }) => {
    try {
      const userId = await registerApi(payload);
      return userId;
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        return rejectWithValue(
          err.response?.data?.message || 'Đăng ký thất bại. Vui lòng thử lại.'
        );
      }
      return rejectWithValue('Đã xảy ra lỗi kết nối hệ thống.');
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logoutUser: (state) => {
      state.token = null;
      state.user = null;
      state.status = 'idle';
      state.error = null;
      localStorage.removeItem('token');
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Login flow
      .addCase(loginUser.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action: PayloadAction<string>) => {
        state.status = 'succeeded';
        state.token = action.payload;
        state.user = decodeJwt(action.payload);
        state.error = null;
        localStorage.setItem('token', action.payload);
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload as string;
      })
      // Register flow
      .addCase(registerUser.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(registerUser.fulfilled, (state) => {
        state.status = 'succeeded';
        state.error = null;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload as string;
        message.error(action.payload as string);
      });
  },
});

export const { logoutUser, clearError } = authSlice.actions;
export default authSlice.reducer;
