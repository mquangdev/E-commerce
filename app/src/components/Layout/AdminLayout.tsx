import React, { useState } from 'react';
import { Layout, Menu, Dropdown, Space, Avatar, Button, Breadcrumb } from 'antd';
import { Outlet, useNavigate, useLocation, Link } from 'react-router-dom';
import { useAppDispatch } from '@/store/hooks';
import { logoutUser } from '@/features/auth/authSlice';
import {
  LayoutDashboard,
  FolderTree,
  Package,
  Menu as MenuIcon,
  LogOut,
  User,
  ChevronLeft,
  ChevronRight,
  ShoppingCart
} from 'lucide-react';

const { Header, Sider, Content } = Layout;

export const AdminLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    dispatch(logoutUser());
    navigate('/login');
  };

  const menuItems = [
    {
      key: '/',
      icon: <LayoutDashboard size={18} />,
      label: 'Tổng quan (Dashboard)',
    },
    {
      key: '/admin/categories',
      icon: <FolderTree size={18} />,
      label: 'Quản lý danh mục',
    },
    {
      key: '/admin/products',
      icon: <Package size={18} />,
      label: 'Quản lý sản phẩm',
    },
  ];

  // Generate breadcrumbs from path
  const getBreadcrumbs = () => {
    const pathnames = location.pathname.split('/').filter((x) => x);
    const breadcrumbItems = [
      {
        title: <Link to="/">Home</Link>,
      },
    ];

    pathnames.forEach((name, index) => {
      const routeTo = `/${pathnames.slice(0, index + 1).join('/')}`;
      const isLast = index === pathnames.length - 1;
      
      let title = name.charAt(0).toUpperCase() + name.slice(1);
      if (name === 'admin') title = 'Quản trị';
      if (name === 'categories') title = 'Danh mục';
      if (name === 'products') title = 'Sản phẩm';

      breadcrumbItems.push({
        title: isLast ? <span>{title}</span> : <Link to={routeTo}>{title}</Link>,
      });
    });

    return breadcrumbItems;
  };

  const userMenu = (
    <Menu
      items={[
        {
          key: 'profile',
          icon: <User size={14} />,
          label: 'Thông tin tài khoản',
        },
        {
          type: 'divider',
        },
        {
          key: 'logout',
          icon: <LogOut size={14} className="text-red-500" />,
          label: <span className="text-red-500 font-medium">Đăng xuất</span>,
          onClick: handleLogout,
        },
      ]}
    />
  );

  return (
    <Layout className="min-h-screen bg-slate-50">
      {/* Sidebar Sider */}
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        width={260}
        theme="light"
        className="fixed left-0 top-0 bottom-0 z-30 border-r border-slate-200/80 shadow-sm"
        style={{
          height: '100vh',
        }}
      >
        {/* Brand Logo */}
        <div className="h-16 flex items-center px-6 border-b border-slate-100 space-x-3 overflow-hidden">
          <div className="bg-blue-600 p-2 rounded-xl text-white flex-shrink-0">
            <ShoppingCart size={20} />
          </div>
          {!collapsed && (
            <span className="text-base font-extrabold tracking-tight text-slate-800 transition-all duration-300 whitespace-nowrap">
              E-Commerce Admin
            </span>
          )}
        </div>

        {/* Navigation Menu */}
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          onClick={({ key }) => navigate(key)}
          items={menuItems}
          className="pt-4 border-none font-medium"
        />

        {/* Collapse Button inside Sider Footer */}
        <div className="absolute bottom-4 left-0 right-0 px-4 flex justify-center">
          <Button
            type="text"
            icon={collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
            onClick={() => setCollapsed(!collapsed)}
            className="w-full flex items-center justify-center hover:bg-slate-100 rounded-lg text-slate-500 h-10"
          >
            {!collapsed && <span className="text-xs ml-2 font-medium">Thu gọn menu</span>}
          </Button>
        </div>
      </Sider>

      {/* Main Container Layout */}
      <Layout 
        style={{ 
          marginLeft: collapsed ? 80 : 260, 
          transition: 'all 0.2s',
        }}
        className="min-h-screen flex flex-col"
      >
        {/* Header */}
        <Header className="sticky top-0 z-20 bg-white/80 backdrop-blur-md border-b border-slate-200/80 px-6 py-0 flex items-center justify-between h-16 shadow-sm">
          <div className="flex items-center space-x-4">
            <Button
              type="text"
              icon={<MenuIcon size={20} />}
              onClick={() => setCollapsed(!collapsed)}
              className="md:hidden flex items-center justify-center text-slate-600"
            />
            {/* Breadcrumbs */}
            <Breadcrumb items={getBreadcrumbs()} className="hidden sm:block text-xs font-medium" />
          </div>

          {/* User Profile Info */}
          <div className="flex items-center space-x-4">
            <Dropdown overlay={userMenu} placement="bottomRight" trigger={['click']}>
              <div className="flex items-center space-x-2 cursor-pointer p-1.5 hover:bg-slate-100 rounded-xl transition-colors duration-200">
                <Avatar 
                  size={32} 
                  icon={<User size={16} />}
                  className="bg-blue-100 text-blue-600 flex items-center justify-center"
                />
                <div className="hidden md:flex flex-col text-left mr-1">
                  <span className="text-sm font-semibold text-slate-800 leading-tight">Admin Portal</span>
                  <span className="text-xs text-slate-400 leading-none">Quản trị viên</span>
                </div>
              </div>
            </Dropdown>
          </div>
        </Header>

        {/* Content Area */}
        <Content className="flex-1 p-6 md:p-8 bg-slate-50 overflow-y-auto">
          <div className="max-w-7xl mx-auto space-y-6">
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};
