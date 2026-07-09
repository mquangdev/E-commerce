---
trigger: always_on
glob: "app/src/**/*.tsx"
description: "Quy chuẩn thiết kế component UI, Table Grid hai dòng và các form nhập liệu"
---

# Hướng dẫn Thiết kế Component UI & Quy chuẩn Table Grid (component-guide.md)

Tài liệu này đặc tả quy chuẩn thiết kế giao diện (UI) và hành vi tương tác (UX) của các bảng dữ liệu (**Table Grid**) trong hệ thống quản trị. Mọi trang quản trị sử dụng bảng dữ liệu phải tuân thủ cấu trúc và phong cách này để đảm bảo tính đồng bộ và trải nghiệm người dùng cao cấp.

---

## 1. Cấu trúc tổng thể của một Trang quản trị chứa Table

Một trang quản trị chuẩn gồm 3 khu vực chính:
1. **Tiêu đề trang (Page Header)**: Tiêu đề trang (`h2`) và phần mô tả ngắn gọn giúp người dùng hiểu nhanh nhiệm vụ của trang.
2. **Thẻ điều khiển chính (Main Controls Card)**: Bọc trong thẻ `<Card>` của Ant Design, chứa thanh tìm kiếm, cụm nút thao tác (Reload, Thêm mới) và phần hiển thị dữ liệu bảng.
3. **Bảng dữ liệu (Data Table)**: Hiển thị danh sách thực thể. Bộ phân trang (Pagination) sẽ được đẩy lên **trên đầu bảng** (`position: ['topRight']`) và phải hiển thị kèm tổng số bản ghi ở bên cạnh (`showTotal`).

---

## 2. Quy chuẩn thiết kế Bố cục điều khiển & Nút Reload

### A. Cấu trúc Bố cục Bảng Điều khiển Hai Hàng (Two-row Toolbar Layout)
Để giao diện cân đối, rõ ràng và phân định mạch lạc giữa điều hướng dữ liệu và các hành động tác vụ, thanh điều khiển trên bảng (Toolbar) sẽ được thiết kế thành hai dòng riêng biệt:
- **Dòng thứ nhất (Tìm kiếm & Phân trang)**:
  - Phía bên trái: Thanh tìm kiếm (`Input`) với chiều rộng tối đa thích hợp (`w-full sm:max-w-md`).
  - Phía bên phải: Component `<Pagination>` độc lập (Ant Design) được thiết lập `size="small"` để căn chỉnh điều hướng.
- **Dòng thứ hai (Cụm nút chức năng - Căn trái)**:
  - Nằm ngay dưới dòng thứ nhất, chứa các nút tác vụ như Reload và Thêm mới, được xếp gọn và **căn lề trái** (`flex justify-start items-center gap-2`).

```tsx
{/* Dòng 1: Tìm kiếm & Phân trang */}
<div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-4">
  <Input
    prefix={<Search size={16} />}
    className="w-full sm:max-w-md h-10 rounded-xl"
  />
  <Pagination
    current={currentPage}
    pageSize={pageSize}
    total={totalElements}
    showSizeChanger
    showTotal={(total) => `Tổng số: ${total} mục`}
    size="small"
    className="flex items-center justify-end text-slate-600"
  />
</div>

{/* Dòng 2: Cụm nút hành động - Căn trái */}
<div className="flex justify-start items-center gap-2 mb-6">
  <Button icon={<RotateCw />} title="Tải lại dữ liệu" />
  <Button type="primary" icon={<Plus />} title="Thêm mới" />
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
import { Typography, Card, Input, Button, Table, Pagination, Space, Skeleton, message } from 'antd';
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
        {/* Dòng 1: Tìm kiếm & Phân trang */}
        <div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-4 mb-4">
          <Input
            prefix={<Search size={16} className="text-slate-400 mr-2" />}
            placeholder="Tìm kiếm..."
            value={searchKeyword}
            onChange={(e) => { setSearchKeyword(e.target.value); setCurrentPage(1); }}
            className="w-full sm:max-w-md h-10 rounded-xl"
            allowClear
          />

          <Pagination
            current={currentPage}
            pageSize={pageSize}
            total={totalElements}
            showSizeChanger
            pageSizeOptions={['10', '20', '50']}
            showTotal={(total) => `Tổng số: ${total} mục`}
            onChange={(page, size) => { setCurrentPage(page); setPageSize(size); }}
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
            onClick={() => openModal()}
            className="h-10 w-10 rounded-xl flex items-center justify-center"
            title="Thêm mới"
          />
        </div>

        {loading && data.length === 0 ? (
          <Skeleton active paragraph={{ rows: 8 }} />
        ) : (
          <Table
            dataSource={data}
            columns={columns}
            rowKey="id"
            loading={loading}
            pagination={false}
            className="border-slate-100 rounded-xl overflow-hidden"
          />
        )}
      </Card>
    </div>
  );
};
```

---

## 5. Quy chuẩn Thiết kế Table Grid phân trang rộng và Cuộn ngang

Đối với các bảng dữ liệu hiển thị nhiều thông tin (nhiều hơn 5 cột hoặc cột chứa nội dung dài như UUID, địa chỉ, email):
- **Bắt buộc chỉ định độ rộng cột (Column Width)**: Định nghĩa thuộc tính `width` trong cấu hình `columns` cho từng cột để tránh việc trình duyệt tự động dồn ép chữ gây mất cân đối. Ví dụ: `width: 150`, `width: 250`.
- **Hỗ trợ cuộn ngang (Horizontal Scroll)**: Cấu hình thuộc tính `scroll={{ x: 'max-content' }}` hoặc một giá trị cụ thể ví dụ `scroll={{ x: 1200 }}` trên thẻ `<Table>`. Việc này giúp hiển thị thanh cuộn ngang mượt mà khi chiều rộng của tất cả các cột vượt quá khung nhìn của trình duyệt, thay vì bóp méo nội dung.
- **Ghim cột Thao tác (Fixed Action Column)**: Khi bật cuộn ngang cho bảng dữ liệu, cột **Thao tác (Action)** ở cuối bảng bắt buộc phải được cấu hình thuộc tính `fixed: 'right'` (hoặc `fixed: 'right' as const` trong TypeScript) để ghim cố định cột này bên phải khi người dùng cuộn xem thông tin các cột khác.

---

## 6. Quy chuẩn Thiết kế Form & Modal chuẩn Enterprise

Để giao diện hiển thị thông tin nghiệp vụ chuyên nghiệp, sạch sẽ:
- **Trình bày chi tiết (Detail Modal/Page)**: Sử dụng thiết kế phân vùng (Layout Grid) chia không gian hiển thị làm các khu vực chuyên biệt:
  - Khối thông tin khách hàng/vận chuyển: Sử dụng component `<Descriptions column={1} bordered={false} size="small" />` nằm ở vùng chính rộng hơn (2/3 chiều ngang).
  - Khối tóm tắt tài chính/trạng thái: Đặt ở góc bên phải (1/3 chiều ngang) bọc trong một chiếc `<Card>` có màu nền dịu nhẹ (ví dụ: các tone màu pastel, gradient nhẹ từ `blue-50` sang `indigo-50/30`) hiển thị trạng thái đơn hàng to, rõ ràng và tổng giá trị thanh toán nổi bật.
- **Form thêm mới danh sách động (Form.List)**:
  - Khi thiết kế nhập danh sách sản phẩm động, tuyệt đối không lặp lại nhãn tiêu đề (label) cho từng dòng nhập liệu. Thay vào đó, hãy dựng một hàng tiêu đề (Header row) duy nhất ở đầu danh sách mặt hàng, sau đó xếp các Input (chọn sản phẩm, nhập đơn giá, nhập số lượng, nút xóa) thành một hàng ngang thẳng cột với tiêu đề.
  - Sử dụng thẻ `<Space align="baseline">` hoặc CSS flexbox để căn chỉnh cân đối các ô nhập liệu, không làm lệch độ cao giữa các trường.

