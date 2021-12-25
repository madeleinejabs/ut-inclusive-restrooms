package com.example.utinclusiverestrooms

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RestroomViewModel @Inject constructor (
    private val restroomRepository: RestroomRepository
): ViewModel() {
    private val restrooms = mutableListOf<Restroom>()
    private val _uiState = MutableStateFlow(emptyList<Restroom>())
    val uiState: StateFlow<List<Restroom>> = _uiState.asStateFlow()


    fun updateUiState(location: Location, num: Int) {
        viewModelScope.launch {
            restroomRepository.sortRestrooms(location)
            restrooms.clear()
            for (i in 0 until num) {
                restrooms.add(restroomRepository.getRestroom(i)!!)
            }
            _uiState.value = Collections.unmodifiableList(restrooms)
        }
    }
}