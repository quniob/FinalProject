package com.example.finalproject

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.finalproject.databinding.FragmentHttpClientBinding

class HttpClientFragment : Fragment() {
    
    private var _binding: FragmentHttpClientBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HttpClientViewModel by viewModels {
        HttpClientViewModelFactory(HistoryRepository(requireContext()))
    }
    
    private val httpMethods = arrayOf("GET", "POST", "PUT", "DELETE", "PATCH")
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHttpClientBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupMethodSpinner()
        setupObservers()
        setupClickListeners()
        setupTextWatchers()
    }
    
    private fun setupMethodSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, httpMethods)
        binding.spinnerMethod.setAdapter(adapter)
        binding.spinnerMethod.setText("GET", false)
        
        binding.spinnerMethod.setOnItemClickListener { _, _, position, _ ->
            val selectedMethod = httpMethods[position]
            viewModel.method.value = selectedMethod
            
            val showBody = selectedMethod in arrayOf("POST", "PUT", "PATCH")
            binding.layoutRequestBody.visibility = if (showBody) View.VISIBLE else View.GONE
        }
    }
    
    private fun setupObservers() {
        viewModel.url.observe(viewLifecycleOwner) { url ->
            if (binding.etUrl.text.toString() != url) {
                binding.etUrl.setText(url)
            }
            updateSendButtonState()
        }
        
        viewModel.method.observe(viewLifecycleOwner) { method ->
            if (binding.spinnerMethod.text.toString() != method) {
                binding.spinnerMethod.setText(method, false)
            }
            val showBody = method in arrayOf("POST", "PUT", "PATCH")
            binding.layoutRequestBody.visibility = if (showBody) View.VISIBLE else View.GONE
        }
        
        viewModel.headers.observe(viewLifecycleOwner) { headers ->
            if (binding.etHeaders.text.toString() != headers) {
                binding.etHeaders.setText(headers)
            }
        }
        
        viewModel.requestBody.observe(viewLifecycleOwner) { body ->
            if (binding.etRequestBody.text.toString() != body) {
                binding.etRequestBody.setText(body)
            }
        }
        
        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                showResponse(response)
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSend.isEnabled = !isLoading && !binding.etUrl.text.isNullOrBlank()
        }
        
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSend.setOnClickListener {
            viewModel.sendRequest()
        }
        
        binding.btnClear.setOnClickListener {
            viewModel.clearAll()
            binding.cardResponse.visibility = View.GONE
        }
        
        binding.btnPresets.setOnClickListener {
            showPresetsMenu(it)
        }
        
        binding.btnCopyResponse.setOnClickListener {
            copyResponseToClipboard()
        }
    }
    
    private fun setupTextWatchers() {
        binding.etUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.url.value = s.toString()
            }
        })
        
        binding.etHeaders.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.headers.value = s.toString()
            }
        })
        
        binding.etRequestBody.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.requestBody.value = s.toString()
            }
        })
    }
    
    private fun showPresetsMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.http_presets_menu, popup.menu)
        
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.preset_get_json -> {
                    viewModel.setPresetRequest("GET_JSON")
                    true
                }
                R.id.preset_post_json -> {
                    viewModel.setPresetRequest("POST_JSON")
                    true
                }
                R.id.preset_get_github -> {
                    viewModel.setPresetRequest("GET_GITHUB")
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
    
    private fun showResponse(response: HttpClientViewModel.HttpResponse) {
        binding.cardResponse.visibility = View.VISIBLE
        
        binding.tvStatus.text = "${response.statusCode} ${response.statusMessage}"
        val statusColor = if (response.isSuccess) {
            ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
        } else {
            ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
        }
        binding.tvStatus.setTextColor(statusColor)
        
        binding.tvResponseTime.text = "${response.responseTime}ms"
        
        binding.tvResponseHeaders.text = response.headers
        
        binding.tvResponseBody.text = response.body
    }
    
    private fun copyResponseToClipboard() {
        val response = viewModel.response.value
        if (response != null) {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("HTTP Response", response.body)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Response copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateSendButtonState() {
        val hasUrl = !binding.etUrl.text.isNullOrBlank()
        val isNotLoading = viewModel.isLoading.value != true
        binding.btnSend.isEnabled = hasUrl && isNotLoading
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}