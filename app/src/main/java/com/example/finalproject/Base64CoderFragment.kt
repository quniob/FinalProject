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
import com.example.finalproject.databinding.FragmentBase64CoderBinding

class Base64CoderFragment : Fragment() {
    
    private var _binding: FragmentBase64CoderBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: Base64CoderViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBase64CoderBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val repository = HistoryRepository(requireContext())
        viewModel = ViewModelProvider(this, Base64CoderViewModelFactory(repository))[Base64CoderViewModel::class.java]
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.outputText.observe(viewLifecycleOwner) { output ->
            binding.tvOutput.text = output
            binding.btnCopy.isEnabled = output.isNotEmpty()
            binding.btnSwap.isEnabled = output.isNotEmpty()
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnEncode.setOnClickListener {
            val input = binding.etInput.text.toString()
            viewModel.encodeText(input)
        }
        
        binding.btnDecode.setOnClickListener {
            val input = binding.etInput.text.toString()
            viewModel.decodeText(input)
        }
        
        binding.btnClear.setOnClickListener {
            binding.etInput.text?.clear()
            viewModel.clearOutput()
        }
        
        binding.btnCopy.setOnClickListener {
            copyToClipboard()
        }
        
        binding.btnSwap.setOnClickListener {
            swapInputOutput()
        }
        
        binding.etInput.addTextChangedListener {
            val hasText = !it.isNullOrEmpty()
            binding.btnEncode.isEnabled = hasText
            binding.btnDecode.isEnabled = hasText
        }
    }
    
    private fun swapInputOutput() {
        binding.etInput.text.toString()
        val output = binding.tvOutput.text.toString()
        
        if (output.isNotEmpty()) {
            binding.etInput.setText(output)
            viewModel.clearOutput()
        }
    }
    
    private fun copyToClipboard() {
        val text = binding.tvOutput.text.toString()
        if (text.isNotEmpty()) {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Base64 Result", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}