import { useState } from 'react';
import { Button, Card, Space, Badge, Tag, message } from 'antd';
import { ShoppingCart, ShieldAlert, Cpu, Palette, Settings, ExternalLink, LogOut, User } from 'lucide-react';
import { useAppDispatch } from '@/store/hooks';
import { logoutUser } from '@/features/auth/authSlice';
import { useNavigate } from 'react-router-dom';

function DashboardPage() {
  const [count, setCount] = useState(0);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const handleLogout = () => {
    dispatch(logoutUser());
    message.success('Đăng xuất thành công!');
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 flex flex-col">
      {/* Header */}
      <header className="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-slate-200 px-6 py-4 flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <div className="bg-blue-600 p-2 rounded-lg text-white">
            <ShoppingCart size={20} />
          </div>
          <span className="text-xl font-bold tracking-tight text-slate-900">E-Commerce Portal</span>
          <Tag color="blue" className="ml-2 font-mono">v1.0.0-beta</Tag>
        </div>
        <div className="flex items-center space-x-6">
          <a href="#docs" className="text-sm font-medium text-slate-600 hover:text-blue-600 transition-colors">Tài liệu</a>
          <a href="#services" className="text-sm font-medium text-slate-600 hover:text-blue-600 transition-colors">Dịch vụ</a>
          
          <Space size="middle" className="border-l border-slate-200 pl-6">
            <div className="flex items-center space-x-2 text-slate-700 bg-slate-100 px-3 py-1.5 rounded-full text-sm font-medium">
              <User size={16} className="text-slate-500" />
              <span>Khách hàng</span>
            </div>
            <Button 
              type="text" 
              danger 
              icon={<LogOut size={16} />} 
              onClick={handleLogout}
              className="flex items-center"
            >
              Đăng xuất
            </Button>
          </Space>
        </div>
      </header>

      {/* Hero Section */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-6 md:p-8 space-y-8">
        <section className="bg-gradient-to-r from-blue-600 via-indigo-600 to-indigo-700 rounded-3xl p-8 md:p-12 text-white shadow-xl relative overflow-hidden">
          <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_right,rgba(255,255,255,0.15),transparent_50%)]" />
          <div className="max-w-2xl space-y-4 relative z-10">
            <span className="bg-white/20 text-white text-xs font-semibold px-3 py-1 rounded-full uppercase tracking-wider">
              Khung Dự Án Đã Sẵn Sàng
            </span>
            <h1 className="text-3xl md:text-5xl font-extrabold tracking-tight">
              Hệ Thống Quản Trị & Bán Hàng E-Commerce
            </h1>
            <p className="text-blue-100 text-base md:text-lg leading-relaxed">
              Bạn đang ở trong trang quản trị bảo mật (Dashboard). Mọi request gọi API đến các dịch vụ Backend bây giờ đã được gắn kèm mã token truy cập của bạn một cách tự động.
            </p>
            <div className="pt-2 flex flex-wrap gap-3">
              <Button type="default" ghost size="large" icon={<ExternalLink size={16} />} className="hover:!text-white hover:!border-white">
                Xem tài liệu API
              </Button>
              <Button type="primary" size="large" style={{ backgroundColor: '#ffffff', color: '#4f46e5' }}>
                Mua sắm ngay
              </Button>
            </div>
          </div>
        </section>

        {/* Feature Grid */}
        <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card
            className="shadow-sm hover:shadow-md transition-shadow border-slate-200/80"
            title={
              <div className="flex items-center space-x-2 text-indigo-600">
                <Cpu size={18} />
                <span className="font-semibold text-slate-800">State Management</span>
              </div>
            }
          >
            <div className="space-y-4">
              <p className="text-slate-600 text-sm">
                Đã cấu hình Redux Toolkit làm cổng quản lý state tập trung cho toàn ứng dụng.
              </p>
              <div className="flex items-center justify-between bg-slate-50 p-3 rounded-lg border border-slate-100">
                <span className="text-xs font-mono text-slate-500">React Local State Test:</span>
                <Space>
                  <Badge count={count} color="#1677ff">
                    <span className="mr-2 text-sm font-semibold">Bộ đếm:</span>
                  </Badge>
                  <Button size="small" type="primary" onClick={() => setCount(count + 1)}>+</Button>
                  <Button size="small" onClick={() => setCount(0)}>Reset</Button>
                </Space>
              </div>
            </div>
          </Card>

          <Card
            className="shadow-sm hover:shadow-md transition-shadow border-slate-200/80"
            title={
              <div className="flex items-center space-x-2 text-blue-600">
                <Palette size={18} />
                <span className="font-semibold text-slate-800">Styling & UI Kit</span>
              </div>
            }
          >
            <p className="text-slate-600 text-sm mb-4">
              Tích hợp mượt mà giữa **Tailwind CSS v4** (CSS-first) và **Ant Design** (Component-first) cho phép tùy biến nhanh chóng.
            </p>
            <div className="flex flex-wrap gap-2">
              <span className="text-xs bg-slate-100 px-2 py-1 rounded text-slate-600 font-mono">Tailwind v4</span>
              <span className="text-xs bg-blue-50 px-2 py-1 rounded text-blue-600 font-mono">AntD v5</span>
              <span className="text-xs bg-indigo-50 px-2 py-1 rounded text-indigo-600 font-mono">Vite 6</span>
              <span className="text-xs bg-emerald-50 px-2 py-1 rounded text-emerald-600 font-mono">TypeScript</span>
            </div>
          </Card>

          <Card
            className="shadow-sm hover:shadow-md transition-shadow border-slate-200/80"
            title={
              <div className="flex items-center space-x-2 text-rose-500">
                <Settings size={18} />
                <span className="font-semibold text-slate-800">API Client (Axios)</span>
              </div>
            }
          >
            <p className="text-slate-600 text-sm mb-4">
              Axios instance được thiết lập sẵn tại `/services/api.ts` hỗ trợ tự động đính kèm JWT Bearer Token trong Header.
            </p>
            <div className="bg-slate-50 p-3 rounded-lg border border-slate-100 text-xs font-mono text-slate-500 space-y-1">
              <div>baseURL: <span className="text-blue-600">"/api"</span></div>
              <div>timeout: <span className="text-amber-600">10000ms</span></div>
              <div>authHeader: <span className="text-emerald-600">"Authorization: Bearer *"</span></div>
            </div>
          </Card>
        </section>

        {/* Configuration Checklist */}
        <section className="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-sm space-y-4">
          <h3 className="text-lg font-bold text-slate-900 flex items-center space-x-2">
            <ShieldAlert size={20} className="text-amber-500" />
            <span>Chức năng bảo mật đang hoạt động</span>
          </h3>
          <p className="text-sm text-slate-600">
            Hệ thống Auth Guard đã hoạt động chính xác. Bạn đã đăng nhập thành công và truy cập an toàn. Thử nhấn nút <strong>Đăng xuất</strong> ở góc trên bên phải để kiểm tra cơ chế bảo vệ phiên làm việc.
          </p>
        </section>
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-6 text-center text-sm text-slate-500 mt-auto">
        <p>© {new Date().getFullYear()} E-Commerce Microservices Workspace. Đã dựng khung thành công.</p>
      </footer>
    </div>
  );
}

export default DashboardPage;
