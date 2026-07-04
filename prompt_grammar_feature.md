# Prompt: Implement tính năng "Viết và Ngữ pháp (Dịch Việt - Anh)" — app_wordpulse

## Bối cảnh dự án

Đây là app Android học tiếng Anh (Kotlin, native Android, không dùng Jetpack Compose cho phần này — dùng Activity + XML layout truyền thống). Package root: `com.example.app_wordpulse`.

Project đang dùng **2 kiểu lưu trữ data song song**:
1. **Room** cho entity `Word` (từ vựng) — có `@Entity`, `AppDatabase`, `WordDao`.
2. **Raw SQLite** cho `topic` và `story` (nằm trong file `dataEl.db` có sẵn) — query bằng `rawQuery`, không dùng Room annotation.

## Schema database hiện có (trong `dataEl.db`)

```sql
CREATE TABLE "topic" (
    "id" INTEGER PRIMARY KEY,
    "level" TEXT,
    "topic_name" TEXT
);

CREATE TABLE "story" (
    "id" INTEGER PRIMARY KEY,
    "topic_id" INTEGER,
    "story_content" TEXT,
    "question" TEXT,
    "option_a" TEXT,
    "option_b" TEXT,
    "option_c" TEXT,
    "option_d" TEXT,
    "correct_answer" TEXT,
    "explanation_vi" TEXT,
    FOREIGN KEY("topic_id") REFERENCES "topic"("id")
);
```

## Task cần làm

Implement đầy đủ tính năng **Viết và Ngữ pháp (dịch câu Việt → Anh)**. Database đã có sẵn, KHÔNG cần tạo hay migrate gì thêm — chỉ code phần đọc/hiển thị/xử lý.

### 1. Database đã có sẵn (đã làm tay, KHÔNG cần đụng tới)

Table `grammar_exercise` đã tồn tại trong `dataEl.db`, có sẵn **100 dòng data** thật:

```sql
CREATE TABLE "grammar_exercise" (
    "id" INTEGER PRIMARY KEY,
    "topic_id" INTEGER,
    "level" TEXT,
    "vietnamese_sentence" TEXT,
    "correct_answer" TEXT,
    "grammar_topic" TEXT,
    "explanation_vi" TEXT,
    FOREIGN KEY("topic_id") REFERENCES "topic"("id")
);
```

Data mẫu thực tế (để hiểu format khi code phần hiển thị/parse):
```
(1, 1, 'A1', 'Tôi có hai anh trai.', 'I have two older brothers.', 'Present Simple', 'Dùng thì hiện tại đơn để nói về sự thật, mối quan hệ gia đình không thay đổi theo thời gian.')
```

Các giá trị `level` hiện có: `A1, A2, B1, B2`.
Các giá trị `grammar_topic` hiện có: `Present Simple, Present Continuous, Past Simple, Present Perfect, Present Perfect Continuous, First Conditional, Second Conditional, Third Conditional, Passive Voice`.

**Lưu ý quan trọng:** `topic_id` ở đây trỏ về `topic.id` (table `topic` có sẵn, dùng chung với phần Story) — khi code, không tự tạo thêm cột hay đổi tên cột nào trong `grammar_exercise`, chỉ đọc đúng như schema trên.

### 2. Model — `GrammarExercise.kt`

Đặt trong `data/model/`, theo style của `DictationLesson.kt` (data class thường, KHÔNG dùng Room `@Entity` vì bảng này nằm trong raw SQLite DB):

```kotlin
data class GrammarExercise(
    val id: Int,
    val topicId: Int,
    val level: String,
    val vietnameseSentence: String,
    val correctAnswer: String,
    val grammarTopic: String,
    val explanationVi: String
)
```

### 3. DAO — `GrammarDbHelper.kt`

Đặt trong `data/local/database/`, theo pattern raw SQLite giống `AuthDbHelper.kt`. Cần các hàm:
- `getExercisesByTopic(topicId: Int): List<GrammarExercise>`
- `getExerciseById(id: Int): GrammarExercise?`
- `getAllExercises(): List<GrammarExercise>` (dùng cho màn hình chọn bài ngẫu nhiên)

### 4. ViewModel — `GrammarViewModel.kt`

Hiện tại đang có sẵn (cần sửa lại):
```kotlin
class GrammarViewModel : ViewModel() {
    fun checkGrammar(input: String): Boolean {
        return input.isNotEmpty()
    }
}
```
Sửa thành:
- Load list `GrammarExercise` theo `topicId` (qua `GrammarDbHelper`).
- Giữ state: exercise hiện tại (dùng `LiveData` hoặc `StateFlow`, chọn 1 kiểu và dùng xuyên suốt).
- Hàm `checkGrammar(userInput: String, correctAnswer: String): Boolean` — so sánh string, bỏ khoảng trắng dư và không phân biệt hoa/thường (`trim().equals(ignoreCase = true)`).
- Hàm `nextExercise()` để chuyển câu tiếp theo.
- Optional: track số câu đúng/sai trong session (dùng cho hiển thị điểm cuối bài).

### 5. Activity — `GrammarActivity.kt`

Hiện tại đang có sẵn (chỉ mới `setContentView`, chưa có logic):
```kotlin
class GrammarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grammar)
    }
}
```
Cần bổ sung:
- Nhận `topicId` qua `Intent` extra (từ màn hình chọn topic, giống flow của `StoryListActivity` → `StoryDetailFragment`).
- Hiển thị `vietnameseSentence` lên `TextView`.
- 1 `EditText` cho user nhập câu tiếng Anh.
- 1 `Button` "Kiểm tra" — gọi `viewModel.checkGrammar()`, hiển thị kết quả đúng/sai (đổi màu border `EditText` hoặc hiện `Toast`/`TextView` thông báo).
- Hiển thị `explanationVi` sau khi user bấm kiểm tra (dù đúng hay sai, để giải thích rõ ngữ pháp).
- 1 `Button` "Câu tiếp theo" để load câu mới.
- Dùng `ViewModel` qua `by viewModels()` (chuẩn Android KTX), KHÔNG tự new instance trực tiếp.

### 6. Layout — `activity_grammar.xml`

Dùng `ConstraintLayout` (đồng bộ với các layout khác trong `res/layout/`), gồm:
- `TextView` hiển thị câu tiếng Việt (font lớn, căn giữa)
- `EditText` nhập câu dịch (multi-line nếu cần)
- `Button` "Kiểm tra"
- `TextView` hiển thị kết quả + giải thích (`explanationVi`), ẩn/hiện tùy trạng thái
- `Button` "Câu tiếp theo"

#### Color palette dùng cho phần Grammar

Thêm các màu sau vào `res/values/colors.xml` (nếu chưa có) và áp dụng vào layout:

| Tên resource | Hex | Vai trò |
|---|---|---|
| `anti_flash_white` | `#EFEFEF` | Background chính của Activity |
| `non_photo_blue` | `#B1E6F3` | Background của card/box chứa câu tiếng Việt |
| `sky_blue` | `#72DDF7` | Border/accent của `EditText` khi focus, hoặc trạng thái "đang làm bài" |
| `argentinian_blue` | `#79B8F4` | Màu chính của `Button` "Kiểm tra" / "Câu tiếp theo" |
| `vista_blue` | `#8093F1` | Màu nhấn cho `TextView` giải thích (`explanationVi`) hoặc trạng thái đúng |

Gợi ý áp dụng: gradient nhẹ từ `non_photo_blue` → `vista_blue` cho phần card hiển thị câu hỏi (tạo cảm giác chuyển màu như bảng màu gốc), text màu đen/dark gray để đảm bảo contrast dễ đọc trên các nền màu sáng này.

## Yêu cầu bắt buộc khi code

- Giữ nguyên convention hiện tại của project: package path, naming (camelCase cho Kotlin, snake_case cho SQL/XML id).
- Không phá vỡ code hiện có ở `Word.kt`, `AppDatabase.kt`, `WordDao.kt`, `ApiService.kt` — chỉ thêm mới, không sửa các entity Room hiện tại.
- KHÔNG dùng Room cho `GrammarExercise` — bảng này sống trong `dataEl.db` (raw SQLite), tách biệt hoàn toàn khỏi Room DB của `Word`.
- Code phải compile được với Kotlin + AndroidX, minSdk theo `build.gradle.kts` hiện tại của module `app`.
- Có comment ngắn giải thích các đoạn logic quan trọng (đặc biệt phần so sánh đáp án và load data từ SQLite).

## Output mong muốn

Trả về đầy đủ code cho:
1. `GrammarExercise.kt`
2. `GrammarDbHelper.kt`
3. `GrammarViewModel.kt` (bản đã sửa)
4. `GrammarActivity.kt` (bản đã sửa)
5. `activity_grammar.xml`
6. SQL script tạo table `grammar_exercise` + data mẫu insert

## Rules bổ sung (bắt buộc tuân theo)

- **Chỉ kết nối và chỉnh sửa** — không tạo file mới trừ khi thật sự cần thiết cho chức năng cuối cùng (ví dụ `GrammarDbHelper.kt` là cần vì chưa tồn tại, nhưng đừng tạo thêm file phụ không cần thiết).
- **Không tạo markdown vô tội vạ** — nếu cần ghi chú/note gì, chỉnh sửa vào file `.md` đã có sẵn trong project (ví dụ `fix_log.md`), không tạo thêm file `.md` mới.
- **Cleanup sau khi hoàn thành** — nếu trong quá trình implement có tạo file debug/test tạm (ví dụ `debug_*.py`, `test_*.kt` tạm, script generate data tạm...), phải xóa/loại bỏ khỏi kết quả cuối cùng, không để sót lại trong project.
- **Không đụng tới file của các phần khác** trừ khi nó liên quan trực tiếp tới phần Grammar này (ví dụ: không sửa `WordDao.kt`, `AuthDbHelper.kt`, `StoryViewModel.kt`... nếu không có lý do liên quan).
- **Giữ đúng tên class/package để navigate đúng** — `GrammarActivity` phải giữ nguyên tên class và đúng package `com.example.app_wordpulse.features.grammar` như hiện tại (không đổi tên, không di chuyển file sang package khác), vì `AndroidManifest.xml` đang khai báo Activity này theo đúng path đó, và nơi nào đang gọi qua (ví dụ `MainActivity` hoặc màn hình chọn topic) cũng đang trỏ Intent tới đúng class này. Nếu cần thêm Intent extra key (ví dụ `topic_id`) để truyền dữ liệu vào, phải dùng đúng tên key đó ở cả nơi gửi (Intent tạo ra) và nơi nhận (`GrammarActivity.onCreate()`), tránh sai lệch dẫn đến crash hoặc không nhận được data.
