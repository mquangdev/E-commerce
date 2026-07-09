import React, { useEffect } from 'react';
import { Modal, Descriptions, Table, Tag, Typography, Button, Spin, Empty, Divider } from 'antd';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { fetchOrderById, setCurrentOrder } from '../orderSlice';
import { fetchProducts } from '@/features/catalog/catalogSlice';
import { ShoppingBag } from 'lucide-react';

const { Title, Text } = Typography;

interface OrderDetailModalProps {
  visible: boolean;
  onClose: () => void;
  orderId: string | null;
}

export const OrderDetailModal: React.FC<OrderDetailModalProps> = ({ visible, onClose, orderId }) => {
  const dispatch = useAppDispatch();
  const { currentOrder, currentOrderLoading, currentOrderError } = useAppSelector((state) => state.order);
  const { products } = useAppSelector((state) => state.catalog);

  useEffect(() => {
    if (visible && orderId) {
      dispatch(fetchOrderById(orderId));
      // Load products if they aren't loaded yet to look up product names
      if (products.length === 0) {
        dispatch(fetchProducts({ keyword: '', page: 0, size: 1000 }));
      }
    }
    return () => {
      if (!visible) {
        dispatch(setCurrentOrder(null));
      }
    };
  }, [visible, orderId, dispatch, products.length]);

  const getProductName = (productId: string) => {
    const prod = products.find((p) => p.id === productId);
    return prod ? prod.name : `Sản phẩm (${productId.substring(0, 8)}...)`;
  };

  const getProductSku = (productId: string) => {
    const prod = products.find((p) => p.id === productId);
    return prod ? prod.sku : '';
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
      title: 'Sản phẩm',
      key: 'product',
      render: (_: any, record: any) => (
        <div>
          <div className="font-semibold text-slate-800">
            {getProductName(record.productId)}
          </div>
          {getProductSku(record.productId) && (
            <div className="text-xs text-slate-400 font-mono">
              {getProductSku(record.productId)}
            </div>
          )}
        </div>
      ),
    },
    {
      title: 'Đơn giá',
      dataIndex: 'unitPrice',
      key: 'unitPrice',
      render: (price: number) => `${price.toLocaleString('vi-VN')} đ`,
      align: 'right' as const,
    },
    {
      title: 'Số lượng',
      dataIndex: 'quantity',
      key: 'quantity',
      align: 'center' as const,
    },
    {
      title: 'Thành tiền',
      key: 'total',
      render: (_: any, record: any) =>
        `${(record.unitPrice * record.quantity).toLocaleString('vi-VN')} đ`,
      align: 'right' as const,
    },
  ];

  return (
    <Modal
      title={
        <div className="flex items-center space-x-2 border-b pb-3 text-slate-800">
          <ShoppingBag size={18} className="text-blue-600" />
          <span className="font-bold text-lg">Chi tiết đơn hàng</span>
        </div>
      }
      open={visible}
      onCancel={onClose}
      footer={[
        <Button key="close" onClick={onClose} className="rounded-xl font-semibold h-10 hover:scale-95 transition-all">
          Đóng
        </Button>
      ]}
      width={800}
      className="rounded-2xl overflow-hidden"
    >
      {currentOrderLoading ? (
        <div className="py-12 flex justify-center items-center">
          <Spin size="large" tip="Đang tải chi tiết đơn hàng..." />
        </div>
      ) : currentOrderError ? (
        <div className="py-6">
          <Empty description={<Text type="danger">{currentOrderError}</Text>} />
        </div>
      ) : currentOrder ? (
        <div className="space-y-6 pt-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Left part: Delivery & User info (2/3 width) */}
            <div className="md:col-span-2 space-y-4">
              <div className="bg-slate-50/50 dark:bg-slate-900/30 p-5 rounded-2xl border border-slate-100/80 dark:border-slate-800 space-y-3.5 shadow-sm">
                <div className="flex justify-between items-baseline py-1 border-b border-slate-100/60 dark:border-slate-800/40">
                  <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">Mã đơn hàng</span>
                  <Text copyable className="font-mono text-xs text-slate-800 dark:text-slate-200">
                    {currentOrder.id}
                  </Text>
                </div>
                <div className="flex justify-between items-baseline py-1 border-b border-slate-100/60 dark:border-slate-800/40">
                  <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">Mã khách hàng</span>
                  <Text className="font-mono text-xs text-slate-600 dark:text-slate-400">{currentOrder.userId}</Text>
                </div>
                <div className="flex justify-between items-baseline py-1 border-b border-slate-100/60 dark:border-slate-800/40">
                  <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">Email liên hệ</span>
                  <Text className="text-xs text-slate-800 dark:text-slate-200 font-medium">{currentOrder.email}</Text>
                </div>
                <div className="flex justify-between items-baseline py-1 border-b border-slate-100/60 dark:border-slate-800/40">
                  <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">Ngày tạo đơn</span>
                  <Text className="text-xs text-slate-700 dark:text-slate-300">
                    {currentOrder.createdAt ? new Date(currentOrder.createdAt).toLocaleString('vi-VN') : ''}
                  </Text>
                </div>
                <div className="flex flex-col space-y-1 pt-1">
                  <span className="text-xs font-semibold text-slate-500 dark:text-slate-400">Địa chỉ giao hàng</span>
                  <Text className="text-xs text-slate-700 dark:text-slate-300 leading-relaxed">{currentOrder.shippingAddress}</Text>
                </div>
              </div>
            </div>

            {/* Right part: Status & Cost Card (1/3 width) */}
            <div>
              <div className="bg-gradient-to-br from-blue-50/70 to-indigo-50/40 dark:from-slate-850 dark:to-slate-800 border border-slate-100 dark:border-slate-800/80 rounded-2xl p-5 h-full flex flex-col justify-between shadow-sm">
                <div>
                  <span className="text-[10px] uppercase font-bold tracking-wider text-slate-400 block mb-1.5">Trạng thái đơn hàng</span>
                  {getStatusTag(currentOrder.status)}
                </div>
                <Divider className="my-4 border-slate-100/50 dark:border-slate-700/50" />
                <div className="space-y-1">
                  <span className="text-[10px] uppercase font-extrabold tracking-wider text-slate-500 dark:text-slate-400 block">Tổng tiền thanh toán</span>
                  <Title level={3} style={{ margin: 0 }} className="text-blue-600 dark:text-blue-400 font-extrabold tracking-tight">
                    {currentOrder.totalAmount ? currentOrder.totalAmount.toLocaleString('vi-VN') : '0'} đ
                  </Title>
                </div>
              </div>
            </div>
          </div>

          <div>
            <div className="flex items-center space-x-2 mb-3">
              <div className="h-1.5 w-1.5 rounded-full bg-blue-600"></div>
              <span className="text-sm font-bold text-slate-800 dark:text-slate-200 uppercase tracking-wider">Danh sách sản phẩm đã đặt</span>
            </div>
            <Table
              dataSource={currentOrder.items}
              columns={columns}
              rowKey="id"
              pagination={false}
              className="border-slate-100 rounded-xl overflow-hidden shadow-sm"
              summary={(pageData) => {
                let total = 0;
                pageData.forEach(({ unitPrice, quantity }) => {
                  total += unitPrice * quantity;
                });
                return (
                  <Table.Summary.Row className="bg-slate-50/50 dark:bg-slate-900/10 font-bold">
                    <Table.Summary.Cell index={0} colSpan={3} className="text-right text-slate-650">
                      Tổng tiền thanh toán:
                    </Table.Summary.Cell>
                    <Table.Summary.Cell index={1} className="text-right text-blue-600 text-base font-extrabold">
                      {total.toLocaleString('vi-VN')} đ
                    </Table.Summary.Cell>
                  </Table.Summary.Row>
                );
              }}
            />
          </div>
        </div>
      ) : (
        <Empty description="Không có dữ liệu đơn hàng" />
      )}
    </Modal>
  );
};
