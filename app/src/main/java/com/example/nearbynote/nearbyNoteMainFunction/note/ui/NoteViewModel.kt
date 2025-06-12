package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceEntity
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository

import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val mapboxRepository: MapboxRepository,

    ) : ViewModel() {

    private val _notes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notes: StateFlow<List<NoteEntity>> = _notes

    var addressQuery by mutableStateOf("")
    var addressLongitude by mutableDoubleStateOf(0.0)
    var addressLatitude by mutableDoubleStateOf(0.0)
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

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }
    }

    suspend fun getNoteById(id: Long): NoteEntity? {
        return noteRepository.getNoteById(id)
    }

    fun updateNoteWithGeofence(
        noteId: Long,
        content: String,
        geofenceEntity: GeofenceEntity?,
        geofenceManager: GeofenceManager,
        geofenceViewModel: GeofenceViewModel,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            val oldNote = getNoteById(noteId)
            if (oldNote == null) {
                onFailure(IllegalStateException("Note not found"))
                return@launch
            }

            if (geofenceEntity == null) {
                // No geofence. Just update note.
                updateNote(
                    oldNote.copy(
                        content = content,
                        geofenceId = null,
                        locationName = null,
                        isVoice = false
                    )
                )
                onSuccess()
                return@launch
            }


            val finalGeofenceId = oldNote.geofenceId ?: geofenceEntity.id.ifBlank {
                UUID.randomUUID().toString()
            }

            if (oldNote.geofenceId == null) {
                // Add geofence
                geofenceManager.addGeofence(
                    geofenceId = finalGeofenceId,
                    latitude = geofenceEntity.latitude,
                    longitude = geofenceEntity.longitude,
                    radius = geofenceEntity.radius,
                    onSuccess = {
                        //Add geofence to room database
                        geofenceViewModel.saveGeofenceToDb(
                            id = finalGeofenceId,
                            name = geofenceEntity.name,
                            lat = geofenceEntity.latitude,
                            lng = geofenceEntity.longitude,
                            radius = geofenceEntity.radius
                        )
                        updateNote(
                            oldNote.copy(
                                content = content,
                                geofenceId = finalGeofenceId,
                                locationName = geofenceEntity.name,
                                isVoice = false
                            )
                        )
                        onSuccess()
                    },
                    onFailure = { onFailure(it) }
                )
            } else {
                updateNote(
                    oldNote.copy(
                        content = content,
                        locationName = geofenceEntity.name,
                        isVoice = false
                    )
                )
                onSuccess()
            }
        }
    }


}