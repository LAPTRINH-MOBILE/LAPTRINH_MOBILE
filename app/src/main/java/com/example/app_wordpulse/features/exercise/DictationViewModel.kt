package com.example.app_wordpulse.features.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.app_wordpulse.data.model.DictationLesson
import com.example.app_wordpulse.data.model.TranscriptLine

class DictationViewModel : ViewModel() {

    private val _lessonData = MutableLiveData<DictationLesson>()
    val lessonData: LiveData<DictationLesson> = _lessonData

    private val _currentSentenceIndex = MutableLiveData<Int>(0)
    val currentSentenceIndex: LiveData<Int> = _currentSentenceIndex

    // Giả lập nạp dữ liệu (sau này thay bằng gọi API thực tế qua ApiService)
    fun loadDummyData() {
        val lines = listOf(
            TranscriptLine(1, 0.0f, 4.5f, "From Academy Award winning director"),
            TranscriptLine(2, 4.6f, 8.0f, "Hayao Miyazaki"),
            TranscriptLine(3, 8.1f, 13.5f, "Comes a brand new magical adventure")
        )
        _lessonData.value = DictationLesson("kiki_id", "Kiki's Delivery Service", "Hard", lines)
    }

    fun moveToNextSentence() {
        val total = _lessonData.value?.transcripts?.size ?: 0
        val current = _currentSentenceIndex.value ?: 0
        if (current < total - 1) {
            _currentSentenceIndex.value = current + 1
        }
    }

    fun setCurrentSentenceIndex(index: Int) {
        _currentSentenceIndex.value = index
    }

    // Biến đổi từ "Hello!" thành "*****!"
    fun convertToMaskedText(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.map { if (it.isLetterOrDigit()) '*' else it }.joinToString("")
        }
    }

    // Tách chuỗi thành mảng các từ dạng ẩn để đưa vào ChipGroup gợi ý câu đang gõ
    fun getMaskedWordList(text: String): List<String> {
        return text.split(" ").map { word ->
            word.map { if (it.isLetterOrDigit()) '*' else it }.joinToString("")
        }
    }

    // Logic so khớp chuỗi chuẩn: Xóa khoảng trắng thừa, xóa kí tự đặc biệt, đưa về chữ thường
    fun isAnswerCorrect(userInput: String, correctAnswer: String): Boolean {
        val regex = Regex("[^a-zA-Z0-9]")
        val cleanUser = userInput.replace(regex, "").lowercase().trim()
        val cleanCorrect = correctAnswer.replace(regex, "").lowercase().trim()
        return cleanUser == cleanCorrect
    }
}