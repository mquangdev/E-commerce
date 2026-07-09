---
trigger: always_on
glob: "app/src/**/*"
description: "Hệ thống thiết kế UI, bảng màu, font chữ Inter và khoảng cách spacing chuẩn"
---

# Hệ thống Thiết kế & Đồng bộ Styling (design-system.md)

Tài liệu này định nghĩa hệ thống thiết kế (Design System) của ứng dụng và các quy tắc kết hợp giữa **Tailwind CSS v4** và **Ant Design v5**. Việc tuân thủ tài liệu này giúp đảm bảo giao diện luôn đồng bộ về màu sắc, khoảng cách và kiểu dáng.

---

## 1. Hệ thống màu sắc (Color Tokens)

Bảng màu được đồng bộ giữa Tailwind CSS v4 (qua biến CSS `@theme`) và Ant Design (qua `<ConfigProvider>` token):

| Tên màu | Mã màu HEX | Sử dụng trong Tailwind | Sử dụng trong Ant Design |
| :--- | :--- | :--- | :--- |
| **Primary (Chủ đạo)** | `#1677ff` | `text-brand-primary` / `bg-brand-primary` | `colorPrimary` (Token gốc) |
| **Success (Thành công)** | `#52c41a` | `text-emerald-500` / `bg-emerald-500` | `colorSuccess` |
| **Warning (Cảnh báo)** | `#faad14` | `text-amber-500` / `bg-amber-500` | `colorWarning` |
| **Error (Lỗi/Nguy hiểm)**| `#ff4d4f` | `text-red-500` / `bg-red-500` | `colorError` |
| **Text Primary** | `#1e293b` | `text-slate-800` | `colorText` |
| **Text Secondary** | `#64748b` | `text-slate-500` | `colorTextDescription` |
| **Background Body** | `#f8fafc` | `bg-slate-50` | `colorBgLayout` |
| **Background Component**| `#ffffff` | `bg-white` | `colorBgContainer` |

---

## 2. Quy tắc phân chia vai trò giữa Tailwind và Ant Design

Để tránh việc ghi đè CSS chồng chéo gây vỡ giao diện, chúng ta chia rõ vai trò sử dụng:

### A. Sử dụng Ant Design cho:
- Các thành phần điều khiển Form phức tạp: `Form`, `Input`, `Select`, `Radio`, `Checkbox`, `Switch`, `Upload`.
- Các thành phần tương tác lớp phủ: `Modal`, `Drawer`, `Popover`, `Tooltip`.
- Các bảng biểu dữ liệu lớn và phân trang: `Table`, `Pagination`.
- Các cấu trúc điều hướng phức tạp: `Menu`, `Tabs`, `Dropdown`, `Steps`.

### B. Sử dụng Tailwind CSS v4 cho:
- **Bố cục (Bones & Layout)**: Định dạng lưới (`grid`), flexbox (`flex`), khoảng cách (`p-*`, `m-*`), định vị (`absolute`, `relative`).
- **Tinh chỉnh giao diện phụ**: Các viền bao ngoài, màu nền tùy biến, bo góc các thẻ tự tạo, căn chỉnh khoảng cách giữa các phần tử độc lập.
- **Micro-interactions**: Các hiệu ứng hover chuyển màu, scale, mờ đục hoặc hiệu ứng chuyển cảnh (`transition-all duration-300`).

---

## 3. Đồng bộ hóa cấu hình trong Code

### A. Cấu hình Tailwind CSS v4 (trong `src/styles/index.css`)
Không sử dụng tệp `tailwind.config.js` nữa. Mọi tùy chỉnh theme được đưa vào khối `@theme` của tệp CSS:

```css
@import "tailwindcss";

@theme {
  --color-brand-primary: #1677ff;
  --color-brand-success: #52c41a;
  --color-brand-warning: #faad14;
  --color-brand-error: #ff4d4f;
  
  --font-sans: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  
  --radius-custom-lg: 12px;
  --radius-custom-md: 8px;
}
```

### B. Cấu hình Ant Design Theme (trong `src/App.tsx`)
Bọc ứng dụng bằng `<ConfigProvider>` để truyền các giá trị token thiết kế tương đương:

```tsx
import { ConfigProvider } from 'antd';

function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1677ff',
          colorSuccess: '#52c41a',
          colorWarning: '#faad14',
          colorError: '#ff4d4f',
          borderRadius: 8,
          fontFamily: 'Inter, sans-serif',
        },
        components: {
          Button: {
            controlHeight: 40, // Tăng chiều cao nút tiêu chuẩn lên 40px cho hiện đại
            fontWeight: 500,
          },
          Input: {
            controlHeight: 40,
          },
        },
      }}
    >
      {/* Các component con */}
    </ConfigProvider>
  );
}
```

---

## 4. Quy định về Typography & Spacing

- **Typography**:
  - Tiêu đề trang (`h1`): Kích cỡ `text-2xl md:text-3xl font-bold tracking-tight text-slate-900`.
  - Tiêu đề thẻ (`h2`, `h3`): Kích cỡ `text-lg font-semibold text-slate-800`.
  - Văn bản thường: Kích cỡ `text-sm text-slate-600 leading-relaxed`.
- **Spacing Grid**:
  - Luôn sử dụng bội số của 4 cho padding/margin để giữ nhịp điệu thiết kế (ví dụ: `p-2` = 8px, `p-4` = 16px, `p-6` = 24px, `p-8` = 32px).
  - Khoảng cách giữa các phần lớn trên trang luôn tối thiểu là `space-y-6` hoặc `space-y-8`.
