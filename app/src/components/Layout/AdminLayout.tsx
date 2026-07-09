import React, { useState } from 'react';
import { Layout, Menu, Dropdown, Space, Avatar, Button, Breadcrumb, Badge, theme } from 'antd';
import { Outlet, useNavigate, useLocation, Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { logoutUser } from '@/features/auth/authSlice';
import { toggleTheme } from '@/store/themeSlice';
import {
  LayoutDashboard,
  FolderTree,
  Package,
  Menu as MenuIcon,
  LogOut,
  User,
  ChevronLeft,
  ChevronRight,
  ShoppingCart,
  ShoppingBag,
  Bell,
  Sun,
  Moon
} from 'lucide-react';

const { Header, Sider, Content } = Layout;

export const AdminLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const darkMode = useAppSelector((state) => state.theme.darkMode);
  const { token } = theme.useToken();

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
    {
      key: '/admin/orders',
      icon: <ShoppingBag size={18} />,
      label: 'Quản lý đơn hàng',
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
      if (name === 'orders') title = 'Đơn hàng';

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
    <Layout className="h-screen overflow-hidden bg-slate-50 dark:bg-slate-950">
      {/* Sidebar Sider */}
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        width={260}
        theme={darkMode ? 'dark' : 'light'}
        className={`border-r shadow-sm transition-colors duration-200 ${darkMode ? 'border-slate-800' : 'border-slate-200/80'
          }`}
      >
        <div className="flex flex-col h-full justify-between pb-4">
          <div>
            {/* Brand Logo */}
            <div className={`h-16 flex items-center px-6 border-b ${darkMode ? 'border-slate-800' : 'border-slate-100'
              } space-x-3 overflow-hidden`}>
              <div className="bg-blue-600 p-2 rounded-xl text-white flex-shrink-0">
                <ShoppingCart size={20} />
              </div>
              {!collapsed && (
                <span className={`text-base font-extrabold tracking-tight transition-all duration-300 whitespace-nowrap ${darkMode ? 'text-slate-100' : 'text-slate-800'
                  }`}>
                  E-Commerce Admin
                </span>
              )}
            </div>

            {/* Navigation Menu */}
            <Menu
              mode="inline"
              theme={darkMode ? 'dark' : 'light'}
              selectedKeys={[location.pathname]}
              onClick={({ key }) => navigate(key)}
              items={menuItems}
              className="pt-4 border-none font-medium"
            />
          </div>

          {/* Collapse Button inside Sider Footer */}
          <div className="px-4">
            <Button
              type="text"
              icon={collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
              onClick={() => setCollapsed(!collapsed)}
              className="w-full flex items-center justify-center hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-slate-500 h-10"
            >
              {!collapsed && <span className="text-xs ml-2 font-medium">Thu gọn menu</span>}
            </Button>
          </div>
        </div>
      </Sider>

      {/* Main Container Layout */}
      <Layout className="h-screen flex flex-col overflow-hidden">
        {/* Header */}
        <Header
          style={{
            backgroundColor: token.colorBgContainer,
            borderBottom: `1px solid ${token.colorBorderSecondary}`,
            padding: '0 24px'
          }}
          className="backdrop-blur-md px-6 flex items-center justify-between h-16 shadow-sm flex-shrink-0 transition-colors duration-200"
        >
          <div className="flex items-center space-x-4">
            <Button
              type="text"
              icon={<MenuIcon size={20} />}
              onClick={() => setCollapsed(!collapsed)}
              className="md:hidden flex items-center justify-center text-slate-600 hover:text-blue-600"
            />
            {/* Breadcrumbs */}
            <Breadcrumb items={getBreadcrumbs()} className="hidden sm:block text-xs font-medium" />
          </div>

          {/* User Profile & Notifications */}
          <div className="flex items-center space-x-4 sm:space-x-6">
            {/* Theme Toggle Button */}
            <Button
              type="text"
              icon={darkMode ? <Sun size={18} className="text-amber-500" /> : <Moon size={18} className="text-slate-500" />}
              onClick={() => dispatch(toggleTheme())}
              className="flex items-center justify-center p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            />

            {/* Notifications Bell */}
            <Badge dot color="#ff4d4f" offset={[-2, 4]}>
              <Button
                type="text"
                icon={<Bell size={18} className="text-slate-500 hover:text-blue-600 transition-colors" />}
                className="flex items-center justify-center p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800"
              />
            </Badge>

            {/* Divider */}
            <div className="h-5 w-px bg-slate-200 dark:bg-slate-800" />

            {/* User Dropdown */}
            <Dropdown overlay={userMenu} placement="bottomRight" trigger={['click']}>
              <div className="flex items-center space-x-3 cursor-pointer p-1.5 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-xl transition-all duration-200 border border-transparent hover:border-slate-200/50 dark:hover:border-slate-700/50">
                <Avatar
                  size={32}
                  icon={<User size={16} />}
                  className="bg-blue-600 text-white flex items-center justify-center shadow-sm shadow-blue-500/20"
                />
                <div className="hidden md:flex flex-col text-left ml-2">
                  <span className="text-sm font-bold text-slate-800 dark:text-slate-200 leading-tight">Admin Portal</span>
                  <span className="text-xs text-slate-400 dark:text-slate-500 leading-none mt-0.5">Quản trị viên</span>
                </div>
              </div>
            </Dropdown>
          </div>
        </Header>

        {/* Content Area */}
        <Content className="flex-1 p-6 md:p-8 bg-slate-50 dark:bg-slate-900 overflow-y-auto transition-colors duration-200">
          <div className="max-w-7xl mx-auto space-y-6">
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};
