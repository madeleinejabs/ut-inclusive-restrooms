package com.example.utinclusiverestrooms

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _uiState = MutableStateFlow(mutableListOf<Restroom>())
    val uiState: StateFlow<MutableList<Restroom>> = _uiState.asStateFlow()


    fun updateUiState(location: Location) {
        viewModelScope.launch {
            _uiState.value.clear()
            restroomRepository.sortRestrooms(location)
            for (i in 0..2) {
                _uiState.value.add(restroomRepository.getRestroom(i)!!)
            }
        }
    }

    val testString : String = "This feature requires location permission"
}