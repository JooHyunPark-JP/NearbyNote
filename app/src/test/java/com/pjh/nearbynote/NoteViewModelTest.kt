package com.pjh.nearbynote

import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteRepository
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteViewModelTest {

    private val repository: NoteRepository = mockk(relaxed = true)
    private lateinit var viewModel: NoteViewModel
    private lateinit var MapBoxRepository: MapboxRepository


    @Before
    fun setup() {
        every { repository.allNotes } returns flowOf(emptyList())
        MapBoxRepository = mockk(relaxed = true)
        viewModel = NoteViewModel(repository,MapBoxRepository)
    }

    @Test
    fun saveNote_shouldCallRepositoryInsertNote() = runTest {
        val noteText = "Mock Note"
        viewModel.saveNote(noteText, null, null, false)

        coVerify {
            repository.insertNote(
                match { it.content == noteText }
            )
        }
    }
}
