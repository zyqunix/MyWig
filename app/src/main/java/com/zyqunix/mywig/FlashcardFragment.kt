package com.zyqunix.mywig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.zyqunix.mywig.databinding.FragmentFlashcardBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class FlashcardFragment : Fragment() {
    private var _binding: FragmentFlashcardBinding? = null
    private val binding get() = _binding!!

    private var flashcards = mutableListOf<Flashcard>()
    private var currentCardIndex = -1
    private var score = 0
    private val jsonFile = "flashcards.json"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlashcardBinding.inflate(inflater, container, false)

        loadFlashcards()
        initializeViews()
        showNextQuestion()

        return binding.root
    }

    private fun initializeViews() {
        binding.addButton.setOnClickListener { addFlashcard() }
        binding.checkButton.setOnClickListener { checkAnswer() }
        binding.nextButton.setOnClickListener { showNextQuestion() }
        binding.resetButton.setOnClickListener { resetQuiz() }
    }

    private fun addFlashcard() {
        val question = binding.questionEditText.text.toString().trim()
        val answer = binding.answerEditText.text.toString().trim()
        if (question.isNotEmpty() && answer.isNotEmpty()) {
            flashcards.add(Flashcard(question, answer))
            saveFlashcards()
            Toast.makeText(requireContext(), getString(R.string.flashcard_added), Toast.LENGTH_SHORT).show()
            binding.questionEditText.text.clear()
            binding.answerEditText.text.clear()
        } else {
            Toast.makeText(requireContext(), getString(R.string.flashcard_input_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveFlashcards() {
        val jsonArray = JSONArray()
        for (flashcard in flashcards) {
            val jsonObject = flashcard.toJson()
            jsonArray.put(jsonObject)
        }
        requireContext().openFileOutput(jsonFile, 0).use { it.write(jsonArray.toString().toByteArray()) }
    }

    private fun loadFlashcards() {
        val file = File(requireContext().filesDir, jsonFile)
        if (file.exists()) {
            val jsonArray = JSONArray(file.readText())
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                flashcards.add(Flashcard(jsonObject.getString("question"), jsonObject.getString("answer")))
            }
        }
    }

    private fun showNextQuestion() {
        if (flashcards.isNotEmpty()) {
            currentCardIndex = (currentCardIndex + 1) % flashcards.size
            binding.questionTextView.text = flashcards[currentCardIndex].question
            binding.answerEditText.text.clear()
            binding.scoreTextView.text = "" // Clear score text
        } else {
            Toast.makeText(requireContext(), getString(R.string.flashcard_no_cards), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAnswer() {
        if (currentCardIndex >= 0) {
            val userAnswer = binding.answerEditText.text.toString().trim().lowercase()
            val correctAnswer = flashcards[currentCardIndex].answer.lowercase()
            if (userAnswer == correctAnswer) {
                score++
                binding.scoreTextView.text = getString(R.string.flashcard_correct_score, score)
            } else {
                binding.scoreTextView.text = getString(R.string.flashcard_incorrect_answer, correctAnswer)
            }
        }
    }

    private fun resetQuiz() {
        score = 0
        currentCardIndex = -1
        showNextQuestion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Flashcard(val question: String, val answer: String) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("question", question)
            put("answer", answer)
        }
    }
}
