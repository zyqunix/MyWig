package com.zyqunix.mywig.ui.flashcard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zyqunix.mywig.databinding.FragmentFlashcardBinding

class FlashcardFragment : Fragment() {

    private var _binding: FragmentFlashcardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FlashcardViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlashcardBinding.inflate(inflater, container, false)

        // Observing LiveData from the ViewModel
        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            binding.questionTextView.text = question
        }

        viewModel.score.observe(viewLifecycleOwner) { score ->
            binding.scoreTextView.text = "Score: $score" // Adjust based on your string resources
        }

        binding.checkButton.setOnClickListener {
            val userAnswer = binding.answerEditText.text.toString()
            viewModel.checkAnswer(userAnswer, requireContext()) // Pass context here
            binding.answerEditText.text.clear()
        }

        binding.nextButton.setOnClickListener {
            viewModel.showNextQuestion()
        }

        binding.resetButton.setOnClickListener {
            viewModel.resetQuiz()
        }

        binding.addButton.setOnClickListener {
            val question = binding.questionEditText.text.toString().trim()
            val answer = binding.answerEditText.text.toString().trim()
            if (question.isNotEmpty() && answer.isNotEmpty()) {
                viewModel.addFlashcard(question, answer)
                Toast.makeText(requireContext(), "Flashcard added!", Toast.LENGTH_SHORT).show()
                binding.questionEditText.text.clear()
                binding.answerEditText.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please enter both question and answer.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
