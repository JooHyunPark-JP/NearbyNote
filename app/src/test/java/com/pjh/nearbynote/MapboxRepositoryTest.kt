package com.pjh.nearbynote

import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxHttpClient
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class MapboxRepositoryTest {

    private lateinit var httpClient: MapboxHttpClient
    private lateinit var repository: MapboxRepository

    @Before
    fun setUp() {
        httpClient = mockk()
        repository = MapboxRepositoryImpl(httpClient)
    }

    @Test
    fun `fetchAddressSuggestions returns parsed suggestions when response is valid`() = runTest {
        val json =
            """{ "type": "FeatureCollection", "query": ["seoul"], "features": [ { "id": "1", "type": "Feature", "place_name": "Seoul, South Korea", "geometry": { "coordinates": [126.978, 37.5665] } } ] }"""

        coEvery { httpClient.getPlaceData(any()) } returns json

        val result = repository.fetchAddressSuggestions("seoul")

        assertEquals(1, result.size)
        assertEquals("Seoul, South Korea", result[0].placeName)

    }

    @Test
    fun `fetchAddressSuggestions returns empty list on error`() = runTest {
        coEvery { httpClient.getPlaceData(any()) } throws RuntimeException("API fail")

        val result = repository.fetchAddressSuggestions("fail")
        assertTrue(result.isEmpty())
    }
}