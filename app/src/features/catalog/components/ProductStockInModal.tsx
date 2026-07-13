import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, message } from 'antd';
import { Product } from '../models/Product';
import { useAppDispatch } from '@/store/hooks';
import { receiveMoreInventoryThunk } from '../catalogSlice';

interface ProductStockInModalProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  product: Product | null;
}

export const ProductStockInModal: React.FC<ProductStockInModalProps> = ({
  visible,
  onCancel,
  onSuccess,
  product,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (visible) {
      form.resetFields();
      if (product) {
        form.setFieldsValue({
          sku: product.sku,
          name: product.name,
          quantity: 1,
        });
      }
    }
  }, [visible, product, form]);

  const handleFinish = async (values: any) => {
    if (!product) return;
    setLoading(true);
    try {
      await dispatch(
        receiveMoreInventoryThunk({
          id: product.id,
          payload: { addedInventory: values.quantity },
        })
      ).unwrap();
      
      message.success(`Nhập thêm ${values.quantity} sản phẩm "${product.name}" thành công!`);
      onSuccess();
    } catch (error: any) {
      message.error(`Lỗi: ${error}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="Nhập thêm sản phẩm"
      open={visible}
      onCancel={onCancel}
      onOk={() => form.submit()}
      confirmLoading={loading}
      okText="Nhập kho"
      cancelText="Hủy bỏ"
      destroyOnClose
      width={480}
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleFinish}
        className="pt-4"
      >
        {/* Product SKU - Readonly */}
        <Form.Item
          name="sku"
          label={<span className="font-semibold text-slate-700 text-xs">Mã SKU sản phẩm</span>}
        >
          <Input disabled />
        </Form.Item>

        {/* Product Name - Readonly */}
        <Form.Item
          name="name"
          label={<span className="font-semibold text-slate-700 text-xs">Tên sản phẩm</span>}
        >
          <Input disabled />
        </Form.Item>

        {/* Quantity to add */}
        <Form.Item
          name="quantity"
          label={<span className="font-semibold text-slate-700 text-xs">Số lượng nhập thêm</span>}
          rules={[
            { required: true, message: 'Vui lòng nhập số lượng nhập thêm!' },
            { type: 'number', min: 1, message: 'Số lượng nhập thêm phải tối thiểu là 1!' },
          ]}
        >
          <InputNumber
            className="w-full"
            placeholder="Nhập số lượng nhập thêm"
            min={1}
            precision={0}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};
