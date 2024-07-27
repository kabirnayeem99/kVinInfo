package io.github.kabirnayeem99.viminfo

import io.github.kabirnayeem99.viminfo.entities.Vin
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

    lateinit var vin: Vin

    @BeforeTest
    fun setUp() {
        vin = Vin.fromNumber(VALID_VIN)
    }

    @AfterTest
    fun tearDown() {
        vin = Vin.fromNumber("")
    }

    @Test
    fun `VIN should be VALID`() {
        assertTrue(vin.isValid, "$VALID_VIN should be a valid vin.")
    }

    @Test
    fun `VIN with less than 17 characters should be invalid`() {
        vin = Vin.fromNumber("1HGCM82635A12345")
        assertFalse(
            vin.isValid,
            "1HGCM82635A12345 should be invalid, as the number of characters are less than 17."
        )
    }

    @Test
    fun `VIN with more than 17 characters should be invalid`() {
        vin = Vin.fromNumber("1HGCM82635A1234567")
        assertFalse(
            vin.isValid,
            "1HGCM82635A1234567 should be invalid, as the number of characters are more than 17."
        )
    }


    @Test
    fun `VIN with invalid characters should be invalid`() {
        vin = Vin.fromNumber("1HGCM82633A!2345&")
        assertFalse(
            vin.isValid, "2345 should be invalid, as it contains invalid characters like ! and &"
        )
    }

    @Test
    fun `VIN should return correct year`() {
        vin = Vin.fromNumber(VALID_VIN)
        assertEquals(2005, vin.year, "The valid year should be 2005.")
    }

    @Test
    fun `VIN with wrong year should throw invalid year exception`() {
        vin = Vin.fromNumber("1HGCM8263?A123456")
        assertFailsWith(
            InvalidVinYearException::class, "This should throw a wrong year exception."
        ) {
            vin.year
        }
    }

    @Test
    fun `VIN region should be NA (North America)`() {
        vin = Vin.fromNumber(VALID_VIN)
        assertEquals("NA", vin.region)
    }

    @Test
    fun `VIN region should throw InvalidVinLengthException with empty VIN if asked for region`() {
        vin = Vin.fromNumber("")
        assertFailsWith(InvalidVinLengthException::class) {
            vin.region
        }
    }

    @Test
    fun `VIN region should throw InvalidVinRegionCharException`() {
    }

}
