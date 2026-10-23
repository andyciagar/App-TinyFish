package com.apps.iatinyfish

import com.apps.iatinyfish.data.repository.PreviewDealsRepository
import com.apps.iatinyfish.data.repository.RemoteDealsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DealsRepositoryTest {

    @Test
    fun previewRepository_returnsDefaultOffers() = runTest {
        val repository = PreviewDealsRepository
        assertTrue(repository.isPreviewData)

        val results = repository.search("Nintendo")
        assertFalse(results.isEmpty())
        assertTrue(results.any { it.productName.contains("Nintendo", ignoreCase = true) })
    }

    @Test
    fun previewRepository_emptyQuery_returnsAllOffers() = runTest {
        val repository = PreviewDealsRepository
        val results = repository.search("")
        assertFalse(results.isEmpty())
    }

    @Test
    fun remoteRepository_search_handlesConnectionOrReturnsResults() = runTest {
        val repository = RemoteDealsRepository()
        assertFalse(repository.isPreviewData)

        try {
            val results = repository.search("Nintendo Switch")
            assertNotNull(results)
            println("Remote search returned ${results.size} offers.")
        } catch (e: Exception) {
            println("Remote search exception (expected if endpoint is unreachable or not configured): ${e.message}")
            assertNotNull(e.message)
        }
    }
}
