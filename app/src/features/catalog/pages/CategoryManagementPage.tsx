import React, { useEffect, useState, useCallback } from 'react';
import { Typography, Card, Input, Button, Table, Pagination, Tag, Popconfirm, Space, Skeleton, message } from 'antd';
import { Search, Plus, Edit, Trash2, RotateCw } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { fetchCategories, deleteCategoryThunk } from '../catalogSlice';
import { Category } from '../models/Category';
import { CategoryModal } from '../components/CategoryModal';
import { useDebounce } from '@/hooks/useDebounce';

const { Title, Paragraph } = Typography;

export const CategoryManagementPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const {
    categories,
    categoriesLoading: loading,
    categoriesTotal: totalElements,
  } = useAppSelector((state) => state.catalog);

  const [searchKeyword, setSearchKeyword] = useState('');
  const debouncedSearch = useDebounce(searchKeyword, 500);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // Modal states
  const [modalVisible, setModalVisible] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);

  const loadCategories = useCallback((keyword: string, pageNum: number, size: number) => {
    dispatch(fetchCategories({ keyword, page: pageNum - 1, size }));
  }, [dispatch]);

  useEffect(() => {
    loadCategories(debouncedSearch, currentPage, pageSize);
  }, [debouncedSearch, currentPage, pageSize, loadCategories]);

  // Handle Search Input Change
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyword(e.target.value);
    setCurrentPage(1); // Reset to page 1 on new search
  };

  // Handle Delete Action
  const handleDelete = async (id: string) => {
    try {
      await dispatch(deleteCategoryThunk(id)).unwrap();
      message.success('Xóa danh mục thành công!');
      // Reload current page
      loadCategories(debouncedSearch, currentPage, pageSize);
    } catch (error: any) {
      message.error(`Lỗi: ${error}`);
    }
  };

  const openCreateModal = () => {
    setEditingCategory(null);
    setModalVisible(true);
  };

  const openEditModal = (category: Category) => {
    setEditingCategory(category);
    setModalVisible(true);
  };

  const handleModalSuccess = () => {
    setModalVisible(false);
    // loadCategories(debouncedSearch, currentPage, pageSize);
  };

  const columns = [
    {
      title: 'Tên danh mục',
      dataIndex: 'name',
      key: 'name',
      className: 'font-semibold text-slate-800',
    },
    {
      title: 'Mô tả',
      dataIndex: 'description',
      key: 'description',
      render: (text: string) => text || <span className="text-slate-400 italic">Chưa có mô tả</span>,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'active',
      key: 'active',
      width: 150,
      render: (active: boolean) => (
        <Tag color={active ? 'success' : 'error'} className="rounded-full px-3">
          {active ? 'Hoạt động' : 'Ngừng hoạt động'}
        </Tag>
      ),
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 200,
      render: (date: string) => {
        if (!date) return '';
        const parts = date.split('-');
        if (parts.length === 3) {
          return `${parts[2]}/${parts[1]}/${parts[0]}`;
        }
        return new Date(date).toLocaleDateString('vi-VN');
      },
    },
    {
      title: 'Hành động',
      key: 'actions',
      width: 150,
      render: (_: any, record: Category) => (
        <Space size="middle">
          <Button
            type="text"
            icon={<Edit size={16} className="text-blue-600" />}
            onClick={() => openEditModal(record)}
            className="flex items-center justify-center hover:bg-blue-50"
          />
          <Popconfirm
            title="Xóa danh mục"
            description="Bạn có chắc chắn muốn xóa danh mục này? Hành động này không thể hoàn tác."
            onConfirm={() => handleDelete(record.id)}
            okText="Xóa"
            cancelText="Hủy"
            okButtonProps={{ danger: true }}
          >
            <Button
              type="text"
              danger
              icon={<Trash2 size={16} />}
              className="flex items-center justify-center hover:bg-red-50"
            />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      {/* Title & Description */}
      <div>
        <Title level={2} style={{ margin: 0, fontWeight: 700 }} className="text-slate-800">
          Quản lý Danh mục
        </Title>
        <Paragraph className="text-slate-500 mt-1">
          Xem, thêm mới, cập nhật và khóa các danh mục phân loại sản phẩm trong hệ thống cửa hàng.
        </Paragraph>
      </div>

      {/* Main Controls Card */}
      <Card className="shadow-sm border-slate-200/80 rounded-2xl" bodyStyle={{ padding: '1.5rem' }}>
        {/* Dòng 1: Tìm kiếm & Phân trang */}
        <div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-4">
          {/* Search bar */}
          <Input
            prefix={<Search size={16} className="text-slate-400 mr-2" />}
            placeholder="Tìm kiếm danh mục theo tên hoặc mô tả..."
            value={searchKeyword}
            onChange={handleSearch}
            className="w-full sm:max-w-md h-10 rounded-xl"
            allowClear
          />

          <Pagination
            current={currentPage}
            pageSize={pageSize}
            total={totalElements}
            showSizeChanger
            pageSizeOptions={['10', '20', '50']}
            showTotal={(total) => `Tổng số: ${total} danh mục`}
            onChange={(page, size) => {
              setCurrentPage(page);
              setPageSize(size);
            }}
            size="small"
            className="flex items-center justify-end text-slate-600"
          />
        </div>

        {/* Dòng 2: Cụm nút hành động - Căn trái */}
        <div className="flex justify-start items-center gap-2 mb-6">
          <Button
            icon={<RotateCw size={16} />}
            onClick={() => {
              loadCategories(debouncedSearch, currentPage, pageSize);
              message.success('Đã làm mới danh mục sản phẩm!');
            }}
            loading={loading}
            className="h-10 w-10 rounded-xl flex items-center justify-center text-slate-500 hover:text-slate-700 border-slate-200 transition-all duration-300"
            title="Tải lại danh sách"
          />
          <Button
            type="primary"
            icon={<Plus size={16} />}
            onClick={openCreateModal}
            className="h-10 w-10 rounded-xl flex items-center justify-center"
            title="Thêm danh mục"
          />
        </div>

        {/* Categories Table or Loading Skeleton */}
        {loading && categories.length === 0 ? (
          <div className="space-y-4 py-4">
            <Skeleton active paragraph={{ rows: 8 }} />
          </div>
        ) : (
          <Table
            dataSource={categories}
            columns={columns}
            rowKey="id"
            loading={loading}
            pagination={false}
            className="border-slate-100 rounded-xl overflow-hidden"
          />
        )}
      </Card>

      {/* Category Create/Edit Modal */}
      <CategoryModal
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSuccess={handleModalSuccess}
        editingCategory={editingCategory}
      />
    </div>
  );
};
