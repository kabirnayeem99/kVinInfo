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
import kotlin.test.fail

private const val VALID_VIN = "WBA3A5G59DNP26082"

class VinInfoTest {

    @Test
    fun testIsValid_validVin() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertTrue(vinInfo.isValid)
    }

    @Test
    fun testIsValid_invalidLength() {
        val shortVin = VinInfo.fromNumber("WBA3A5G")
        val longVin = VinInfo.fromNumber("WBA3A5G59DNP26082X")
        assertFalse(shortVin.isValid)
        assertFalse(longVin.isValid)
    }

    @Test
    fun testIsValid_invalidCharacters() {
        val invalidCharVin = VinInfo.fromNumber("WBA3A5G$9DNP26082")
        assertFalse(invalidCharVin.isValid)
    }

    @Test
    fun testIsValid_incorrectChecksum() {
        val invalidChecksumVin = VinInfo.fromNumber("WBA3A5G52DNP26082")
        assertFalse(invalidChecksumVin.isValid)
    }

    @Test
    fun testWmiVdsVisExtraction() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals("WBA", vinInfo.wmi)
        assertEquals("3A5G59", vinInfo.vds)
        assertEquals("DNP26082", vinInfo.vis)
    }

    @Test
    fun testYearExtraction() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals(2013, vinInfo.year)
    }

    @Test
    fun testChecksumCalculation() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals('9', vinInfo.calculatedChecksum)
    }

    @Test
    fun testRegionAndCountry() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals("EU", vinInfo.regionCode)
        assertEquals("Europe", vinInfo.region)
        assertEquals("Germany", vinInfo.country)
    }

    @Test
    fun testManufacturer() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals("BMW", vinInfo.manufacturer)
    }

    @Test
    fun testAssemblyPlantAndSerialNumber() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals('N', vinInfo.assemblyPlant)
        assertEquals("26082", vinInfo.serialNumber)
    }

    @Test
    fun testInvalidVinLengthException() {
        try {
            VinInfo.fromNumber("")
            fail("Expected InvalidVinLengthException")
        } catch (e: InvalidVinLengthException) {
            // Expected exception
        }
    }


}