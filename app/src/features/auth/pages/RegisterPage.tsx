import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { Button, Card, Form, Input, Typography, message } from 'antd';
import { Lock, Mail, ShoppingCart, User, UserCheck } from 'lucide-react';
import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerUser } from '../authSlice';

const { Title, Text } = Typography;

const RegisterPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [form] = Form.useForm();

  const { status, token } = useAppSelector((state) => state.auth);

  useEffect(() => {
    // Nếu đã đăng nhập, chuyển về trang chủ
    if (token) {
      navigate('/', { replace: true });
    }
  }, [token, navigate]);

  const onFinish = (values: any) => {
    dispatch(
      registerUser({
        username: values.username,
        email: values.email,
        password: values.password,
        fullName: values.fullName,
      })
    ).then((result) => {
      if (registerUser.fulfilled.match(result)) {
        message.success('Đăng ký tài khoản thành công! Vui lòng đăng nhập.');
        navigate('/login');
      }
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-950 flex flex-col justify-center items-center p-4">
      {/* Brand Logo Header */}
      <div className="flex items-center space-x-3 mb-8">
        <div className="bg-blue-600 p-2.5 rounded-xl text-white shadow-lg shadow-blue-500/20">
          <ShoppingCart size={24} />
        </div>
        <span className="text-2xl font-extrabold tracking-tight text-white">
          E-Commerce Portal
        </span>
      </div>

      {/* Register Card */}
      <Card
        className="w-full max-w-md shadow-2xl border-white/5 bg-white/95 backdrop-blur-md rounded-2xl overflow-hidden"
        bodyStyle={{ padding: '2rem' }}
      >
        <div className="text-center mb-6">
          <Title level={3} style={{ margin: 0, fontWeight: 700, color: '#0f172a' }}>
            Tạo tài khoản mới
          </Title>
          <Text className="text-slate-500 text-sm">
            Đăng ký tài khoản để bắt đầu trải nghiệm mua sắm
          </Text>
        </div>

        <Form
          form={form}
          name="register_form"
          layout="vertical"
          onFinish={onFinish}
          requiredMark={false}
          disabled={status === 'loading'}
        >
          {/* Full Name */}
          <Form.Item
            name="fullName"
            label={<span className="text-slate-700 font-medium text-xs">Họ và tên</span>}
            rules={[{ required: true, message: 'Vui lòng nhập họ và tên!' }]}
          >
            <Input
              prefix={<UserCheck className="text-slate-400 mr-2" size={16} />}
              placeholder="Nhập họ và tên của bạn"
            />
          </Form.Item>

          {/* Username */}
          <Form.Item
            name="username"
            label={<span className="text-slate-700 font-medium text-xs">Tên tài khoản</span>}
            rules={[
              { required: true, message: 'Vui lòng nhập tên tài khoản!' },
              { min: 4, message: 'Tên tài khoản phải từ 4 ký tự trở lên!' }
            ]}
          >
            <Input
              prefix={<User className="text-slate-400 mr-2" size={16} />}
              placeholder="Nhập tên tài khoản mong muốn"
            />
          </Form.Item>

          {/* Email */}
          <Form.Item
            name="email"
            label={<span className="text-slate-700 font-medium text-xs">Email</span>}
            rules={[
              { required: true, message: 'Vui lòng nhập địa chỉ email!' },
              { type: 'email', message: 'Địa chỉ email không đúng định dạng!' }
            ]}
          >
            <Input
              prefix={<Mail className="text-slate-400 mr-2" size={16} />}
              placeholder="Nhập địa chỉ email"
            />
          </Form.Item>

          {/* Password */}
          <Form.Item
            name="password"
            label={<span className="text-slate-700 font-medium text-xs">Mật khẩu</span>}
            rules={[
              { required: true, message: 'Vui lòng nhập mật khẩu!' },
              { min: 6, message: 'Mật khẩu phải từ 6 ký tự trở lên!' }
            ]}
          >
            <Input.Password
              prefix={<Lock className="text-slate-400 mr-2" size={16} />}
              placeholder="Nhập mật khẩu bảo mật"
            />
          </Form.Item>

          {/* Confirm Password */}
          <Form.Item
            name="confirmPassword"
            label={<span className="text-slate-700 font-medium text-xs">Xác nhận mật khẩu</span>}
            dependencies={['password']}
            rules={[
              { required: true, message: 'Vui lòng xác nhận mật khẩu!' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Mật khẩu xác nhận không khớp!'));
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<Lock className="text-slate-400 mr-2" size={16} />}
              placeholder="Nhập lại mật khẩu để xác nhận"
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
              Đăng ký
            </Button>
          </Form.Item>
        </Form>

        {/* Login Redirect */}
        <div className="text-center mt-4 text-sm text-slate-500">
          Đã có tài khoản?{' '}
          <Link
            to="/login"
            className="text-blue-600 font-semibold hover:text-blue-500 transition-colors"
          >
            Đăng nhập ngay
          </Link>
        </div>
      </Card>
    </div>
  );
};

export default RegisterPage;
