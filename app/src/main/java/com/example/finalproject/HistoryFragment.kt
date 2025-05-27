package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.FragmentHistoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistoryFragment : Fragment() {
    
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val repository = HistoryRepository(requireContext())
        viewModel = ViewModelProvider(this, HistoryViewModelFactory(repository))[HistoryViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        viewModel.loadHistory()
    }
    
    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { historyItem ->
            showDeleteConfirmationDialog(historyItem)
        }
        
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.historyItems.observe(viewLifecycleOwner) { items ->
            historyAdapter.submitList(items)
            updateEmptyState(items.isEmpty())
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Error")
                    .setMessage(error)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnClearHistory.setOnClickListener {
            showClearHistoryConfirmationDialog()
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.rvHistory.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.cardEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
    
    private fun showDeleteConfirmationDialog(item: HistoryItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete History Item")
            .setMessage("Are you sure you want to delete this ${item.toolName} history item?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteHistoryItem(item)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showClearHistoryConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear All History")
            .setMessage("Are you sure you want to delete all history items? This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                viewModel.clearHistory()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}