package com.pjh.nearbynote.nearbyNoteMainFunction.note.data


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val dao: NoteDao
) {

    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()

    suspend fun insertNote(note: NoteEntity) = dao.insert(note)

    suspend fun updateNote(note: NoteEntity) = dao.update(note)
    suspend fun getNoteById(id: Long): NoteEntity? = dao.getNoteById(id)
    suspend fun deleteNote(note: NoteEntity) = dao.delete(note)

    suspend fun getNoteByGeofenceId(geofenceId: String): NoteEntity? {
        return dao.getNotesByGeofence(geofenceId).firstOrNull()
    }

    suspend fun getNotesByLocation(note: String) = dao.getNotesByLocation(note)


}