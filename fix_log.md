# Nhật ký sửa lỗi dự án APP_WordPulse

## 1. Cấu hình Gradle & Dependencies
- **Cập nhật `libs.versions.toml`**: 
    - Thêm các thư viện: `appcompat`, `activity-ktx`, `lifecycle-livedata-ktx`, `material`, `youtube-player-core`, `flexbox`.
    - Thêm cấu hình `room` và `ksp` (Kotlin Symbol Processing).
- **Cập nhật `app/build.gradle.kts`**:
    - **Sửa lỗi nghiêm trọng**: Xóa dòng `val LibrariesForLibs.google: Any` gây lỗi "Extension property must have accessors or be abstract".
    - Áp dụng plugin `ksp`.
    - Khai báo các dependencies mới từ version catalog, bao gồm `libs.flexbox`.
    - Sửa lỗi truy cập `libs.androidx.lifecycle.livedata.ktx` (thay dấu `-` bằng `.`).
    - Xóa dependency lỗi `libs.google.material` (đã có `libs.material`).
- **Cập nhật `build.gradle.kts` (root)**:
    - Thêm plugin `ksp` vào block plugins.

## 2. Android Manifest
- Đăng ký đầy đủ các Activity vào `AndroidManifest.xml` để tránh crash khi điều hướng:
    - `LoginActivity`
    - `DictationActivity`
    - `LessonListActivity` (Mới thêm)
    - `QuizActivity`
    - `GrammarActivity`
    - `StoryListActivity`
    - `FlashcardActivity`
    - `VocabTopicActivity`

## 3. Giao diện (Resources)
- **Sửa lỗi Resource Linking Failed**: Tạo hàng loạt tài nguyên bị thiếu trong `activity_main.xml`:
    - **Drawables**: 
        - `bg_header_gradient.xml`: Nền gradient cho header.
        - `bg_progressbar.xml`: Tùy chỉnh thanh tiến độ.
        - `bg_category_flashcard.xml`: Nền cho danh mục flashcard.
        - `bg_top_orange.xml`, `bg_top_blue.xml`, `bg_top_green.xml`, `bg_top_pink.xml`: Nền màu cho các thẻ card chức năng.
        - `bg_border_selector.xml`: Hiệu ứng ripple và viền cho các nút chọn chế độ học.
    - **Icons (Vector)**: 
        - `ic_avatar_dummy.xml`, `ic_notification.xml`, `ic_progress_dummy.xml`, `ic_flashcards.xml`.
    - **Colors**:
        - `res/color/selector_nav_color.xml`: Đổi màu icon/text khi chọn item ở thanh Bottom Navigation.
    - **Menus**:
        - `res/menu/bottom_nav_menu.xml`: Danh sách các mục cho thanh điều hướng dưới cùng.
- **`activity_dictation.xml`**: Sửa lỗi namespace `xmlns:android` bị dính ký tự lạ (`বরা/android`).
- **`themes.xml`**: Chuyển từ `Theme.Material.Light.NoActionBar` sang `Theme.Material3.DayNight.NoActionBar`.

## 4. Logic Xử lý (Kotlin)
- **`MainActivity.kt`**:
    - Chuyển từ giao diện cũ sang `setContentView(R.layout.activity_main)`.
    - Ánh xạ và thiết lập sự kiện Click cho tất cả các nút: `btnVocab`, `btnListening` (mở `LessonListActivity`), `btnStories`, `btnGrammar`.
    - Triển khai `BottomNavigationView.setOnItemSelectedListener` để điều hướng giữa các tab.
    - Sửa lỗi thiếu Import cho `Intent`, `DictationActivity`, v.v.
- **`DictationActivity.kt`**:
    - Fix lỗi thiếu import cho `Chip` và `ChipGroup`.
    - Thêm code tự động tạo `Chip` và thêm vào `ChipGroup`.
- **`DictationViewModel.kt`**:
    - Cải thiện hàm `checkAnswer`: Sửa Regex từ `[^a-zA-Z0-dict]` thành `[^a-z0-9]`.
- **`LessonListActivity.kt`**:
    - Cấu hình điều hướng và chuẩn bị logic cho hộp thoại chọn chế độ (`showModeSelectionDialog`).

## 5. Trạng thái hiện tại
- **Build**: Thành công (`assembleDebug`).
- **Runtime**: Ứng dụng có thể chạy, điều hướng trơn tru giữa màn hình chính và các tính năng con.
- **UI**: Hiển thị đầy đủ màu sắc, icon và layout (bao gồm cả Flexbox).

## 6. Danh sách các file mới và Workflow
- **File mới thêm**:
    - `fix_log.md`: Lưu trữ lịch sử sửa lỗi.
    - Hơn 10 file drawable và menu để hoàn thiện UI.
- **Workflow chuẩn**:
    1. Kiểm tra build log -> Xác định Resource/Dependency thiếu.
    2. Cập nhật `libs.versions.toml` và `build.gradle.kts`.
    3. Tạo Resource XML (Drawable/Layout/Menu).
    4. Code logic Kotlin (Activity/ViewModel/Intent).
    5. Đăng ký Activity vào Manifest.
    6. Build & Verify.
