package com.example.utinclusiverestrooms.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.utinclusiverestrooms.data.Restroom
import com.example.utinclusiverestrooms.data.RestroomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestroomViewModel @Inject constructor (
    private val restroomRepository: RestroomRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(emptyList<Restroom>())
    val uiState: StateFlow<List<Restroom>> = _uiState.asStateFlow()


    fun updateUiState(location: Location) {
        viewModelScope.launch {
            restroomRepository.sortRestrooms(location)
            _uiState.value = restroomRepository.getRestrooms()!!
        }
    }
}