package com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressEntity
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedAddressViewModel @Inject constructor(
    private val repository: SavedAddressRepository
) : ViewModel() {

    private val _savedAddresses = MutableStateFlow<List<SavedAddressEntity>>(emptyList())
    val savedAddresses: StateFlow<List<SavedAddressEntity>> = _savedAddresses

    init {
        viewModelScope.launch {
            repository.getAll().collectLatest {
                _savedAddresses.value = it
            }
        }
    }

    fun saveAddress(name: String, placeName: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            repository.save(
                SavedAddressEntity(
                    name = name,
                    placeName = placeName,
                    latitude = lat,
                    longitude = lng
                )
            )
        }
    }

    fun deleteAddress(address: SavedAddressEntity) {
        viewModelScope.launch {
            repository.delete(address)
        }
    }

    fun isDuplicateAddress(placeName: String, lat: Double, lng: Double): Boolean {
        return savedAddresses.value.any {
            it.placeName == placeName && it.latitude == lat && it.longitude == lng
        }
    }
}