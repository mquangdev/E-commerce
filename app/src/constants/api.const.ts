export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8088';

export const AUTH_BASE_URL = `${API_BASE_URL}/auth`;
export const CATALOG_BASE_URL = `${API_BASE_URL}/catalog`;
export const ORDER_BASE_URL = `${API_BASE_URL}/order`;
export const PAYMENT_BASE_URL = `${API_BASE_URL}/payment`;
export const INVENTORY_BASE_URL = `${API_BASE_URL}/inventory`;
export const SEARCH_BASE_URL = `${API_BASE_URL}/search`;
