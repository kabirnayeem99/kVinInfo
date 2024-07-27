package io.github.kabirnayeem99

import io.github.kabirnayeem99.viminfo.VinInfo
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinYearException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val VALID_VIN = "1HGCM82635A123456"

class VinInfoTest {

    private lateinit var vinInfo: VinInfo

    @BeforeTest
    fun setUp() {
        vinInfo = VinInfo.fromNumber(VALID_VIN)
    }

    @AfterTest
    fun tearDown() {
        vinInfo = VinInfo.fromNumber("")
    }

    @Test
    fun `VIN should be VALID`() {
        assertTrue(vinInfo.isValid, "$VALID_VIN should be a valid vinInfo.")
    }

    @Test
    fun `VIN with less than 17 characters should be invalid`() {
        vinInfo = VinInfo.fromNumber("1HGCM82635A12345")
        assertFalse(
            vinInfo.isValid,
            "1HGCM82635A12345 should be invalid, as the number of characters are less than 17."
        )
    }

    @Test
    fun `VIN with more than 17 characters should be invalid`() {
        vinInfo = VinInfo.fromNumber("1HGCM82635A1234567")
        assertFalse(
            vinInfo.isValid,
            "1HGCM82635A1234567 should be invalid, as the number of characters are more than 17."
        )
    }


    @Test
    fun `VIN with invalid characters should be invalid`() {
        vinInfo = VinInfo.fromNumber("1HGCM82633A!2345&")
        assertFalse(
            vinInfo.isValid, "2345 should be invalid, as it contains invalid characters like ! and &"
        )
    }

    @Test
    fun `VIN should return correct year`() {
        vinInfo = VinInfo.fromNumber(VALID_VIN)
        assertEquals(2005, vinInfo.year, "The valid year should be 2005.")
    }

    @Test
    fun `VIN with wrong year should throw invalid year exception`() {
        vinInfo = VinInfo.fromNumber("1HGCM8263?A123456")
        assertFailsWith(
            InvalidVinYearException::class, "This should throw a wrong year exception."
        ) {
            vinInfo.year
        }
    }

    @Test
    fun `VIN region should be NA North America`() {
        vinInfo = VinInfo.fromNumber(VALID_VIN)
        assertEquals("NA", vinInfo.region)
    }

    @Test
    fun `VIN region should throw InvalidVinLengthException with empty VIN if asked for region`() {
        vinInfo = VinInfo.fromNumber("")
        assertFailsWith(InvalidVinLengthException::class) {
            vinInfo.region
        }
    }

    @Test
    fun `VIN region should throw InvalidVinRegionCharException`() {
    }

}