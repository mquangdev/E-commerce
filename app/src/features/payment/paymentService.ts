import api from '@/services/api';
import { PAYMENT_BASE_URL } from '@/constants/api.const';

export interface WalletResponse {
  id: string;
  userId: string;
  balance: number;
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface WalletRequest {
  userId: string;
  amount: number;
}

export const getWalletByUserId = async (userId: string): Promise<WalletResponse> => {
  const response = await api.get<WalletResponse>(
    `${PAYMENT_BASE_URL}/api/v1/payments/wallets/${userId}`
  );
  return response.data;
};

export const depositWallet = async (payload: WalletRequest): Promise<WalletResponse> => {
  const response = await api.put<WalletResponse>(
    `${PAYMENT_BASE_URL}/api/v1/payments/wallets/deposit`,
    payload
  );
  return response.data;
};
