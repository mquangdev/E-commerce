import { configureStore } from '@reduxjs/toolkit';
import authReducer from '@/features/auth/authSlice';
import themeReducer from './themeSlice';
import catalogReducer from '@/features/catalog/catalogSlice';
import orderReducer from '@/features/order/orderSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    theme: themeReducer,
    catalog: catalogReducer,
    order: orderReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export type AppStore = typeof store;
