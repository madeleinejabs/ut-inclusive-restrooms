package com.example.utinclusiverestrooms

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
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


    fun updateUiState(location: Location) {
        viewModelScope.launch {
            restroomRepository.sortRestrooms(location)
            Log.d("RestroomViewModel", "After sortRestrooms")
            restrooms.clear()
            for (i in 0..2) {
                restrooms.add(restroomRepository.getRestroom(i)!!)
            }
            _uiState.value = Collections.unmodifiableList(restrooms)
        }
    }

    val testString : String = "This feature requires location permission"
}