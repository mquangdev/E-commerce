import React, { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Button, Select, Space, Divider, Typography, message } from 'antd';
import { Plus, Trash2, ShoppingBag } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { createOrderThunk, resetCreateState } from '../orderSlice';
import { fetchProducts } from '@/features/catalog/catalogSlice';
import { OrderRequest } from '../models/Order';

const { Text, Title } = Typography;
const { Option } = Select;

interface OrderModalProps {
  visible: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const OrderModal: React.FC<OrderModalProps> = ({ visible, onClose, onSuccess }) => {
  const [form] = Form.useForm();
  const dispatch = useAppDispatch();
  const { createLoading, createSuccess, createError } = useAppSelector((state) => state.order);
  const { products } = useAppSelector((state) => state.catalog);
  const { user } = useAppSelector((state) => state.auth);

  // Watch items array to calculate total price dynamically
  const items = Form.useWatch('items', form) || [];

  useEffect(() => {
    if (visible) {
      dispatch(resetCreateState());
      form.resetFields();

      // Auto-fill email if available from logged-in user
      if (user) {
        const userEmail = (user as any).email || (user.sub?.includes('@') ? user.sub : `${user.sub}@gmail.com`);
        form.setFieldsValue({ email: userEmail });
      }

      // Load products to populate dropdown
      dispatch(fetchProducts({ keyword: '', page: 0, size: 1000 }));
    }
  }, [visible, dispatch, form, user]);

  useEffect(() => {
    if (createError) {
      message.error(createError);
    }
  }, [createError]);

  useEffect(() => {
    if (createSuccess) {
      message.info('Yêu cầu tạo mới đơn hàng đang được xử lý!');
      onSuccess();
      onClose();
    }
  }, [
    createSuccess
  ])

  const handleProductChange = (productId: string, index: number) => {
    const selectedProd = products.find((p) => p.id === productId);
    if (selectedProd) {
      const itemsFields = form.getFieldValue('items') || [];
      const updatedFields = [...itemsFields];
      updatedFields[index] = {
        ...updatedFields[index],
        unitPrice: selectedProd.price,
      };
      form.setFieldsValue({ items: updatedFields });
    }
  };

  const handleFinish = (values: any) => {
    const payload: OrderRequest = {
      shippingAddress: values.shippingAddress,
      email: values.email,
      items: values.items.map((item: any) => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
      })),
    };
    dispatch(createOrderThunk(payload));
  };

  // Calculate dynamic total price
  const calculateTotal = () => {
    return items.reduce((acc: number, curr: any) => {
      if (curr && curr.unitPrice && curr.quantity) {
        return acc + curr.unitPrice * curr.quantity;
      }
      return acc;
    }, 0);
  };

  return (
    <Modal
      title={
        <div className="flex items-center space-x-2 border-b pb-3 text-slate-800">
          <ShoppingBag size={18} className="text-blue-600" />
          <span className="font-bold text-lg">Tạo đơn hàng mới</span>
        </div>
      }
      open={visible}
      onCancel={onClose}
      footer={[
        <Button key="cancel" onClick={onClose} className="rounded-xl font-semibold h-10 hover:scale-95 transition-all">
          Hủy
        </Button>,
        <Button
          key="submit"
          type="primary"
          onClick={() => form.submit()}
          loading={createLoading}
          className="rounded-xl font-semibold h-10 hover:scale-95 transition-all"
        >
          Tạo đơn hàng
        </Button>,
      ]}
      width={720}
      className="rounded-2xl overflow-hidden"
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleFinish}
        initialValues={{ items: [{ productId: undefined, quantity: 1, unitPrice: 0 }] }}
        className="pt-4"
      >
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Form.Item
            name="email"
            label={<span className="font-semibold text-slate-750">Email khách hàng</span>}
            rules={[
              { required: true, message: 'Vui lòng nhập Email khách hàng' },
              { type: 'email', message: 'Email không đúng định dạng' },
            ]}
            className="md:col-span-2"
          >
            <Input placeholder="example@mail.com" className="rounded-xl h-10" />
          </Form.Item>
        </div>

        <Form.Item
          name="shippingAddress"
          label={<span className="font-semibold text-slate-750">Địa chỉ giao hàng</span>}
          rules={[{ required: true, message: 'Vui lòng nhập Địa chỉ giao hàng' }]}
        >
          <Input placeholder="Số nhà, đường, quận/huyện, tỉnh/thành phố" className="rounded-xl h-10" />
        </Form.Item>

        <Divider orientation="left" className="text-slate-500 m-0 pb-4">
          <span className="font-bold text-sm">Danh sách mặt hàng</span>
        </Divider>

        <Form.List name="items">
          {(fields, { add, remove }) => (
            <div className="space-y-3">
              {fields.length > 0 && (
                <div className="grid grid-cols-12 gap-3 mb-1 px-2 text-[10px] font-bold text-slate-400 dark:text-slate-500 uppercase tracking-wider">
                  <div className="col-span-6">Sản phẩm</div>
                  <div className="col-span-3">Đơn giá</div>
                  <div className="col-span-2">Số lượng</div>
                  <div className="col-span-1"></div>
                </div>
              )}

              {fields.map(({ key, name, ...restField }, index) => (
                <div
                  key={key}
                  className="grid grid-cols-12 gap-3 items-center bg-slate-50/40 dark:bg-slate-900/10 p-2.5 rounded-2xl border border-slate-100 dark:border-slate-800"
                >
                  <div className="col-span-6">
                    <Form.Item
                      {...restField}
                      name={[name, 'productId']}
                      rules={[{ required: true, message: 'Vui lòng chọn sản phẩm' }]}
                      style={{ margin: 0 }}
                    >
                      <Select
                        placeholder="Chọn sản phẩm"
                        onChange={(val) => handleProductChange(val, index)}
                        className="rounded-xl h-10 w-full"
                        showSearch
                        optionFilterProp="children"
                      >
                        {products.map((p) => (
                          <Option key={p.id} value={p.id}>
                            {p.name}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </div>

                  <div className="col-span-3">
                    <Form.Item
                      {...restField}
                      name={[name, 'unitPrice']}
                      rules={[{ required: true, message: 'Đơn giá trống' }]}
                      style={{ margin: 0 }}
                    >
                      <InputNumber
                        className="rounded-xl w-full"
                        formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                        parser={(value) => value!.replace(/\$\s?|(,*)/g, '')}
                        addonAfter="đ"
                        disabled
                      />
                    </Form.Item>
                  </div>

                  <div className="col-span-2">
                    <Form.Item
                      {...restField}
                      name={[name, 'quantity']}
                      rules={[{ required: true, message: 'Bắt buộc' }]}
                      style={{ margin: 0 }}
                    >
                      <InputNumber min={1} className="rounded-xl w-full" />
                    </Form.Item>
                  </div>

                  <div className="col-span-1 flex justify-center items-center">
                    {fields.length > 1 && (
                      <Button
                        type="text"
                        danger
                        icon={<Trash2 size={16} />}
                        onClick={() => remove(name)}
                        className="h-10 w-10 flex items-center justify-center rounded-xl hover:bg-red-50 dark:hover:bg-red-950/20"
                      />
                    )}
                  </div>
                </div>
              ))}

              <Form.Item style={{ margin: '12px 0 0 0' }}>
                <Button
                  type="dashed"
                  onClick={() => add({ productId: undefined, quantity: 1, unitPrice: 0 })}
                  block
                  icon={<Plus size={16} className="mr-1" />}
                  className="rounded-xl h-10 flex items-center justify-center font-medium border-dashed border-slate-300 text-slate-600 dark:text-slate-400 hover:text-blue-600 hover:border-blue-400 transition-all duration-200"
                >
                  Thêm sản phẩm
                </Button>
              </Form.Item>
            </div>
          )}
        </Form.List>

        <div className="bg-gradient-to-r from-blue-50/70 to-indigo-50/40 dark:from-slate-900/40 dark:to-slate-800/40 p-4 rounded-2xl flex justify-between items-center border border-slate-100 dark:border-slate-850 shadow-sm mt-5">
          <Text className="font-semibold text-slate-700 dark:text-slate-300">Tổng tiền tạm tính:</Text>
          <Title level={4} style={{ margin: 0 }} className="text-blue-600 dark:text-blue-400 font-extrabold tracking-tight">
            {calculateTotal().toLocaleString('vi-VN')} đ
          </Title>
        </div>
      </Form>
    </Modal>
  );
};
