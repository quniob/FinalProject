package com.example.finalproject

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.databinding.FragmentUuidGeneratorBinding

class UuidGeneratorFragment : Fragment() {
    
    private var _binding: FragmentUuidGeneratorBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UuidGeneratorViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUuidGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val repository = HistoryRepository(requireContext())
        viewModel = ViewModelProvider(this, UuidGeneratorViewModelFactory(repository))[UuidGeneratorViewModel::class.java]
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewModel.generatedUuid.observe(viewLifecycleOwner) { uuid ->
            binding.tvGeneratedUuid.text = uuid
            binding.btnCopy.isEnabled = true
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupClickListeners() {
        binding.btnGenerate.setOnClickListener {
            viewModel.generateUuid()
        }
        
        binding.btnGenerateUppercase.setOnClickListener {
            viewModel.generateUppercaseUuid()
        }
        
        binding.btnGenerateNoDashes.setOnClickListener {
            viewModel.generateUuidWithoutDashes()
        }
        
        binding.btnCopy.setOnClickListener {
            copyToClipboard()
        }
    }
    
    private fun copyToClipboard() {
        val uuid = binding.tvGeneratedUuid.text.toString()
        if (uuid.isNotEmpty() && uuid != "Click generate to create UUID") {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("UUID", uuid)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "UUID copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}