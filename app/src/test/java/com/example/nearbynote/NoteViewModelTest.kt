package com.example.nearbynote

import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteRepository
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
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

    @Before
    fun setup() {
        every { repository.allNotes } returns flowOf(emptyList())
        viewModel = NoteViewModel(repository)
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
