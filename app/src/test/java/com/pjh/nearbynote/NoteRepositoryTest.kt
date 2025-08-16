package com.pjh.nearbynote

import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteDao
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteRepositoryTest {

    private lateinit var dao: NoteDao
    private lateinit var repository: NoteRepository

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        repository = NoteRepository(dao)
    }

    @Test
    fun insertNote_callsDaoInsert() = runTest {
        val note =
            NoteEntity(content = "Test", isVoice = false, geofenceId = null, locationName = null)

        repository.insertNote(note)

        coVerify { dao.insert(note) }
    }

    @Test
    fun getAllNotes_returnsFlowFromDao() = runTest {
        val fakeNotes = listOf(
            NoteEntity(content = "Note 1", geofenceId = null, locationName = null),
            NoteEntity(content = "Note 2", geofenceId = null, locationName = null)
        )

        every { dao.getAllNotes() } returns flowOf(fakeNotes)

        val repository = NoteRepository(dao)

        val result = repository.allNotes.first()

        assertEquals(2, result.size)
    }
}