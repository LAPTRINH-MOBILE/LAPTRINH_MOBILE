package com.example.app_wordpulse.features.grammar

import androidx.lifecycle.ViewModel

class GrammarViewModel : ViewModel() {
    fun checkGrammar(input: String): Boolean {
        return input.isNotEmpty()
    }
}
