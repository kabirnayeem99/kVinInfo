package io.github.kabirnayeem99

import io.github.kabirnayeem99.viminfo.VinInfo
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail

class VinInfoTest {

    @Test
    fun `should validate a VIN with a correct format and checksum`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertTrue(vinInfo.isValid)
    }

    @Test
    fun `should not validate a VIN with an invalid length`() {
        val shortVin = VinInfo.fromNumber("WBA3A5G")
        val longVin = VinInfo.fromNumber("WBA3A5G59DNP26082X")
        assertFalse(shortVin.isValid)
        assertFalse(longVin.isValid)
    }

    @Test
    fun `should not validate a VIN with invalid characters`() {
        val invalidCharVin = VinInfo.fromNumber("WBA3A5G$9DNP26082")
        assertFalse(invalidCharVin.isValid)
    }

    @Test
    fun `should not validate a VIN with an incorrect checksum`() {
        val invalidChecksumVin = VinInfo.fromNumber("WBA3A5G52DNP26082")
        assertFalse(invalidChecksumVin.isValid)
    }

    @Test
    fun `should extract WMI VDS and VIS components from a valid VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals("WBA", vinInfo.wmi)
        assertEquals("3A5G59", vinInfo.vds)
        assertEquals("DNP26082", vinInfo.vis)
    }

    @Test
    fun `should extract the year from a valid VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals(2013, vinInfo.year)
    }

    @Test
    fun `should calculate the correct checksum for a valid VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals('9', vinInfo.calculatedChecksum)
    }

    @Test
    fun `should determine the region and country based on the VIN code`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals("EU", vinInfo.regionCode)
        assertEquals("Europe", vinInfo.region)
        assertEquals("Germany", vinInfo.country)
    }

    @Test
    fun `should determine the manufacturer based on the VIN code`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals("BMW", vinInfo.manufacturer)
    }

    @Test
    fun `should extract the assembly plant and serial number from a valid VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082")
        assertEquals('N', vinInfo.assemblyPlant)
        assertEquals("26082", vinInfo.serialNumber)
    }

    @Test
    fun `should throw an exception when creating a VIN with an invalid length`() {
        try {
            VinInfo.fromNumber("")
            fail("Expected InvalidVinLengthException")
        } catch (e: InvalidVinLengthException) {
            // Expected exception
        }
    }
}
