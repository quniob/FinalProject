package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.cardUuid.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_uuid)
        }
        
        binding.cardHash.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_hash)
        }
        
        binding.cardRegex.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_regex)
        }
        
        binding.cardBase64.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_base64)
        }
        
        binding.cardHistory.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_history)
        }
        
        binding.cardHttpClient.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_http_client)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}