# TỔNG QUAN DỰ ÁN WORDPULSE

## 1. Lời mở đầu
Trong kỷ nguyên số hóa, việc học ngoại ngữ, đặc biệt là tiếng Anh, đã trở thành một nhu cầu thiết yếu để tiếp cận tri thức và cơ hội toàn cầu. Tuy nhiên, nhiều người học vẫn gặp khó khăn trong việc duy trì động lực và tìm kiếm một phương pháp học tập tích hợp hiệu quả.

**WordPulse** được ra đời với sứ mệnh trở thành một "nhịp đập" đồng hành cùng người học. Ứng dụng không chỉ là một công cụ học từ vựng đơn thuần mà là một hệ sinh thái học tập thông minh, kết hợp giữa việc ghi nhớ chủ động (Active Recall), luyện nghe hiểu và đọc hiểu thông qua ngữ cảnh thực tế. Với giao diện hiện đại, thân thiện và các tính năng tương tác cao, WordPulse giúp việc học tiếng Anh trở nên tự nhiên, thú vị và hiệu quả hơn bao giờ hết.

---

## 2. Mục tiêu dự án
- **Toàn diện:** Cung cấp đầy đủ các kỹ năng từ Từ vựng, Ngữ pháp đến Nghe và Đọc hiểu.
- **Tiện lợi:** Học mọi lúc mọi nơi với cơ sở dữ liệu nội bộ (SQLite/Room), không phụ thuộc hoàn toàn vào kết nối mạng.
- **Trải nghiệm người dùng:** Giao diện trực quan, hỗ trợ đa phương tiện (hình ảnh, âm thanh TTS) để tăng khả năng ghi nhớ.

---

## 3. Các tính năng chính
- **Học Từ vựng (Vocabulary):** Sử dụng hệ thống Flashcards trực quan, phân loại theo nhiều chủ đề đa dạng (Gia đình, Kinh doanh, v.v.).
- **Luyện Nghe (Listening):** Các bài tập nghe và điền từ (Dictation), trắc nghiệm nghe hiểu giúp cải thiện phản xạ âm thanh.
- **Học Ngữ pháp (Grammar):** Hệ thống bài giảng và bài tập thực hành được cấu trúc logic.
- **Đọc truyện & Hiểu bài (Story):**
    - Kho truyện ngắn phong phú giúp học từ vựng qua ngữ cảnh.
    - Tích hợp công nghệ **Text-to-Speech (TTS)** đọc nội dung chuẩn bản xứ.
    - Bài tập trắc nghiệm sau mỗi câu chuyện để kiểm tra mức độ hiểu bài.
- **Theo dõi tiến độ:** Hệ thống đánh giá kết quả sau mỗi bài kiểm tra, giúp người học nhận biết điểm mạnh và điểm cần cải thiện.

---

## 4. Công nghệ sử dụng
- **Ngôn ngữ:** Kotlin.
- **Kiến trúc:** MVVM (Model-View-ViewModel) đảm bảo mã nguồn sạch và dễ bảo trì.
- **Cơ sở dữ liệu:** Room Persistence Library (SQLite) quản lý dữ liệu hiệu quả.
- **UI/UX:** Material Design 3, Jetpack Components.
- **API Hệ thống:** Android Text-to-Speech Engine.

---

## 6. Phương pháp chuyển giao (Deployment)
- **Đóng gói:** Ứng dụng được đóng gói dưới dạng tệp Android Package (APK) hoặc Android App Bundle (AAB) thông qua Android Studio.
- **Phân phối:** 
    - Cài đặt trực tiếp qua tệp APK cho mục đích kiểm thử nội bộ.
    - Phát hành lên Google Play Store để tiếp cận người dùng cuối.
- **Cập nhật:** Hỗ trợ cơ chế cập nhật phiên bản mới thông qua cửa hàng ứng dụng, đảm bảo tính nhất quán của dữ liệu người dùng (Room database) khi nâng cấp.

---

## 7. Cấu hình hệ thống (Configuration)
- **Yêu cầu phần cứng:**
    - Thiết bị chạy hệ điều hành Android 7.0 (API 24) trở lên.
    - Bộ nhớ trống tối thiểu 50MB để lưu trữ ứng dụng và cơ sở dữ liệu nội bộ.
- **Cấu hình phần mềm:**
    - **Text-to-Speech:** Yêu cầu cài đặt Google Speech Services để có chất lượng âm thanh tốt nhất.
    - **Quyền hạn (Permissions):** Ứng dụng yêu cầu quyền truy cập Internet (để cập nhật dữ liệu nếu cần) và quyền rung (Vibrate) để tăng tương tác trong các bài tập.
- **Cơ sở dữ liệu:** Cấu hình tự động khởi tạo dữ liệu mẫu (Pre-populated Database) ngay từ lần đầu cài đặt để người dùng có thể học ngay lập tức.
