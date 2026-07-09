export interface JwtPayload {
  roles?: string[];
  fullName?: string;
  userId?: string;
  sub?: string;
  iat?: number;
  exp?: number;
}

export const decodeJwt = (token: string): JwtPayload | null => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Failed to decode JWT token', error);
    return null;
  }
};
