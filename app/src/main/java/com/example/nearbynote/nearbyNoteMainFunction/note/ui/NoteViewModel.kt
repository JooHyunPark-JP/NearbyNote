package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository

import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val mapboxRepository: MapboxRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes

    var addressQuery by mutableStateOf("")
    var suggestions by mutableStateOf(emptyList<AddressSuggestion>())

    init {
        viewModelScope.launch {
            noteRepository.allNotes.collectLatest {
                _notes.value = it
            }
        }
    }

    fun saveNote(content: String, geofenceId: String?, locationName: String?, isVoice: Boolean) {
        viewModelScope.launch {
            noteRepository.insertNote(
                NoteEntity(
                    content = content,
                    geofenceId = geofenceId,
                    locationName = locationName,
                    isVoice = isVoice
                )
            )
        }
    }

    fun onQueryChanged(query: String) {
        addressQuery = query
        if (query.length >= 3) {
            viewModelScope.launch {
                suggestions = mapboxRepository.fetchAddressSuggestions(query)
            }
        }
    }
}