import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Select, message } from 'antd';
import { Product, ProductCreateRequest, ProductUpdateRequest } from '../models/Product';
import { Category } from '../models/Category';
import { createProduct, updateProduct, getCategories } from '../catalogService';

interface ProductModalProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  editingProduct: Product | null;
}

export const ProductModal: React.FC<ProductModalProps> = ({
  visible,
  onCancel,
  onSuccess,
  editingProduct,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loadingCategories, setLoadingCategories] = useState(false);

  // Load active categories for Select input
  useEffect(() => {
    const fetchCategories = async () => {
      setLoadingCategories(true);
      try {
        // Load up to 100 categories for the dropdown selector
        const data = await getCategories('', 0, 100);
        // Only show active categories
        setCategories(data.content.filter(cat => cat.isActive && !cat.isDeleted));
      } catch (error) {
        message.error('Không thể tải danh sách danh mục sản phẩm.');
      } finally {
        setLoadingCategories(false);
      }
    };

    if (visible) {
      fetchCategories();
    }
  }, [visible]);

  // Set form fields when editing
  useEffect(() => {
    if (visible) {
      if (editingProduct) {
        form.setFieldsValue({
          categoryId: editingProduct.categoryId,
          sku: editingProduct.sku,
          name: editingProduct.name,
          description: editingProduct.description,
          price: editingProduct.price,
          imageUrl: editingProduct.imageUrl,
          stockQuantity: editingProduct.stockQuantity,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({ stockQuantity: 0 });
      }
    }
  }, [visible, editingProduct, form]);

  const handleFinish = async (values: any) => {
    setLoading(true);
    try {
      if (editingProduct) {
        const payload: ProductUpdateRequest = {
          categoryId: values.categoryId,
          name: values.name,
          description: values.description,
          price: values.price,
          imageUrl: values.imageUrl,
          stockQuantity: values.stockQuantity,
        };
        await updateProduct(editingProduct.id, payload);
        message.success('Cập nhật sản phẩm thành công!');
      } else {
        const payload: ProductCreateRequest = {
          categoryId: values.categoryId,
          sku: values.sku,
          name: values.name,
          description: values.description,
          price: values.price,
          imageUrl: values.imageUrl,
          stockQuantity: values.stockQuantity,
        };
        await createProduct(payload);
        message.success('Thêm sản phẩm mới thành công!');
      }
      onSuccess();
    } catch (error: any) {
      const errMsg = error.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại!';
      message.error(`Lỗi: ${errMsg}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={editingProduct ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới'}
      open={visible}
      onCancel={onCancel}
      onOk={() => form.submit()}
      confirmLoading={loading}
      okText={editingProduct ? 'Cập nhật' : 'Thêm mới'}
      cancelText="Hủy bỏ"
      destroyOnClose
      width={600}
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleFinish}
        className="pt-4"
        requiredMark={false}
      >
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* SKU - Unique, cannot change when editing */}
          <Form.Item
            name="sku"
            label={<span className="font-semibold text-slate-700 text-xs">Mã SKU</span>}
            rules={[
              { required: true, message: 'Vui lòng nhập mã SKU!' },
              { max: 50, message: 'SKU không vượt quá 50 ký tự!' },
            ]}
          >
            <Input 
              placeholder="Nhập mã SKU độc nhất (VD: PHONE-IP15-128)" 
              disabled={editingProduct !== null}
            />
          </Form.Item>

          {/* Category Dropdown */}
          <Form.Item
            name="categoryId"
            label={<span className="font-semibold text-slate-700 text-xs">Danh mục sản phẩm</span>}
            rules={[{ required: true, message: 'Vui lòng chọn danh mục!' }]}
          >
            <Select
              placeholder="Chọn danh mục phân loại"
              loading={loadingCategories}
              options={categories.map((cat) => ({
                label: cat.name,
                value: cat.id,
              }))}
            />
          </Form.Item>
        </div>

        {/* Product Name */}
        <Form.Item
          name="name"
          label={<span className="font-semibold text-slate-700 text-xs">Tên sản phẩm</span>}
          rules={[
            { required: true, message: 'Vui lòng nhập tên sản phẩm!' },
            { max: 255, message: 'Tên sản phẩm không vượt quá 255 ký tự!' },
          ]}
        >
          <Input placeholder="Nhập tên đầy đủ của sản phẩm" />
        </Form.Item>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* Price */}
          <Form.Item
            name="price"
            label={<span className="font-semibold text-slate-700 text-xs">Giá sản phẩm (VNĐ)</span>}
            rules={[
              { required: true, message: 'Vui lòng nhập giá bán!' },
              { type: 'number', min: 0.01, message: 'Giá sản phẩm phải lớn hơn 0!' },
            ]}
          >
            <InputNumber
              className="w-full"
              placeholder="Nhập giá bán"
              formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
              parser={(value) => value!.replace(/\$\s?|(,*)/g, '')}
            />
          </Form.Item>

          {/* Stock Quantity */}
          <Form.Item
            name="stockQuantity"
            label={<span className="font-semibold text-slate-700 text-xs">Số lượng tồn kho</span>}
            rules={[
              { required: true, message: 'Vui lòng nhập số lượng!' },
              { type: 'number', min: 0, message: 'Số lượng tồn kho không được nhỏ hơn 0!' },
            ]}
          >
            <InputNumber className="w-full" placeholder="Nhập số lượng trong kho" min={0} />
          </Form.Item>
        </div>

        {/* Image URL */}
        <Form.Item
          name="imageUrl"
          label={<span className="font-semibold text-slate-700 text-xs">Đường dẫn hình ảnh (URL)</span>}
          rules={[{ max: 500, message: 'URL hình ảnh không vượt quá 500 ký tự!' }]}
        >
          <Input placeholder="Nhập link ảnh minh họa (URL)" />
        </Form.Item>

        {/* Description */}
        <Form.Item
          name="description"
          label={<span className="font-semibold text-slate-700 text-xs">Mô tả sản phẩm</span>}
        >
          <Input.TextArea 
            rows={4} 
            placeholder="Nhập mô tả chi tiết, tính năng nổi bật..." 
            className="resize-none"
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};
