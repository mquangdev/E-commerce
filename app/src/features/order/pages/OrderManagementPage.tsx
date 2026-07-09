import React, { useEffect, useState, useCallback } from 'react';
import { Typography, Card, Input, Button, Table, Pagination, Tag, Skeleton, message, Tooltip } from 'antd';
import { Search, Plus, RotateCw, Eye } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { useDebounce } from '@/hooks/useDebounce';
import { fetchOrders } from '../orderSlice';
import { OrderModal } from '../components/OrderModal';
import { OrderDetailModal } from '../components/OrderDetailModal';

const { Title, Paragraph, Text } = Typography;

export const OrderManagementPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { orders, loading, totalElements, pageNumber, pageSize, error } = useAppSelector((state) => state.order);

  const [searchKeyword, setSearchKeyword] = useState('');
  const debouncedSearch = useDebounce(searchKeyword, 500);

  const [currentPage, setCurrentPage] = useState(1);
  const [currentPageSize, setCurrentPageSize] = useState(10);

  // Modal states
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [selectedOrderId, setSelectedOrderId] = useState<string | null>(null);

  const fetchOrderData = useCallback(
    (keyword: string, page: number, size: number) => {
      // Spring Page is 0-indexed, React Pagination is 1-indexed
      dispatch(fetchOrders({ keyword, page: page - 1, size }));
    },
    [dispatch]
  );

  useEffect(() => {
    fetchOrderData(debouncedSearch, currentPage, currentPageSize);
  }, [debouncedSearch, currentPage, currentPageSize, fetchOrderData]);

  useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  const handleReload = () => {
    fetchOrderData(debouncedSearch, currentPage, currentPageSize);
    message.success('Đã cập nhật danh sách đơn hàng mới nhất!');
  };

  const getStatusTag = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Tag color="warning">Chờ xử lý</Tag>;
      case 'PROCESSING':
        return <Tag color="blue">Đang xử lý</Tag>;
      case 'COMPLETED':
        return <Tag color="success">Đã hoàn thành</Tag>;
      case 'CANCELLING':
        return <Tag color="volcano">Đang hủy</Tag>;
      case 'CANCELLED':
        return <Tag color="error">Đã hủy</Tag>;
      case 'FAILED':
        return <Tag color="red">Thất bại</Tag>;
      default:
        return <Tag color="default">{status}</Tag>;
    }
  };

  const columns = [
    {
      title: 'Mã đơn hàng',
      dataIndex: 'id',
      key: 'id',
      className: 'font-semibold text-slate-800 font-mono',
      width: 150,
      render: (id: string) => (
        <Tooltip title={id}>
          <Text copyable={{ text: id }}>
            {`${id.substring(0, 8)}...`}
          </Text>
        </Tooltip>
      ),
    },
    {
      title: 'Mã KH (User ID)',
      dataIndex: 'userId',
      key: 'userId',
      className: 'font-mono text-xs text-slate-500',
      width: 150,
      render: (userId: string) => (
        <Tooltip title={userId}>
          <span>{`${userId.substring(0, 8)}...`}</span>
        </Tooltip>
      ),
    },
    {
      title: 'Email khách hàng',
      dataIndex: 'email',
      key: 'email',
      className: 'text-slate-700',
      width: 220,
    },
    {
      title: 'Địa chỉ giao hàng',
      dataIndex: 'shippingAddress',
      key: 'shippingAddress',
      className: 'text-slate-600 max-w-xs truncate',
      width: 280,
    },
    {
      title: 'Tổng tiền',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      className: 'font-bold text-slate-800 text-right',
      width: 150,
      render: (amount: number) => `${amount.toLocaleString('vi-VN')} đ`,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 150,
      render: (status: string) => getStatusTag(status),
    },
    {
      title: 'Ngày đặt',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (createdAt: string) => {
        if (!createdAt) return '';
        // Safe formatting
        const date = new Date(createdAt);
        return date.toLocaleString('vi-VN');
      },
    },
    {
      title: 'Thao tác',
      key: 'action',
      align: 'center' as const,
      width: 100,
      fixed: 'right' as const,
      render: (_: any, record: any) => (
        <Button
          type="text"
          icon={<Eye size={16} className="text-blue-500" />}
          onClick={() => setSelectedOrderId(record.id)}
          className="flex items-center justify-center h-8 w-8 rounded-lg hover:bg-slate-100 mx-auto"
          title="Xem chi tiết"
        />
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div>
        <Title level={2} style={{ margin: 0, fontWeight: 700 }} className="text-slate-800">
          Quản lý Đơn hàng
        </Title>
        <Paragraph className="text-slate-500 mt-1">
          Xem danh sách đơn hàng và quản lý các hoạt động giao dịch đặt hàng trong hệ thống.
        </Paragraph>
      </div>

      <Card className="shadow-sm border-slate-200/80 rounded-2xl" bodyStyle={{ padding: '1.5rem' }}>
        {/* Dòng 1: Tìm kiếm & Phân trang */}
        <div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-4">
          <Input
            prefix={<Search size={16} className="text-slate-400 mr-2" />}
            placeholder="Tìm kiếm theo email, địa chỉ giao hàng..."
            value={searchKeyword}
            onChange={(e) => {
              setSearchKeyword(e.target.value);
              setCurrentPage(1);
            }}
            className="w-full sm:max-w-md h-10 rounded-xl"
            allowClear
          />

          <Pagination
            current={currentPage}
            pageSize={currentPageSize}
            total={totalElements}
            showSizeChanger
            pageSizeOptions={['10', '20', '50']}
            showTotal={(total) => `Tổng số: ${total} mục`}
            onChange={(page, size) => {
              setCurrentPage(page);
              setCurrentPageSize(size);
            }}
            size="small"
            className="flex items-center justify-end text-slate-600"
          />
        </div>

        {/* Dòng 2: Cụm nút hành động - Căn trái */}
        <div className="flex justify-start items-center gap-2 mb-6">
          <Button
            icon={<RotateCw size={16} />}
            onClick={handleReload}
            loading={loading}
            className="h-10 w-10 rounded-xl flex items-center justify-center text-slate-500 hover:text-slate-700 border-slate-200 transition-all duration-300"
            title="Tải lại dữ liệu"
          />
          <Button
            type="primary"
            icon={<Plus size={16} />}
            onClick={() => setIsCreateOpen(true)}
            className="h-10 w-10 rounded-xl flex items-center justify-center hover:scale-105 transition-all"
            title="Tạo đơn hàng mới"
          />
        </div>

        {loading && orders.length === 0 ? (
          <Skeleton active paragraph={{ rows: 8 }} />
        ) : (
          <Table
            dataSource={orders}
            columns={columns}
            rowKey="id"
            loading={loading}
            pagination={false}
            scroll={{ x: 'max-content' }}
            className="border-slate-100 rounded-xl overflow-hidden shadow-sm"
          />
        )}
      </Card>

      {/* Add Order Modal */}
      <OrderModal
        visible={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        // onSuccess={() => fetchOrderData(debouncedSearch, currentPage, currentPageSize)}
        onSuccess={() => {
          console.log('Thêm đơn hàng thành công');
        }}
      />

      {/* View Order Detail Modal */}
      <OrderDetailModal
        visible={!!selectedOrderId}
        onClose={() => setSelectedOrderId(null)}
        orderId={selectedOrderId}
      />
    </div>
  );
};
export default OrderManagementPage;
