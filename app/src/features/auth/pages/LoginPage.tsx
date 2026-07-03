import React, { useEffect } from 'react';
import { Form, Input, Button, Card, Typography, message } from 'antd';
import { User, Lock, ShoppingCart } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { loginUser, clearError } from '../authSlice';
import { useNavigate, Link } from 'react-router-dom';

const { Title, Text } = Typography;

const LoginPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  
  const { status, error, token } = useAppSelector((state) => state.auth);

  // Khởi tạo/Lấy deviceId duy nhất cho thiết bị để gửi kèm request
  const getOrCreateDeviceId = (): string => {
    let deviceId = localStorage.getItem('deviceId');
    if (!deviceId) {
      // Hàm sinh UUID v4 đơn giản
      deviceId = 'device-' + Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
      localStorage.setItem('deviceId', deviceId);
    }
    return deviceId;
  };

  useEffect(() => {
    // Nếu đã có token (đã đăng nhập), tự động đưa về trang chủ
    if (token) {
      navigate('/', { replace: true });
    }
  }, [token, navigate]);

  useEffect(() => {
    // Hiển thị lỗi hệ thống hoặc API nếu có
    if (error) {
      message.error(error);
      dispatch(clearError());
    }
  }, [error, dispatch]);

  const onFinish = (values: any) => {
    const deviceId = getOrCreateDeviceId();
    dispatch(
      loginUser({
        username: values.username,
        password: values.password,
        deviceId: deviceId,
      })
    ).then((result) => {
      if (loginUser.fulfilled.match(result)) {
        message.success('Đăng nhập thành công!');
        navigate('/', { replace: true });
      }
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-950 flex flex-col justify-center items-center p-4">
      {/* Brand Logo Header */}
      <div className="flex items-center space-x-3 mb-8 animate-fade-in">
        <div className="bg-blue-600 p-2.5 rounded-xl text-white shadow-lg shadow-blue-500/20">
          <ShoppingCart size={24} />
        </div>
        <span className="text-2xl font-extrabold tracking-tight text-white">
          E-Commerce Portal
        </span>
      </div>

      {/* Login Card */}
      <Card
        className="w-full max-w-md shadow-2xl border-white/5 bg-white/95 backdrop-blur-md rounded-2xl overflow-hidden"
        bodyStyle={{ padding: '2rem' }}
      >
        <div className="text-center mb-6">
          <Title level={3} style={{ margin: 0, fontWeight: 700, color: '#0f172a' }}>
            Chào mừng trở lại!
          </Title>
          <Text className="text-slate-500 text-sm">
            Vui lòng đăng nhập tài khoản của bạn để tiếp tục
          </Text>
        </div>

        <Form
          form={form}
          name="login_form"
          layout="vertical"
          onFinish={onFinish}
          requiredMark={false}
          disabled={status === 'loading'}
        >
          {/* Username */}
          <Form.Item
            name="username"
            label={<span className="text-slate-700 font-medium text-xs">Tên tài khoản</span>}
            rules={[{ required: true, message: 'Vui lòng nhập tên tài khoản!' }]}
          >
            <Input
              prefix={<User className="text-slate-400 mr-2" size={16} />}
              placeholder="Nhập tên tài khoản"
              className="hover:border-blue-500 focus:border-blue-500"
            />
          </Form.Item>

          {/* Password */}
          <Form.Item
            name="password"
            label={<span className="text-slate-700 font-medium text-xs">Mật khẩu</span>}
            rules={[{ required: true, message: 'Vui lòng nhập mật khẩu!' }]}
          >
            <Input.Password
              prefix={<Lock className="text-slate-400 mr-2" size={16} />}
              placeholder="Nhập mật khẩu"
            />
          </Form.Item>

          {/* Submit Button */}
          <Form.Item className="mt-6 mb-4">
            <Button
              type="primary"
              htmlType="submit"
              loading={status === 'loading'}
              className="w-full h-10 font-semibold shadow-md shadow-blue-500/10 hover:translate-y-[-1px] transition-transform duration-200"
            >
              Đăng nhập
            </Button>
          </Form.Item>
        </Form>

        {/* Register Redirect */}
        <div className="text-center mt-4 text-sm text-slate-500">
          Chưa có tài khoản?{' '}
          <Link
            to="/register"
            className="text-blue-600 font-semibold hover:text-blue-500 transition-colors"
          >
            Đăng ký ngay
          </Link>
        </div>
      </Card>
    </div>
  );
};

export default LoginPage;
