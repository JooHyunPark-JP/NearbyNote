package com.example.nearbynote

import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceEntity
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceRepository
import com.example.nearbynote.test.FakeGeofenceRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalCoroutinesApi::class)
class GeofenceRepositoryTest {

    private lateinit var repository: GeofenceRepository

    @Before
    fun setup() {
        repository = FakeGeofenceRepository()
    }

    @Test
    fun `save and retrieve geofence by id`() = runTest {
        val geofence = GeofenceEntity(
            id = "g1",
            name = "Test",
            latitude = 37.0,
            longitude = -122.0,
            radius = 100f,
            createdAt = System.currentTimeMillis()
        )

        repository.saveGeofence(geofence)
        val result = repository.getGeofenceById("g1")

        assertEquals(geofence, result)
    }

    @Test
    fun `delete geofence and verify not present`() = runTest {
        val geofence = GeofenceEntity(
            id = "g2",
            name = "ToDelete",
            latitude = 37.1,
            longitude = -122.1,
            radius = 200f,
            createdAt = System.currentTimeMillis()
        )

        repository.saveGeofence(geofence)
        repository.deleteGeofence(geofence)
        val result = repository.getGeofenceById("g2")

        assertNull(result)
    }

    @Test
    fun `getAll emits current list`() = runTest {
        val g1 = GeofenceEntity("1", "A", 1.0, 1.0, 100f, System.currentTimeMillis())
        val g2 = GeofenceEntity("2", "B", 2.0, 2.0, 100f, System.currentTimeMillis())

        repository.saveGeofence(g1)
        repository.saveGeofence(g2)

        val values = repository.getAllGeofences().first()

        assertTrue(values.contains(g1))
        assertTrue(values.contains(g2))
        assertEquals(2, values.size)
    }
}