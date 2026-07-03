# Đặc tả Quy chuẩn UI/UX & Các Trạng thái Giao diện (requirements.md)

Tài liệu này đóng vai trò là hướng dẫn bắt buộc cho mọi hoạt động sinh mã (code generation) hoặc phát triển giao diện người dùng mới trong dự án E-commerce. Tất cả các trang và component phải tuân thủ nghiêm ngặt các quy tắc dưới đây.

---

## 1. Cấu trúc Layout Tổng thể

Ứng dụng sử dụng một hệ thống Layout thống nhất dựa trên vai trò người dùng và mục đích của trang:

### A. Main Layout (Giao diện mua sắm và trang chủ)
- **Header**: Cố định ở đầu trang (`sticky top-0 z-50`), nền mờ thủy tinh (`bg-white/80 backdrop-blur-md`), chứa Logo, thanh Tìm kiếm toàn cục, nút Giỏ hàng (kèm Badge số lượng) và thông tin Tài khoản người dùng.
- **Content Area**: Chiều rộng tối đa cố định ở `max-w-7xl`, căn giữa (`mx-auto`), khoảng đệm (padding) tối thiểu `px-4 py-6 md:px-8`.
- **Footer**: Chứa thông tin bản quyền, các liên kết chính sách và liên kết mạng xã hội.

### B. Auth Layout (Đăng nhập, Đăng ký, Quên mật khẩu)
- Thiết kế tối giản, sạch sẽ, tập trung vào Form.
- **Desktop**: Chia đôi màn hình (Bên trái là hình ảnh minh họa/marketing chất lượng cao có gradient; Bên phải là Form đăng nhập/đăng ký căn giữa).
- **Mobile**: Form căn giữa trên nền xám nhạt, bọc trong một Card có bo góc mềm mại (`rounded-2xl`) và đổ bóng nhẹ.

### C. Admin/Dashboard Layout (Trang quản trị)
- **Sidebar**: Cố định bên trái (`fixed left-0`), có thể thu gọn (collapsible), hiển thị Menu chức năng dưới dạng cây (Tree menu).
- **Header**: Hiển thị Breadcrumb (đường dẫn trang hiện tại), nút thông báo và Menu Avatar người quản trị.
- **Content**: Nền xám nhạt (`bg-slate-50`), tự động cuộn khi tràn nội dung.

---

## 2. Logic Hành vi và Nút bấm (Buttons & Forms)

- **Trạng thái vô hiệu hóa (Disabled State)**:
  - Nút bấm gửi dữ liệu (Submit) phải được vô hiệu hóa khi form chưa hợp lệ (invalid) hoặc đang trong quá trình gửi yêu cầu (submitting/loading).
- **Hiệu ứng phản hồi (Feedback/Active State)**:
  - Tất cả các nút bấm tương tác phải có hiệu ứng hover rõ ràng (thay đổi độ đậm nhạt của màu nền hoặc đổ bóng).
  - Sử dụng hiệu ứng active (click) làm nhỏ nhẹ kích thước (scale-95) để tạo cảm giác bấm vật lý chân thực.
- **Xác nhận hành vi nguy hiểm (Destructive Actions)**:
  - Các hành động như Xóa sản phẩm, Hủy đơn hàng, Đăng xuất bắt buộc phải hiển thị hộp thoại xác nhận (AntD `<Popconfirm>` hoặc `<Modal>`) trước khi thực thi.

---

## 3. Quy chuẩn xử lý các Trạng thái Giao diện

Mọi tương tác gọi API hoặc tải dữ liệu bất đồng bộ bắt buộc phải thể hiện rõ 3 trạng thái: **Tải dữ liệu (Loading)**, **Thành công (Success)**, và **Lỗi (Error)**.

### A. Trạng thái Loading (Đang tải)
- **Danh sách / Bảng dữ liệu**: Không dùng vòng xoay (Spinner) toàn màn hình gây khó chịu. Sử dụng component `<Skeleton>` của Ant Design để giả lập cấu trúc dữ liệu đang tải.
- **Form / Nút bấm**: Khi người dùng nhấn nút gửi dữ liệu (ví dụ: Đăng nhập, Thanh toán), nút bấm phải chuyển sang trạng thái loading (`loading={true}` của AntD) và vô hiệu hóa tất cả các trường nhập liệu trong form để tránh gửi trùng lặp (double submit).
- **Tải trang ban đầu**: Sử dụng thanh tiến trình nhỏ ở đầu trang (Page Loading Bar) hoặc skeleton layout.

### B. Trạng thái Success (Thành công)
- **Tác vụ nhanh (Thêm vào giỏ, cập nhật thông tin)**: Hiển thị thông báo nhỏ tự ẩn bằng `message.success()` của Ant Design ở góc trên cùng giữa màn hình.
- **Tác vụ lớn (Đặt hàng thành công, Đăng ký tài khoản thành công)**: Điều hướng người dùng tới một trang kết quả riêng biệt sử dụng component `<Result>` của Ant Design với thông điệp rõ ràng và các nút hành động tiếp theo (ví dụ: "Tiếp tục mua sắm", "Xem chi tiết đơn hàng").

### C. Trạng thái Error (Lỗi)
- **Lỗi Form (Validation Error)**: Hiển thị thông báo lỗi ngay dưới trường nhập liệu có màu đỏ, tự động focus vào trường lỗi đầu tiên.
- **Lỗi Hệ thống / Lỗi gọi API**:
  - Không để màn hình trắng hoặc treo.
  - Sử dụng thông báo `notification.error()` của Ant Design để hiển thị chi tiết lỗi một cách thân thiện ở góc phải màn hình.
  - Đối với trang nội dung không tải được dữ liệu, hiển thị component `<Empty>` kèm nút "Tải lại trang" (Retry Button).
