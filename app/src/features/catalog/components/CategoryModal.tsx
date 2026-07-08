import React, { useEffect } from 'react';
import { Modal, Form, Input, Switch, message } from 'antd';
import { useAppDispatch } from '@/store/hooks';
import { Category, CategoryCreateRequest, CategoryUpdateRequest } from '../models/Category';
import { createCategoryThunk, updateCategoryThunk } from '../catalogSlice';

interface CategoryModalProps {
  visible: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  editingCategory: Category | null;
}

export const CategoryModal: React.FC<CategoryModalProps> = ({
  visible,
  onCancel,
  onSuccess,
  editingCategory,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (visible) {
      if (editingCategory) {
        form.setFieldsValue({
          name: editingCategory.name,
          description: editingCategory.description,
          isActive: editingCategory.active,
        });
      } else {
        form.resetFields();
        form.setFieldsValue({ isActive: true });
      }
    }
  }, [visible, editingCategory, form]);

  const handleFinish = async (values: any) => {
    setLoading(true);
    try {
      if (editingCategory) {
        const payload: CategoryUpdateRequest = {
          name: values.name,
          description: values.description,
          isActive: values.isActive,
        };
        await dispatch(updateCategoryThunk({ id: editingCategory.id, payload })).unwrap();
        message.success('Cập nhật danh mục thành công!');
      } else {
        const payload: CategoryCreateRequest = {
          name: values.name,
          description: values.description,
          isActive: values.isActive,
        };
        await dispatch(createCategoryThunk(payload)).unwrap();
        message.success('Thêm danh mục mới thành công!');
      }
      onSuccess();
    } catch (error: any) {
      message.error(`Lỗi: ${error}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={editingCategory ? 'Chỉnh sửa danh mục' : 'Thêm danh mục mới'}
      open={visible}
      onCancel={onCancel}
      onOk={() => form.submit()}
      confirmLoading={loading}
      okText={editingCategory ? 'Cập nhật' : 'Thêm mới'}
      cancelText="Hủy bỏ"
      destroyOnClose
      width={500}
    >
      <Form
        form={form}
        layout="vertical"
        onFinish={handleFinish}
        className="pt-4"
      >
        <Form.Item
          name="name"
          label={<span className="font-semibold text-slate-700 text-xs">Tên danh mục</span>}
          rules={[
            { required: true, message: 'Vui lòng nhập tên danh mục!' },
            { max: 100, message: 'Tên danh mục không vượt quá 100 ký tự!' },
          ]}
        >
          <Input placeholder="Nhập tên danh mục (VD: Điện thoại, Laptop,...)" />
        </Form.Item>

        <Form.Item
          name="description"
          label={<span className="font-semibold text-slate-700 text-xs">Mô tả</span>}
          rules={[{ max: 255, message: 'Mô tả không vượt quá 255 ký tự!' }]}
        >
          <Input.TextArea 
            rows={4} 
            placeholder="Nhập mô tả tóm tắt cho danh mục này..." 
            className="resize-none"
          />
        </Form.Item>

        <Form.Item
          name="isActive"
          label={<span className="font-semibold text-slate-700 text-xs">Trạng thái hoạt động</span>}
          valuePropName="checked"
        >
          <Switch checkedChildren="Bật" unCheckedChildren="Tắt" />
        </Form.Item>
      </Form>
    </Modal>
  );
};
