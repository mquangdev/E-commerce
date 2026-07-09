import React, { useEffect, useState, useCallback } from 'react';
import { Typography, Card, Input, Button, Table, Pagination, Tag, Popconfirm, Space, Skeleton, Avatar, message } from 'antd';
import { Search, Plus, Edit, Trash2, Image as ImageIcon, AlertTriangle, RotateCw, PackagePlus } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { fetchProducts, deleteProductThunk } from '../catalogSlice';
import { Product } from '../models/Product';
import { ProductModal } from '../components/ProductModal';
import { ProductStockInModal } from '../components/ProductStockInModal';
import { useDebounce } from '@/hooks/useDebounce';

const { Title, Paragraph } = Typography;

export const ProductManagementPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const {
    products,
    productsLoading: loading,
    productsTotal: totalElements,
  } = useAppSelector((state) => state.catalog);

  const [searchKeyword, setSearchKeyword] = useState('');
  const debouncedSearch = useDebounce(searchKeyword, 500);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // Modal states
  const [modalVisible, setModalVisible] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);

  // Stock-in modal states
  const [stockInVisible, setStockInVisible] = useState(false);
  const [stockInProduct, setStockInProduct] = useState<Product | null>(null);

  const loadProducts = useCallback((keyword: string, pageNum: number, size: number) => {
    dispatch(fetchProducts({ keyword, page: pageNum - 1, size }));
  }, [dispatch]);

  useEffect(() => {
    loadProducts(debouncedSearch, currentPage, pageSize);
  }, [debouncedSearch, currentPage, pageSize, loadProducts]);

  // Handle Search Input Change
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyword(e.target.value);
    setCurrentPage(1); // Reset to page 1 on new search
  };

  // Handle Delete Action
  const handleDelete = async (id: string) => {
    try {
      await dispatch(deleteProductThunk(id)).unwrap();
      message.success('Xóa sản phẩm thành công!');
      loadProducts(debouncedSearch, currentPage, pageSize);
    } catch (error: any) {
      message.error(`Lỗi: ${error}`);
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
    loadProducts(debouncedSearch, currentPage, pageSize);
  };

  const openStockInModal = (product: Product) => {
    setStockInProduct(product);
    setStockInVisible(true);
  };

  const handleStockInSuccess = () => {
    setStockInVisible(false);
    setStockInProduct(null);
    loadProducts(debouncedSearch, currentPage, pageSize);
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
      width: 250,
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
      title: 'Số lượng nhập',
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
      width: 150,
      fixed: 'right' as const,
      render: (_: any, record: Product) => (
        <Space size="middle">
          <Button
            type="text"
            icon={<PackagePlus size={16} className="text-amber-600" />}
            onClick={() => openStockInModal(record)}
            className="flex items-center justify-center hover:bg-amber-50"
            title="Nhập thêm sản phẩm"
          />
          <Button
            type="text"
            icon={<Edit size={16} className="text-blue-600" />}
            onClick={() => openEditModal(record)}
            className="flex items-center justify-center hover:bg-blue-50"
            title="Chỉnh sửa"
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
              title="Xóa"
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
        {/* Dòng 1: Tìm kiếm & Phân trang */}
        <div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-4">
          {/* Search bar */}
          <Input
            prefix={<Search size={16} className="text-slate-400 mr-2" />}
            placeholder="Tìm kiếm sản phẩm theo tên, SKU hoặc mô tả..."
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
            showTotal={(total) => `Tổng số: ${total} sản phẩm`}
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
              loadProducts(debouncedSearch, currentPage, pageSize);
              message.success('Đã làm mới danh sách sản phẩm!');
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
            title="Thêm sản phẩm"
          />
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
            pagination={false}
            scroll={{ x: 'max-content' }}
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

      {/* Product Stock In Modal */}
      <ProductStockInModal
        visible={stockInVisible}
        onCancel={() => {
          setStockInVisible(false);
          setStockInProduct(null);
        }}
        onSuccess={handleStockInSuccess}
        product={stockInProduct}
      />
    </div>
  );
};
