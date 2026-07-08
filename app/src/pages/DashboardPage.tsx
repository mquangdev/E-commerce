import React from 'react';
import { Card, Button, Space, Badge, Tag, Progress, List, Avatar } from 'antd';
import { useNavigate } from 'react-router-dom';
import {
  TrendingUp,
  TrendingDown,
  DollarSign,
  ShoppingBag,
  Package,
  FolderTree,
  ArrowUpRight,
  Plus,
  Eye,
  Settings,
  AlertTriangle,
  UserCheck
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();

  // Mock data for dashboard stats
  const stats = [
    {
      title: 'Doanh thu tháng này',
      value: '152,430,000 đ',
      trend: '+12.5%',
      isPositive: true,
      icon: <DollarSign className="text-emerald-600 dark:text-emerald-400" size={24} />,
      color: 'from-emerald-50 to-emerald-100/50 dark:from-emerald-950/20 dark:to-emerald-900/10',
      borderColor: 'border-emerald-100 dark:border-emerald-900/30'
    },
    {
      title: 'Đơn hàng thành công',
      value: '1,240',
      trend: '+8.3%',
      isPositive: true,
      icon: <ShoppingBag className="text-blue-600 dark:text-blue-400" size={24} />,
      color: 'from-blue-50 to-blue-100/50 dark:from-blue-950/20 dark:to-blue-900/10',
      borderColor: 'border-blue-100 dark:border-blue-900/30'
    },
    {
      title: 'Sản phẩm hoạt động',
      value: '458',
      trend: '15 sp mới',
      isPositive: true,
      icon: <Package className="text-purple-600 dark:text-purple-400" size={24} />,
      color: 'from-purple-50 to-purple-100/50 dark:from-purple-950/20 dark:to-purple-900/10',
      borderColor: 'border-purple-100 dark:border-purple-900/30'
    },
    {
      title: 'Danh mục sản phẩm',
      value: '24',
      trend: 'Không thay đổi',
      isPositive: null,
      icon: <FolderTree className="text-amber-600 dark:text-amber-400" size={24} />,
      color: 'from-amber-50 to-amber-100/50 dark:from-amber-950/20 dark:to-amber-900/10',
      borderColor: 'border-amber-100 dark:border-amber-900/30'
    }
  ];

  // Mock top-selling products
  const topProducts = [
    {
      id: '1',
      name: 'iPhone 15 Pro Max 256GB',
      sku: 'IPHONE-15PM-256',
      category: 'Điện thoại',
      sales: 142,
      revenue: '42,600,000 đ',
      stock: 45,
      percentage: 85,
    },
    {
      id: '2',
      name: 'MacBook Pro 14 M3 Space Grey',
      sku: 'MAC-PRO-M3-14',
      category: 'Laptop',
      sales: 68,
      revenue: '39,990,000 đ',
      stock: 12,
      percentage: 62,
    },
    {
      id: '3',
      name: 'Sony WH-1000XM5 Wireless Headphones',
      sku: 'SONY-WH1000XM5-B',
      category: 'Phụ kiện',
      sales: 95,
      revenue: '8,490,000 đ',
      stock: 5,
      percentage: 78,
    },
    {
      id: '4',
      name: 'iPad Pro 11-inch M2 Wi-Fi',
      sku: 'IPAD-PRO-11-M2',
      category: 'Máy tính bảng',
      sales: 44,
      revenue: '23,190,000 đ',
      stock: 0,
      percentage: 45,
    }
  ];

  // Mock recent activities
  const recentActivities = [
    {
      id: 1,
      user: 'Nguyễn Văn A',
      action: 'đã thêm sản phẩm mới',
      target: 'Samsung Galaxy S24 Ultra',
      time: '10 phút trước',
    },
    {
      id: 2,
      user: 'Trần Thị B',
      action: 'đã cập nhật danh mục',
      target: 'Thời trang nam',
      time: '1 giờ trước',
    },
    {
      id: 3,
      user: 'Hệ thống',
      action: 'đã đồng bộ dữ liệu với',
      target: 'Elasticsearch Search Service',
      time: '2 giờ trước',
      isSystem: true,
    },
    {
      id: 4,
      user: 'Lê Hoàng C',
      action: 'đã xóa sản phẩm lỗi',
      target: 'Adapter sạc nhanh 15W',
      time: '5 giờ trước',
    }
  ];

  return (
    <div className="space-y-8 pb-8">
      {/* Welcome Banner */}
      <section className="bg-gradient-to-r from-blue-600 via-indigo-600 to-indigo-700 rounded-3xl p-8 md:p-10 text-white shadow-xl relative overflow-hidden">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_right,rgba(255,255,255,0.15),transparent_50%)]" />
        <div className="max-w-3xl space-y-4 relative z-10">
          <div className="flex items-center space-x-2 bg-white/10 px-3 py-1 rounded-full w-fit backdrop-blur-sm border border-white/10">
            <UserCheck size={14} />
            <span className="text-xs font-semibold uppercase tracking-wider">Phiên làm việc bảo mật</span>
          </div>
          <h1 className="text-3xl md:text-4xl font-extrabold tracking-tight">
            Chào mừng trở lại, Quản trị viên!
          </h1>
          <p className="text-blue-100 text-sm md:text-base leading-relaxed max-w-2xl">
            Hệ thống quản trị danh mục và kho hàng E-Commerce đã sẵn sàng. Mọi thay đổi dữ liệu của bạn sẽ được đồng bộ hóa tức thì sang dịch vụ tìm kiếm tập trung qua hàng đợi Kafka.
          </p>
          <div className="pt-2 flex flex-wrap gap-3">
            <Button
              type="primary"
              icon={<Plus size={16} />}
              onClick={() => navigate('/admin/products')}
              className="bg-white hover:bg-slate-100 text-indigo-600 border-none font-semibold shadow-md rounded-xl hover:scale-102 transition-all h-10"
            >
              Thêm sản phẩm mới
            </Button>
            <Button
              type="default"
              ghost
              icon={<Eye size={16} />}
              onClick={() => navigate('/admin/categories')}
              className="hover:!text-white hover:!border-white hover:bg-white/10 rounded-xl transition-all h-10"
            >
              Xem các danh mục
            </Button>
          </div>
        </div>
      </section>

      {/* Stats Cards Grid */}
      <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, i) => (
          <Card
            key={i}
            className={`shadow-sm border border-slate-200/80 dark:border-slate-800 rounded-2xl bg-gradient-to-br ${stat.color} transition-all duration-300 hover:shadow-md`}
            bodyStyle={{ padding: '1.25rem' }}
          >
            <div className="flex justify-between items-start">
              <div className="space-y-2">
                <span className="text-xs font-medium text-slate-500 dark:text-slate-400 block">{stat.title}</span>
                <span className="text-2xl font-bold text-slate-850 dark:text-slate-100 block tracking-tight">
                  {stat.value}
                </span>
              </div>
              <div className="p-3 bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-100 dark:border-slate-800 flex items-center justify-center">
                {stat.icon}
              </div>
            </div>
            
            <div className="mt-4 flex items-center space-x-1.5 text-xs">
              {stat.isPositive === true && (
                <div className="flex items-center text-emerald-600 dark:text-emerald-400 font-semibold bg-emerald-50 dark:bg-emerald-950/50 px-2 py-0.5 rounded-full">
                  <TrendingUp size={12} className="mr-0.5" />
                  <span>{stat.trend}</span>
                </div>
              )}
              {stat.isPositive === false && (
                <div className="flex items-center text-rose-600 dark:text-rose-400 font-semibold bg-rose-50 dark:bg-rose-950/50 px-2 py-0.5 rounded-full">
                  <TrendingDown size={12} className="mr-0.5" />
                  <span>{stat.trend}</span>
                </div>
              )}
              {stat.isPositive === null && (
                <div className="text-slate-500 dark:text-slate-400 font-medium bg-slate-100 dark:bg-slate-800 px-2 py-0.5 rounded-full">
                  <span>{stat.trend}</span>
                </div>
              )}
              {stat.isPositive !== null && (
                <span className="text-slate-400 dark:text-slate-500 font-medium">so với tháng trước</span>
              )}
            </div>
          </Card>
        ))}
      </section>

      {/* Main Details Grid */}
      <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left: Top Selling Products (2/3 width) */}
        <Card
          className="lg:col-span-2 shadow-sm border-slate-200/80 dark:border-slate-800 rounded-2xl"
          title={<span className="font-bold text-slate-850 dark:text-slate-100">Sản phẩm bán chạy nhất</span>}
          bodyStyle={{ padding: '1.25rem' }}
        >
          <div className="space-y-6">
            {topProducts.map((product) => (
              <div key={product.id} className="space-y-2">
                <div className="flex justify-between items-start">
                  <div>
                    <h4 className="text-sm font-semibold text-slate-800 dark:text-slate-200">{product.name}</h4>
                    <div className="flex items-center space-x-2 mt-0.5">
                      <span className="text-xs text-slate-400 font-mono">{product.sku}</span>
                      <span className="text-slate-300 dark:text-slate-700">•</span>
                      <span className="text-xs text-slate-400">{product.category}</span>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-sm font-bold text-slate-850 dark:text-slate-100 block">{product.revenue}</span>
                    <span className="text-xs text-slate-400 block">{product.sales} lượt bán</span>
                  </div>
                </div>
                
                <div className="flex items-center space-x-4">
                  <div className="flex-1">
                    <Progress
                      percent={product.percentage}
                      showInfo={false}
                      strokeColor={{
                        '0%': '#1677ff',
                        '100%': '#52c41a',
                      }}
                      className="m-0"
                    />
                  </div>
                  <div className="w-20 text-right">
                    {product.stock === 0 ? (
                      <Tag color="error" className="m-0 text-[10px] rounded-full">Hết hàng</Tag>
                    ) : product.stock < 10 ? (
                      <Tag color="warning" className="m-0 text-[10px] rounded-full">Còn ít ({product.stock})</Tag>
                    ) : (
                      <span className="text-xs text-slate-400 font-medium">Kho: {product.stock}</span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Right: Recent Log Activities (1/3 width) */}
        <Card
          className="shadow-sm border-slate-200/80 dark:border-slate-800 rounded-2xl"
          title={<span className="font-bold text-slate-850 dark:text-slate-100">Hoạt động gần đây</span>}
          bodyStyle={{ padding: '0.75rem 1.25rem' }}
        >
          <List
            itemLayout="horizontal"
            dataSource={recentActivities}
            renderItem={(item) => (
              <List.Item className="border-b border-slate-100 dark:border-slate-800/50 py-3 last:border-none">
                <List.Item.Meta
                  avatar={
                    <Avatar 
                      className={item.isSystem ? 'bg-indigo-100 text-indigo-600' : 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-350'}
                      size="small"
                      icon={item.isSystem ? <Settings size={12} /> : <Plus size={12} />}
                    />
                  }
                  title={
                    <span className="text-xs text-slate-600 dark:text-slate-400 block leading-tight">
                      <strong className="text-slate-800 dark:text-slate-200 font-semibold">{item.user}</strong>{' '}
                      {item.action}{' '}
                      <span className="text-blue-600 dark:text-blue-400 font-medium block sm:inline mt-0.5 sm:mt-0">
                        {item.target}
                      </span>
                    </span>
                  }
                  description={<span className="text-[10px] text-slate-400">{item.time}</span>}
                />
              </List.Item>
            )}
          />
        </Card>
      </section>

      {/* Stock Alert Summary Footer */}
      <section className="bg-white dark:bg-slate-950 rounded-2xl p-6 border border-slate-200/80 dark:border-slate-800 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-amber-50 dark:bg-amber-950/30 rounded-xl text-amber-500 flex items-center justify-center">
            <AlertTriangle size={20} />
          </div>
          <div>
            <h4 className="text-sm font-semibold text-slate-850 dark:text-slate-200">Cảnh báo tồn kho cửa hàng</h4>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
              Phát hiện <strong>3 sản phẩm</strong> sắp hết hàng trong kho. Vui lòng bổ sung số lượng kịp thời.
            </p>
          </div>
        </div>
        <Button
          type="primary"
          onClick={() => navigate('/admin/products')}
          className="rounded-xl font-semibold flex items-center justify-center h-9 text-xs"
        >
          Kiểm tra kho ngay
        </Button>
      </section>
    </div>
  );
};

export default DashboardPage;
