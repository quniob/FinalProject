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
import com.example.finalproject.databinding.FragmentHashGeneratorBinding

class HashGeneratorFragment : Fragment() {
    
    private var _binding: FragmentHashGeneratorBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HashGeneratorViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHashGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val repository = HistoryRepository(requireContext())
        viewModel = ViewModelProvider(this, HashGeneratorViewModelFactory(repository))[HashGeneratorViewModel::class.java]
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.md5Hash.observe(viewLifecycleOwner) { hash ->
            binding.tvMd5Hash.text = hash
            updateCopyButtonsState()
        }
        
        viewModel.sha1Hash.observe(viewLifecycleOwner) { hash ->
            binding.tvSha1Hash.text = hash
            updateCopyButtonsState()
        }
        
        viewModel.sha256Hash.observe(viewLifecycleOwner) { hash ->
            binding.tvSha256Hash.text = hash
            updateCopyButtonsState()
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnGenerate.setOnClickListener {
            val input = binding.etInput.text.toString()
            viewModel.generateHashes(input)
        }
        
        binding.btnClear.setOnClickListener {
            binding.etInput.text?.clear()
            viewModel.clearHashes()
        }
        
        binding.btnCopyMd5.setOnClickListener {
            copyToClipboard(binding.tvMd5Hash.text.toString(), "MD5")
        }
        
        binding.btnCopySha1.setOnClickListener {
            copyToClipboard(binding.tvSha1Hash.text.toString(), "SHA1")
        }
        
        binding.btnCopySha256.setOnClickListener {
            copyToClipboard(binding.tvSha256Hash.text.toString(), "SHA256")
        }
        
        binding.etInput.addTextChangedListener {
            val hasText = !it.isNullOrEmpty()
            binding.btnGenerate.isEnabled = hasText
        }
    }
    
    private fun updateCopyButtonsState() {
        val hasHashes = binding.tvMd5Hash.text.isNotEmpty()
        binding.btnCopyMd5.isEnabled = hasHashes
        binding.btnCopySha1.isEnabled = hasHashes
        binding.btnCopySha256.isEnabled = hasHashes
    }
    
    private fun copyToClipboard(text: String, type: String) {
        if (text.isNotEmpty()) {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("$type Hash", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "$type hash copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}