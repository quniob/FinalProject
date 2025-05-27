package com.example.finalproject

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.FragmentRegexTesterBinding

class RegexTesterFragment : Fragment() {
    
    private var _binding: FragmentRegexTesterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegexTesterViewModel
    private lateinit var matchesAdapter: MatchesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegexTesterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val repository = HistoryRepository(requireContext())
        viewModel = ViewModelProvider(this, RegexTesterViewModelFactory(repository))[RegexTesterViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupRecyclerView() {
        matchesAdapter = MatchesAdapter { match ->
            copyToClipboard(match)
        }
        binding.rvMatches.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = matchesAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.matches.observe(viewLifecycleOwner) { matches ->
            matchesAdapter.submitList(matches)
            binding.rvMatches.visibility = if (matches.isNotEmpty()) View.VISIBLE else View.GONE
            binding.cardNoMatches.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnTest.setOnClickListener {
            val pattern = binding.etRegex.text.toString()
            val text = binding.etTestText.text.toString()
            viewModel.testRegex(pattern, text)
        }
        
        binding.btnClear.setOnClickListener {
            binding.etRegex.text?.clear()
            binding.etTestText.text?.clear()
            viewModel.clearResults()
        }
        
        binding.btnExamples.setOnClickListener {
            showExamplesDialog()
        }
        
        binding.etRegex.addTextChangedListener {
            updateTestButtonState()
        }
        
        binding.etTestText.addTextChangedListener {
            updateTestButtonState()
        }
    }
    
    private fun showExamplesDialog() {
        val examples = arrayOf(
            "Email: [a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            "Phone: \\+?[1-9]\\d{1,14}",
            "URL: https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?",
            "Date (YYYY-MM-DD): \\d{4}-\\d{2}-\\d{2}",
            "Time (HH:MM): \\d{2}:\\d{2}",
            "IP Address: \\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b"
        )
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Regex Examples")
            .setItems(examples) { _, which ->
                val pattern = examples[which].substringAfter(": ")
                binding.etRegex.setText(pattern)
            }
            .show()
    }
    
    private fun updateTestButtonState() {
        val hasPattern = !binding.etRegex.text.isNullOrEmpty()
        val hasText = !binding.etTestText.text.isNullOrEmpty()
        binding.btnTest.isEnabled = hasPattern && hasText
    }
    
    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Regex Match", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Match copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}