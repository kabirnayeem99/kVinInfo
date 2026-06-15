package io.github.kabirnayeem99

import io.github.kabirnayeem99.viminfo.network.NhtsaUsaApi
import kotlin.test.Test

/**
 * Exercises the else branch in NhtsaUsaApi's httpClient initialisation (lines 46-50) where no
 * explicit engine is provided and Ktor uses the platform default (CIO on JVM).
 *
 * This test lives in jvmTest because the platform CIO engine (ktor-client-cio) is only available
 * on JVM, and HttpClient {} without an explicit engine would fail in common/iOS targets.
 */
class NhtsaUsaApiJvmTest {

    @Test
    fun `NhtsaUsaApi without explicit engine initialises and closes cleanly`() {
        // Constructing with no engine argument takes the else branch (lines 46-50).
        // close() is called immediately — no network request is made.
        val api = NhtsaUsaApi("1HGBH41JXMN109186")
        api.close()
    }
}
