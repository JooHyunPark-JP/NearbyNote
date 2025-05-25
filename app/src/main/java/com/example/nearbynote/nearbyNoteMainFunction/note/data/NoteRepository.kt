package com.example.nearbynote.nearbyNoteMainFunction.note.data


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val dao: NoteDao
) {

    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()

    suspend fun insertNote(note: NoteEntity) = dao.insert(note)
}