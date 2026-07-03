import React, { useEffect, useState, useCallback } from 'react';
import { Typography, Card, Input, Button, Table, Tag, Popconfirm, Space, Skeleton, message } from 'antd';
import { Search, Plus, Edit, Trash2 } from 'lucide-react';
import { getCategories, deleteCategory } from '../catalogService';
import { Category } from '../models/Category';
import { CategoryModal } from '../components/CategoryModal';
import { useDebounce } from '@/hooks/useDebounce'; // We will create this helper hook

const { Title, Paragraph } = Typography;

export const CategoryManagementPage: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const debouncedSearch = useDebounce(searchKeyword, 500);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  // Modal states
  const [modalVisible, setModalVisible] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);

  const fetchCategories = useCallback(async (keyword: string, pageNum: number, size: number) => {
    setLoading(true);
    try {
      // Backend is 0-based page index, AntD Table is 1-based page index
      const data = await getCategories(keyword, pageNum - 1, size);
      // Filter out deleted items (or handle deleted flag)
      setCategories(data.content.filter(cat => !cat.isDeleted));
      setTotalElements(data.totalElements);
    } catch (error) {
      message.error('Không thể tải danh sách danh mục sản phẩm.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchCategories(debouncedSearch, currentPage, pageSize);
  }, [debouncedSearch, currentPage, pageSize, fetchCategories]);

  // Handle Search Input Change
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyword(e.target.value);
    setCurrentPage(1); // Reset to page 1 on new search
  };

  // Handle Delete Action
  const handleDelete = async (id: string) => {
    try {
      await deleteCategory(id);
      message.success('Xóa danh mục thành công!');
      // Reload current page
      fetchCategories(debouncedSearch, currentPage, pageSize);
    } catch (error: any) {
      const errMsg = error.response?.data?.message || 'Có lỗi xảy ra khi xóa danh mục!';
      message.error(`Lỗi: ${errMsg}`);
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
    fetchCategories(debouncedSearch, currentPage, pageSize);
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
      dataIndex: 'isActive',
      key: 'isActive',
      width: 150,
      render: (isActive: boolean) => (
        <Tag color={isActive ? 'success' : 'error'} className="rounded-full px-3">
          {isActive ? 'Hoạt động' : 'Tạm khóa'}
        </Tag>
      ),
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 200,
      render: (date: string) => new Date(date).toLocaleString('vi-VN'),
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
        <div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-6">
          {/* Search bar */}
          <Input
            prefix={<Search size={16} className="text-slate-400 mr-2" />}
            placeholder="Tìm kiếm danh mục theo tên hoặc mô tả..."
            value={searchKeyword}
            onChange={handleSearch}
            className="w-full sm:max-w-md h-10 rounded-xl"
            allowClear
          />

          {/* Add Category button */}
          <Button
            type="primary"
            icon={<Plus size={16} className="mr-1" />}
            onClick={openCreateModal}
            className="h-10 rounded-xl flex items-center justify-center font-semibold"
          >
            Thêm danh mục
          </Button>
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
            pagination={{
              current: currentPage,
              pageSize: pageSize,
              total: totalElements,
              showSizeChanger: true,
              pageSizeOptions: ['10', '20', '50'],
              onChange: (page, size) => {
                setCurrentPage(page);
                setPageSize(size);
              },
              className: 'pt-4',
            }}
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
