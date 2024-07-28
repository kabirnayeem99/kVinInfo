package io.github.kabirnayeem99

import io.github.kabirnayeem99.viminfo.VinInfo
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinYearException
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val VALID_VIN = "WBA3A5G59DNP26082"

class VinInfoTest {

    private lateinit var vinInfo: VinInfo

    @BeforeTest
    fun setUp() {
        vinInfo = VinInfo.fromNumber(VALID_VIN)
    }

    @AfterTest
    fun tearDown() {
        vinInfo.close()
    }

    @Test
    fun `VIN should be VALID`() {
        assertTrue(vinInfo.isValid, "$VALID_VIN should be a valid vinInfo.")
    }

    @Test
    fun `VIN with less than 17 characters should be invalid`() {
        vinInfo = VinInfo.fromNumber("WBA3A5G59DNP2608")
        assertFalse(
            vinInfo.isValid,
            "1HGCM82635A12345 should be invalid, as the number of characters are less than 17."
        )
    }

    @Test
    fun `VIN with more than 17 characters should be invalid`() {
        vinInfo = VinInfo.fromNumber("WBA3A5G59DNP260823")
        assertFalse(
            vinInfo.isValid,
            "1HGCM82635A1234567 should be invalid, as the number of characters are more than 17."
        )
    }


    @Test
    fun `VIN with invalid characters should be invalid`() {
        vinInfo = VinInfo.fromNumber("WBA3A5G59!NP26&82")
        assertFalse(
            vinInfo.isValid,
            "WBA3A5G59!NP26&82 should be invalid, as it contains invalid characters like ! and &"
        )
    }

    @Test
    fun `VIN should return correct year`() {
        vinInfo = VinInfo.fromNumber(VALID_VIN)
        assertEquals(2013, vinInfo.year, "The valid year should be 2005.")
    }

    @Test
    fun `VIN with wrong year should throw invalid year exception`() {
        vinInfo = VinInfo.fromNumber("WBA3A5G59?NP26082")
        assertFailsWith(
            InvalidVinYearException::class, "This should throw a wrong year exception."
        ) {
            vinInfo.year
        }
    }

    @Test
    fun `VIN region should be NA North America`() {
        vinInfo = VinInfo.fromNumber(VALID_VIN)
        assertEquals("EU", vinInfo.regionCode)
    }

    @Test
    fun `VIN region should throw InvalidVinLengthException with empty VIN if asked for region`() {
        vinInfo = VinInfo.fromNumber("")
        assertFailsWith(InvalidVinLengthException::class) {
            vinInfo.regionCode
        }
    }


    @Test
    fun `VIN data returned from NHTSA should be correct`() {
        runBlocking {
            vinInfo = VinInfo.fromNumber(VALID_VIN)
            assertEquals(vinInfo.getMakeFromNhtsa(), "BMW")
        }
    }


}