import React, { useEffect, useState, useCallback } from 'react';
import { Typography, Card, Input, Button, Table, Tag, Popconfirm, Space, Skeleton, Avatar, message } from 'antd';
import { Search, Plus, Edit, Trash2, Image as ImageIcon, AlertTriangle } from 'lucide-react';
import { getProducts, deleteProduct } from '../catalogService';
import { Product } from '../models/Product';
import { ProductModal } from '../components/ProductModal';
import { useDebounce } from '@/hooks/useDebounce';

const { Title, Paragraph } = Typography;

export const ProductManagementPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const debouncedSearch = useDebounce(searchKeyword, 500);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  // Modal states
  const [modalVisible, setModalVisible] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);

  const fetchProducts = useCallback(async (keyword: string, pageNum: number, size: number) => {
    setLoading(true);
    try {
      // Backend is 0-based page index, AntD Table is 1-based page index
      const data = await getProducts(keyword, pageNum - 1, size);
      // Filter out deleted items (or handle deleted flag)
      setProducts(data.content.filter(prod => !prod.isDeleted));
      setTotalElements(data.totalElements);
    } catch (error) {
      message.error('Không thể tải danh sách sản phẩm.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProducts(debouncedSearch, currentPage, pageSize);
  }, [debouncedSearch, currentPage, pageSize, fetchProducts]);

  // Handle Search Input Change
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyword(e.target.value);
    setCurrentPage(1); // Reset to page 1 on new search
  };

  // Handle Delete Action
  const handleDelete = async (id: string) => {
    try {
      await deleteProduct(id);
      message.success('Xóa sản phẩm thành công!');
      fetchProducts(debouncedSearch, currentPage, pageSize);
    } catch (error: any) {
      const errMsg = error.response?.data?.message || 'Có lỗi xảy ra khi xóa sản phẩm!';
      message.error(`Lỗi: ${errMsg}`);
    }
  };

  const openCreateModal = () => {
    setEditingProduct(null);
    setModalVisible(true);
  };

  const openEditModal = (product: Product) => {
    setEditingProduct(product);
    setModalVisible(true);
  };

  const handleModalSuccess = () => {
    setModalVisible(false);
    fetchProducts(debouncedSearch, currentPage, pageSize);
  };

  // Helper to format currency (assuming VND but adaptable)
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(amount);
  };

  const columns = [
    {
      title: 'Hình ảnh',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      width: 100,
      render: (url: string) => (
        <Avatar
          src={url}
          shape="square"
          size={48}
          icon={<ImageIcon size={20} className="text-slate-400" />}
          className="bg-slate-100 border border-slate-200/50 rounded-lg flex items-center justify-center overflow-hidden"
        />
      ),
    },
    {
      title: 'Tên sản phẩm',
      dataIndex: 'name',
      key: 'name',
      className: 'font-semibold text-slate-800',
      render: (name: string, record: Product) => (
        <div className="flex flex-col">
          <span className="text-sm font-semibold">{name}</span>
          <span className="text-xs text-slate-400 font-mono mt-0.5">{record.sku}</span>
        </div>
      ),
    },
    {
      title: 'Danh mục',
      dataIndex: 'categoryName',
      key: 'categoryName',
      width: 180,
      render: (categoryName: string) => (
        <Tag color="blue" className="rounded-md border-blue-100 font-medium">
          {categoryName || 'Mặc định'}
        </Tag>
      ),
    },
    {
      title: 'Giá bán',
      dataIndex: 'price',
      key: 'price',
      width: 150,
      className: 'font-semibold text-blue-600',
      render: (price: number) => formatCurrency(price),
    },
    {
      title: 'Tồn kho',
      dataIndex: 'stockQuantity',
      key: 'stockQuantity',
      width: 160,
      render: (stock: number) => {
        if (stock === 0) {
          return (
            <Tag color="error" icon={<AlertTriangle size={12} className="inline mr-1" />} className="rounded-full px-3">
              Hết hàng
            </Tag>
          );
        }
        if (stock < 10) {
          return (
            <Tag color="warning" className="rounded-full px-3">
              Còn ít ({stock})
            </Tag>
          );
        }
        return (
          <Tag color="success" className="rounded-full px-3">
            {stock} sp
          </Tag>
        );
      },
    },
    {
      title: 'Hành động',
      key: 'actions',
      width: 130,
      render: (_: any, record: Product) => (
        <Space size="middle">
          <Button
            type="text"
            icon={<Edit size={16} className="text-blue-600" />}
            onClick={() => openEditModal(record)}
            className="flex items-center justify-center hover:bg-blue-50"
          />
          <Popconfirm
            title="Xóa sản phẩm"
            description="Bạn có chắc chắn muốn xóa sản phẩm này? Hành động này không thể hoàn tác."
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
          Quản lý Sản phẩm
        </Title>
        <Paragraph className="text-slate-500 mt-1">
          Xem, thêm mới, cập nhật giá bán, số lượng tồn kho và xóa sản phẩm khỏi kho bán hàng.
        </Paragraph>
      </div>

      {/* Main Controls Card */}
      <Card className="shadow-sm border-slate-200/80 rounded-2xl" bodyStyle={{ padding: '1.5rem' }}>
        <div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-6">
          {/* Search bar */}
          <Input
            prefix={<Search size={16} className="text-slate-400 mr-2" />}
            placeholder="Tìm kiếm sản phẩm theo tên, SKU hoặc mô tả..."
            value={searchKeyword}
            onChange={handleSearch}
            className="w-full sm:max-w-md h-10 rounded-xl"
            allowClear
          />

          {/* Add Product button */}
          <Button
            type="primary"
            icon={<Plus size={16} className="mr-1" />}
            onClick={openCreateModal}
            className="h-10 rounded-xl flex items-center justify-center font-semibold"
          >
            Thêm sản phẩm
          </Button>
        </div>

        {/* Products Table or Loading Skeleton */}
        {loading && products.length === 0 ? (
          <div className="space-y-4 py-4">
            <Skeleton active paragraph={{ rows: 8 }} />
          </div>
        ) : (
          <Table
            dataSource={products}
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

      {/* Product Create/Edit Modal */}
      <ProductModal
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSuccess={handleModalSuccess}
        editingProduct={editingProduct}
      />
    </div>
  );
};
