import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Select, message, Tabs, Spin, Empty, Divider } from 'antd';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { Product, ProductCreateRequest, ProductUpdateRequest } from '../models/Product';
import { Category } from '../models/Category';
import { fetchCategories, createProductThunk, updateProductThunk, fetchProductInventory, clearCurrentInventory } from '../catalogSlice';

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
  const dispatch = useAppDispatch();
  const {
    categories,
    categoriesLoading,
    currentProductInventory,
    currentProductInventoryLoading,
    currentProductInventoryError
  } = useAppSelector((state) => state.catalog);

  // Load active categories and inventory if editing
  useEffect(() => {
    if (visible) {
      dispatch(fetchCategories({ keyword: '', page: 0, size: 100 }));
      if (editingProduct) {
        dispatch(fetchProductInventory(editingProduct.id));
      }
    }
    return () => {
      if (!visible) {
        dispatch(clearCurrentInventory());
      }
    };
  }, [visible, dispatch, editingProduct]);

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
        };
        await dispatch(updateProductThunk({ id: editingProduct.id, payload })).unwrap();
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
        await dispatch(createProductThunk(payload)).unwrap();
        message.success('Thêm sản phẩm mới thành công!');
      }
      onSuccess();
    } catch (error: any) {
      message.error(`Lỗi: ${error}`);
    } finally {
      setLoading(false);
    }
  };

  const renderGeneralInfo = () => (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleFinish}
      className="pt-4"
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
            loading={categoriesLoading}
            options={categories.filter(cat => cat.active).map((cat) => ({
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
          className={editingProduct ? 'sm:col-span-2' : ''}
        >
          <InputNumber
            className="w-full"
            placeholder="Nhập giá bán"
            formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
            parser={(value) => value!.replace(/\$\s?|(,*)/g, '')}
          />
        </Form.Item>

        {/* Stock Quantity - Only for Creation */}
        {editingProduct === null && (
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
        )}
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
  );

  const renderInventoryTab = () => {
    if (currentProductInventoryLoading) {
      return (
        <div className="py-12 flex justify-center items-center">
          <Spin size="large" tip="Đang tải thông tin tồn kho..." />
        </div>
      );
    }

    if (currentProductInventoryError) {
      return (
        <div className="py-6">
          <Empty description={<span className="text-red-500 font-semibold">{currentProductInventoryError}</span>} />
        </div>
      );
    }

    if (!currentProductInventory) {
      return (
        <div className="py-6">
          <Empty description="Không tìm thấy thông tin tồn kho của sản phẩm này." />
        </div>
      );
    }

    return (
      <div className="pt-4 space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="bg-emerald-50/50 dark:bg-emerald-950/10 border border-emerald-100/80 dark:border-emerald-900/35 rounded-2xl p-5 shadow-sm text-center">
            <span className="text-xs font-bold uppercase tracking-wider text-emerald-600 dark:text-emerald-450 block mb-1">
              Số lượng khả dụng
            </span>
            <span className="text-3xl font-extrabold text-emerald-700 dark:text-emerald-400">
              {currentProductInventory.availableQuantity.toLocaleString('vi-VN')}
            </span>
          </div>

          <div className="bg-amber-50/50 dark:bg-amber-950/10 border border-amber-100/80 dark:border-amber-900/35 rounded-2xl p-5 shadow-sm text-center">
            <span className="text-xs font-bold uppercase tracking-wider text-amber-600 dark:text-amber-450 block mb-1">
              Số lượng giữ chỗ
            </span>
            <span className="text-3xl font-extrabold text-amber-700 dark:text-amber-400">
              {currentProductInventory.reservedQuantity.toLocaleString('vi-VN')}
            </span>
          </div>
        </div>

        <div className="bg-slate-50/50 dark:bg-slate-900/30 p-5 rounded-2xl border border-slate-100/80 dark:border-slate-800 space-y-3.5 shadow-sm">
          <div className="flex justify-between items-baseline py-1 border-b border-slate-100/60 dark:border-slate-800/40">
            <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">Tổng lượng tồn nhập vào</span>
            <span className="text-sm font-bold text-slate-800 dark:text-slate-200">
              {editingProduct ? editingProduct.stockQuantity.toLocaleString('vi-VN') : 0}
            </span>
          </div>
          <div className="flex justify-between items-baseline py-1 border-b border-slate-100/60 dark:border-slate-800/40">
            <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">Số lượng đã tiêu thụ</span>
            <span className="text-sm font-bold text-slate-800 dark:text-slate-200">
              {editingProduct ? (editingProduct.stockQuantity - currentProductInventory.availableQuantity).toLocaleString('vi-VN') : 0}
            </span>
          </div>
          <div className="flex justify-between items-baseline py-1">
            <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">Cập nhật lần cuối</span>
            <span className="text-xs text-slate-700 dark:text-slate-300">
              {currentProductInventory.updatedAt ? new Date(currentProductInventory.updatedAt).toLocaleString('vi-VN') : ''}
            </span>
          </div>
        </div>
      </div>
    );
  };

  const tabItems = [
    {
      key: 'general',
      label: 'Thông tin chung',
      children: renderGeneralInfo(),
      forceRender: true,
    },
    {
      key: 'inventory',
      label: 'Thông tin tồn kho',
      children: renderInventoryTab(),
    },
  ];

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
      {editingProduct ? (
        <Tabs defaultActiveKey="general" items={tabItems} className="mt-2" />
      ) : (
        renderGeneralInfo()
      )}
    </Modal>
  );
};
