# Hướng dẫn Thiết kế Component UI & Quy chuẩn Table Grid (component-guide.md)

Tài liệu này đặc tả quy chuẩn thiết kế giao diện (UI) và hành vi tương tác (UX) của các bảng dữ liệu (**Table Grid**) trong hệ thống quản trị. Mọi trang quản trị sử dụng bảng dữ liệu phải tuân thủ cấu trúc và phong cách này để đảm bảo tính đồng bộ và trải nghiệm người dùng cao cấp.

---

## 1. Cấu trúc tổng thể của một Trang quản trị chứa Table

Một trang quản trị chuẩn gồm 3 khu vực chính:
1. **Tiêu đề trang (Page Header)**: Tiêu đề trang (`h2`) và phần mô tả ngắn gọn giúp người dùng hiểu nhanh nhiệm vụ của trang.
2. **Thẻ điều khiển chính (Main Controls Card)**: Bọc trong thẻ `<Card>` của Ant Design, chứa thanh tìm kiếm, cụm nút thao tác (Reload, Thêm mới) và phần hiển thị dữ liệu bảng.
3. **Bảng dữ liệu (Data Table)**: Hiển thị danh sách thực thể cùng bộ phân trang đồng bộ.

---

## 2. Quy chuẩn thiết kế Bố cục điều khiển & Nút Reload

### A. Cấu trúc Flexbox của Toolbar
Cụm điều khiển trên bảng phải sử dụng cấu trúc Responsive Flexbox:
- Trực quan: Thanh tìm kiếm (`Input`) nằm bên trái, cụm nút hành động (Actions Toolbar) nằm bên phải.
- Trên Mobile: Chuyển thành dạng cột đứng (`flex-col`), các nút chiếm toàn bộ chiều rộng (`w-full`).
- Trên Desktop: Trải ngang (`flex-row`), căn chỉnh giữa (`items-center`), phân bố hai đầu (`justify-between`).

```tsx
<div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-6">
  {/* Tìm kiếm */}
  <Input ... />

  {/* Nhóm nút hành động */}
  <div className="flex items-center gap-2">
    {/* Nút Reload */}
    <Button ... />
    
    {/* Nút Thêm mới */}
    <Button ... />
  </div>
</div>
```

### B. Quy chuẩn thiết kế nút Reload
Nút Reload đóng vai trò nạp lại dữ liệu hiện tại từ server nhanh chóng.
- **Icon**: Sử dụng `RotateCw` từ thư viện `lucide-react` để thể hiện tính chất làm mới đồng bộ.
- **Kích thước & Bo góc**: Nút có chiều rộng và cao bằng nhau (`h-10 w-10`), bo góc tròn mượt mà (`rounded-xl`).
- **Styling**: Loại mặc định (`type="default"` hoặc không set `type`), viền màu xám dịu (`border-slate-200`), màu chữ xám (`text-slate-500 hover:text-slate-700`).
- **Hiệu ứng Loading (UX Quan trọng)**: Truyền trạng thái `loading={loading}` từ local state vào nút để icon tự động xoay tròn khi dữ liệu đang được fetch, mang lại phản hồi trực quan cực tốt cho người dùng.

---

## 3. Quy chuẩn Hiển thị Ngày tháng (Date-only Rendering)

Đối với các thực thể sử dụng kiểu dữ liệu `LocalDate` (chỉ lưu ngày, không lưu giờ), khi hiển thị trên Table:
- Không sử dụng `new Date(date).toLocaleString()` vì múi giờ local của trình duyệt có thể làm lệch ngày (ví dụ: ngày `2026-05-26` ở múi giờ UTC khi parse ở GMT-5 có thể bị lùi thành `2026-05-25`).
- Sử dụng hàm định dạng thủ công bằng chuỗi hoặc hàm xử lý múi giờ an toàn để định dạng chuẩn Việt Nam `dd/MM/yyyy`.

**Hàm Helper định dạng khuyên dùng:**
```typescript
export const formatDateOnly = (dateStr: string): string => {
  if (!dateStr) return '';
  const parts = dateStr.split('-');
  if (parts.length === 3) {
    // Định dạng yyyy-MM-dd -> dd/MM/yyyy
    return `${parts[2]}/${parts[1]}/${parts[0]}`;
  }
  return new Date(dateStr).toLocaleDateString('vi-VN');
};
```

---

## 4. Code mẫu Component chuẩn (Template)

Dưới đây là mã nguồn mẫu cho một trang quản trị chuẩn hóa theo các quy tắc trên:

```tsx
import React, { useEffect, useState, useCallback } from 'react';
import { Typography, Card, Input, Button, Table, Space, Skeleton, message } from 'antd';
import { Search, Plus, RotateCw, Edit, Trash2 } from 'lucide-react';
import { useDebounce } from '@/hooks/useDebounce';
import { formatDateOnly } from '@/utils/dateHelper'; // Hoặc viết trực tiếp trong component

const { Title, Paragraph } = Typography;

export const SampleManagementPage: React.FC = () => {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const debouncedSearch = useDebounce(searchKeyword, 500);

  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  const fetchData = useCallback(async (keyword: string, pageNum: number, size: number) => {
    setLoading(true);
    try {
      // Gọi API tương ứng (giả định trả về phân trang)
      const res = await getItems(keyword, pageNum - 1, size);
      setData(res.content);
      setTotalElements(res.totalElements);
    } catch (error) {
      message.error('Không thể tải danh sách dữ liệu.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData(debouncedSearch, currentPage, pageSize);
  }, [debouncedSearch, currentPage, pageSize, fetchData]);

  const handleReload = () => {
    fetchData(debouncedSearch, currentPage, pageSize);
    message.success('Đã cập nhật dữ liệu mới nhất!');
  };

  const columns = [
    {
      title: 'Tên',
      dataIndex: 'name',
      key: 'name',
      className: 'font-semibold text-slate-800',
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => formatDateOnly(date),
    },
    // Các cột khác...
  ];

  return (
    <div className="space-y-6">
      <div>
        <Title level={2} style={{ margin: 0, fontWeight: 700 }} className="text-slate-800">
          Tiêu đề quản lý
        </Title>
        <Paragraph className="text-slate-500 mt-1">
          Mô tả ngắn gọn về chức năng của trang.
        </Paragraph>
      </div>

      <Card className="shadow-sm border-slate-200/80 rounded-2xl" bodyStyle={{ padding: '1.5rem' }}>
        <div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-6">
          <Input
            prefix={<Search size={16} className="text-slate-400 mr-2" />}
            placeholder="Tìm kiếm..."
            value={searchKeyword}
            onChange={(e) => { setSearchKeyword(e.target.value); setCurrentPage(1); }}
            className="w-full sm:max-w-md h-10 rounded-xl"
            allowClear
          />

          <div className="flex items-center gap-2">
            {/* Nút Reload chuẩn thiết kế */}
            <Button
              icon={<RotateCw size={16} />}
              onClick={handleReload}
              loading={loading}
              className="h-10 w-10 rounded-xl flex items-center justify-center text-slate-500 hover:text-slate-700 border-slate-200 transition-all duration-300"
              title="Tải lại dữ liệu"
            />
            <Button
              type="primary"
              icon={<Plus size={16} className="mr-1" />}
              onClick={() => openModal()}
              className="h-10 rounded-xl flex items-center justify-center font-semibold"
            >
              Thêm mới
            </Button>
          </div>
        </div>

        {loading && data.length === 0 ? (
          <Skeleton active paragraph={{ rows: 8 }} />
        ) : (
          <Table
            dataSource={data}
            columns={columns}
            rowKey="id"
            loading={loading}
            pagination={{
              current: currentPage,
              pageSize: pageSize,
              total: totalElements,
              showSizeChanger: true,
              onChange: (page, size) => { setCurrentPage(page); setPageSize(size); },
              className: 'pt-4',
            }}
            className="border-slate-100 rounded-xl overflow-hidden"
          />
        )}
      </Card>
    </div>
  );
};
```
