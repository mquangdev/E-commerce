import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ConfigProvider, App as AntdApp } from 'antd';
import { store } from './store/store';
import LoginPage from './features/auth/pages/LoginPage';
import RegisterPage from './features/auth/pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import ProtectedRoute from './components/Common/ProtectedRoute';
import { AdminLayout } from './components/Layout/AdminLayout';
import { CategoryManagementPage } from './features/catalog/pages/CategoryManagementPage';
import { ProductManagementPage } from './features/catalog/pages/ProductManagementPage';

function App() {
  return (
    <Provider store={store}>
      <ConfigProvider
        theme={{
          token: {
            colorPrimary: '#1677ff',
            colorSuccess: '#52c41a',
            colorWarning: '#faad14',
            colorError: '#ff4d4f',
            borderRadius: 8,
            fontFamily: 'Inter, sans-serif',
          },
          components: {
            Button: {
              controlHeight: 40,
              fontWeight: 500,
            },
            Input: {
              controlHeight: 40,
            },
          },
        }}
      >
        <AntdApp>
          <Router>
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              {/* Protected Routes under AdminLayout */}
              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <AdminLayout />
                  </ProtectedRoute>
                }
              >
                {/* Nested routes inside AdminLayout Outlet */}
                <Route index element={<DashboardPage />} />
                <Route path="admin/categories" element={<CategoryManagementPage />} />
                <Route path="admin/products" element={<ProductManagementPage />} />
              </Route>

              {/* Fallback redirection */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Router>
        </AntdApp>
      </ConfigProvider>
    </Provider>
  );
}

export default App;
