package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes

    init {
        viewModelScope.launch {
            repository.allNotes.collectLatest {
                _notes.value = it
            }
        }
    }

    fun saveNote(content: String, geofenceId: String?, locationName: String?, isVoice: Boolean) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    content = content,
                    geofenceId = geofenceId,
                    locationName = locationName,
                    isVoice = isVoice
                )
            )
        }
    }
}