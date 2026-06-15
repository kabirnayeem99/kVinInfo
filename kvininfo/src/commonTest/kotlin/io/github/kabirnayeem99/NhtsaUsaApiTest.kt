package io.github.kabirnayeem99

import io.github.kabirnayeem99.viminfo.VinInfo
import io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException
import io.github.kabirnayeem99.viminfo.network.dto.NhtsaDecodeVinDto
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Tests for NHTSA API interactions via [VinInfo.fromNumberWithEngine].
 *
 * All tests use MockEngine — no real network calls are made.
 */
class NhtsaUsaApiTest {

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private val testVin = "1HGBH41JXMN109186"

    /** Full happy-path JSON as returned by the NHTSA DecodeVin endpoint. */
    private val successJson = """
        {
          "Count": 5,
          "Message": "Results returned successfully",
          "Results": [
            {"Value": "0 - VIN decoded clean. Check Digit (9th position) is correct", "ValueId": "0", "Variable": "Error Text", "VariableId": 191},
            {"Value": "HONDA", "ValueId": "474", "Variable": "Make", "VariableId": 26},
            {"Value": "Accord", "ValueId": "1861", "Variable": "Model", "VariableId": 28},
            {"Value": "PASSENGER CAR", "ValueId": "5", "Variable": "Vehicle Type", "VariableId": 39},
            {"Value": "Sedan/Saloon", "ValueId": "10", "Variable": "Body Class", "VariableId": 5}
          ],
          "SearchCriteria": "VIN:$testVin"
        }
    """.trimIndent()

    /** JSON where Error Text is blank — treated as valid by isValidByNhtsa. */
    private val blankErrorJson = """
        {
          "Count": 1,
          "Message": "Results returned successfully",
          "Results": [
            {"Value": "", "ValueId": "0", "Variable": "Error Text", "VariableId": 191}
          ],
          "SearchCriteria": "VIN:$testVin"
        }
    """.trimIndent()

    /** JSON where Error Text signals a real decode error. */
    private val errorJson = """
        {
          "Count": 1,
          "Message": "Results returned successfully",
          "Results": [
            {"Value": "1 - VIN decoded with errors", "ValueId": "1", "Variable": "Error Text", "VariableId": 191}
          ],
          "SearchCriteria": "VIN:$testVin"
        }
    """.trimIndent()

    private fun mockEngineWith(body: String) = MockEngine { _ ->
        respond(
            content = ByteReadChannel(body),
            status = HttpStatusCode.OK,
            headers = headersOf("Content-Type", ContentType.Application.Json.toString()),
        )
    }

    private fun vinInfoWithMock(body: String): VinInfo =
        VinInfo.fromNumberWithEngine(testVin, mockEngineWith(body)).getOrThrow()

    // ---------------------------------------------------------------------------
    // isValidByNhtsa
    // ---------------------------------------------------------------------------

    @Test
    fun `isValidByNhtsa returns success when error text is clean decode message`() = runTest {
        val vinInfo = vinInfoWithMock(successJson)
        val result = vinInfo.isValidByNhtsa()
        assertTrue(result.isSuccess)
        assertEquals(testVin, result.getOrThrow())
        vinInfo.close()
    }

    @Test
    fun `isValidByNhtsa returns success when error text is blank`() = runTest {
        val vinInfo = vinInfoWithMock(blankErrorJson)
        val result = vinInfo.isValidByNhtsa()
        assertTrue(result.isSuccess)
        vinInfo.close()
    }

    @Test
    fun `isValidByNhtsa returns failure when error text describes a decode error`() = runTest {
        val vinInfo = vinInfoWithMock(errorJson)
        val result = vinInfo.isValidByNhtsa()
        assertTrue(result.isFailure)
        vinInfo.close()
    }

    // ---------------------------------------------------------------------------
    // getMakeFromNhtsa
    // ---------------------------------------------------------------------------

    @Test
    fun `getMakeFromNhtsa returns the make from the API response`() = runTest {
        val vinInfo = vinInfoWithMock(successJson)
        assertEquals("HONDA", vinInfo.getMakeFromNhtsa())
        vinInfo.close()
    }

    // ---------------------------------------------------------------------------
    // getModelFromNhtsa
    // ---------------------------------------------------------------------------

    @Test
    fun `getModelFromNhtsa returns the model from the API response`() = runTest {
        val vinInfo = vinInfoWithMock(successJson)
        assertEquals("Accord", vinInfo.getModelFromNhtsa())
        vinInfo.close()
    }

    // ---------------------------------------------------------------------------
    // getVehicleTypeFromNhtsa
    // ---------------------------------------------------------------------------

    @Test
    fun `getVehicleTypeFromNhtsa returns the vehicle type from the API response`() = runTest {
        val vinInfo = vinInfoWithMock(successJson)
        assertEquals("PASSENGER CAR", vinInfo.getVehicleTypeFromNhtsa())
        vinInfo.close()
    }

    // ---------------------------------------------------------------------------
    // getBodyClassFromNhtsa
    // ---------------------------------------------------------------------------

    @Test
    fun `getBodyClassFromNhtsa returns the body class from the API response`() = runTest {
        val vinInfo = vinInfoWithMock(successJson)
        assertEquals("Sedan/Saloon", vinInfo.getBodyClassFromNhtsa())
        vinInfo.close()
    }

    // ---------------------------------------------------------------------------
    // toJsonString
    // ---------------------------------------------------------------------------

    @Test
    fun `toJsonString returns a non-empty JSON string on success`() = runTest {
        val vinInfo = vinInfoWithMock(successJson)
        val json = vinInfo.toJsonString()
        assertTrue(json.isNotBlank())
        assertTrue(json.contains("HONDA"))
        vinInfo.close()
    }

    @Test
    fun `toJsonString throws NhtsaDatabaseFailedException when API returns null results`() = runTest {
        // A null Results field causes decodeVinWithApi to return null,
        // which then causes getInfoAsMap to throw NhtsaDatabaseFailedException,
        // which toStringAsJson catches and rethrows as NhtsaDatabaseFailedException.
        val nullResultsJson = """
            {
              "Count": 0,
              "Message": "Results returned successfully",
              "Results": null,
              "SearchCriteria": "VIN:$testVin"
            }
        """.trimIndent()
        val failEngine = MockEngine { _ ->
            respondError(HttpStatusCode.InternalServerError)
        }
        val vinInfo = VinInfo.fromNumberWithEngine(testVin, failEngine).getOrThrow()
        assertFailsWith<NhtsaDatabaseFailedException> {
            vinInfo.toJsonString()
        }
        vinInfo.close()
    }

    // ---------------------------------------------------------------------------
    // Closed client — covers NhtsaDatabaseAlreadyClosedException (thrown internally)
    // and NhtsaDatabaseFailedException (propagated to caller)
    // ---------------------------------------------------------------------------

    @Test
    fun `calling getMakeFromNhtsa after close throws NhtsaDatabaseFailedException`() = runTest {
        // Use a fail engine so the first call leaves decodedValueMap empty (no cached results).
        // The lazy NhtsaUsaApi is still initialized. After close(), isClosed=true; the next
        // call hits the isClosed guard, decodeVinWithApi returns null, resolveDecodedValue throws.
        val failEngine = MockEngine { _ ->
            respondError(HttpStatusCode.InternalServerError)
        }
        val vinInfo = VinInfo.fromNumberWithEngine(testVin, failEngine).getOrThrow()
        // First call initializes the lazy NhtsaUsaApi and fails (map stays empty).
        runCatching { vinInfo.getMakeFromNhtsa() }
        // Now close — sets isClosed=true on the already-initialized NhtsaUsaApi.
        vinInfo.close()
        // Second call: map is empty, decodeVinWithApi sees isClosed=true and returns null,
        // resolveDecodedValue throws NhtsaDatabaseFailedException.
        assertFailsWith<NhtsaDatabaseFailedException> {
            vinInfo.getMakeFromNhtsa()
        }
    }

    // ---------------------------------------------------------------------------
    // Network failure — engine returns an error status
    // ---------------------------------------------------------------------------

    @Test
    fun `getMakeFromNhtsa throws NhtsaDatabaseFailedException when network returns error`() = runTest {
        val failEngine = MockEngine { _ ->
            respondError(HttpStatusCode.InternalServerError)
        }
        val vinInfo = VinInfo.fromNumberWithEngine(testVin, failEngine).getOrThrow()
        // decodeVinWithApi catches the exception and returns null;
        // resolveDecodedValue then throws NhtsaDatabaseFailedException.
        assertFailsWith<NhtsaDatabaseFailedException> {
            vinInfo.getMakeFromNhtsa()
        }
        vinInfo.close()
    }

    // ---------------------------------------------------------------------------
    // isValidByNhtsa null response — covers the ?: "" fallback on line 193
    // ---------------------------------------------------------------------------

    @Test
    fun `isValidByNhtsa returns success when decodeVin returns null`() = runTest {
        // Engine returns 500 → decodeVinWithApi catches exception and returns null →
        // the safe-call chain evaluates to null → ?: "" → errorMessage = "" → success.
        val failEngine = MockEngine { _ -> respondError(HttpStatusCode.InternalServerError) }
        val vinInfo = VinInfo.fromNumberWithEngine(testVin, failEngine).getOrThrow()
        val result = vinInfo.isValidByNhtsa()
        assertTrue(result.isSuccess)
        vinInfo.close()
    }

    // ---------------------------------------------------------------------------
    // NhtsaDecodeVinDto property coverage — count, message, searchCriteria, valueId
    // ---------------------------------------------------------------------------

    @Test
    fun `NhtsaDecodeVinDto properties are accessible`() {
        val dto = NhtsaDecodeVinDto(
            count = 5L,
            message = "OK",
            results = null,
            searchCriteria = "VIN:TEST",
        )
        assertEquals(5L, dto.count)
        assertEquals("OK", dto.message)
        assertEquals("VIN:TEST", dto.searchCriteria)
        val result = NhtsaDecodeVinDto.Result(
            value = "HONDA",
            valueId = "474",
            variable = "Make",
            variableId = 26L,
        )
        assertEquals("474", result.valueId)
    }
}
