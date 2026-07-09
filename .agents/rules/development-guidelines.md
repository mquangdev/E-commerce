---
trigger: always_on
glob: "*"
description: "Quy chuẩn phát triển mã nguồn chung, cấu trúc dự án và quy tắc đặt tên"
---

# Hướng dẫn Phát triển & Quy chuẩn Viết Code (development-guidelines.md)

Tài liệu này hướng dẫn chi tiết cách tổ chức mã nguồn, viết code TypeScript, quản lý trạng thái (State) và giao tiếp API với Backend cho dự án Frontend.

---

## 1. Tiêu chuẩn Viết Code (Coding Standards)

- **Quy tắc đặt tên (Naming Conventions)**:
  - **Component**: Đặt tên theo dạng `PascalCase`. Ví dụ: `ProductCard.tsx`, `LoginForm.tsx`.
  - **Hooks**: Đặt tên bắt đầu bằng tiền tố `use` dạng `camelCase`. Ví dụ: `useAuth.ts`, `useCart.ts`.
  - **Services / Utilities / Slices**: Đặt tên dạng `camelCase`. Ví dụ: `api.ts`, `authSlice.ts`, `formatHelper.ts`.
- **TypeScript Strict Mode**:
  - Không được phép sử dụng kiểu dữ liệu `any`. Nếu không xác định được kiểu dữ liệu, hãy sử dụng `unknown` hoặc viết Interface/Type rõ ràng.
  - Mọi tham số hàm, thuộc tính props của Component bắt buộc phải được khai báo kiểu dữ liệu.

---

## 2. Tổ chức Thư mục theo Tính năng (Feature-based Architecture)

Khi thêm một chức năng mới (ví dụ: giỏ hàng - Cart), hãy gom nhóm tất cả các tệp liên quan vào thư mục tính năng đó:

```
src/features/cart/
├── cartSlice.ts        # Quản lý state cục bộ của Cart (Redux Toolkit)
├── cartService.ts      # Các lời gọi API liên quan tới giỏ hàng (sử dụng Axios)
├── components/         # Các component UI nhỏ dùng riêng cho Cart
│   ├── CartItem.tsx
│   └── CartSummary.tsx
├── pages/              # Trang Cart chính hiển thị cho người dùng
│   └── CartPage.tsx
└── hooks/              # Custom hooks cho tính năng này nếu có
```

---

## 3. Quản lý Trạng thái (State Management)

Ứng dụng phân chia thành hai loại State rõ rệt:

### A. State Toàn cục (Global State - Redux Toolkit)
- **Khi nào dùng**: Chỉ sử dụng Redux khi dữ liệu cần chia sẻ giữa nhiều trang không liên quan trực tiếp, hoặc cần duy trì xuyên suốt phiên làm việc (ví dụ: Thông tin người dùng hiện tại, Token xác thực, Giỏ hàng hiện tại, Cấu hình giao diện).
- **Quy chuẩn định nghĩa Slice**:
  - Đặt tên slice rõ ràng.
  - Sử dụng `createAsyncThunk` của Redux Toolkit để gọi các API không đồng bộ làm thay đổi state toàn cục.
  - Khai báo đầy đủ kiểu dữ liệu cho `initialState`.

### B. State Cục bộ (Local State - React `useState`)
- **Khi nào dùng**: Các dữ liệu chỉ phục vụ cho một component duy nhất hoặc truyền trực tiếp qua props giữa các component cha con (ví dụ: trạng thái đóng/mở của modal, dữ liệu nhập tạm thời của Form, trạng thái hiển thị loading cục bộ).
- **Nghiêm cấm**: Đưa các dữ liệu mang tính chất tạm thời của một form đơn lẻ vào Redux Store để tránh làm loãng và giảm hiệu năng ứng dụng.

---

## 4. Giao tiếp API & Xử lý lỗi (API Calls & Error Handling)

- **Sử dụng Axios Client**:
  - Mọi request gửi lên Backend bắt buộc phải thông qua Axios Instance chung đặt tại `src/services/api.ts`.
  - Không sử dụng thư viện `fetch` hoặc tự khởi tạo `axios` cục bộ tại các file component.
- **Xử lý Token tự động**:
  - Request Interceptor tự động tìm khóa `token` trong `localStorage` và đính kèm vào header `Authorization: Bearer <token>`.
- **Bắt lỗi toàn cục (Global Error Interceptor)**:
  - Nếu API trả về lỗi `401 Unauthorized`, hệ thống tự động xóa token hiện tại khỏi thiết bị và điều hướng người dùng về trang Đăng nhập (`/login`).
  - Nếu gặp các lỗi mạng hoặc lỗi Server (`500 Internal Server Error`, `503 Service Unavailable`), interceptor sẽ bắn thông báo dạng Notification từ Ant Design để người dùng nắm được sự cố mà không bị crash ứng dụng.
- **Xử lý lỗi cục bộ**:
  - Khi bắt các lỗi nghiệp vụ cụ thể (ví dụ: sai mật khẩu khi đăng nhập, trùng email khi đăng ký - trả về mã `400 Bad Request`), lập trình viên sử dụng khối `try...catch` khi dispatch thunk hoặc gọi api để bắt lỗi trực tiếp và đưa ra hiển thị lỗi trên form hoặc màn hình tương ứng.
