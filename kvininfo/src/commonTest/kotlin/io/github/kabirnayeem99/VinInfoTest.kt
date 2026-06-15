package io.github.kabirnayeem99

import io.github.kabirnayeem99.viminfo.VinInfo
import io.github.kabirnayeem99.viminfo.VinInfo.Companion.withVinInfo
import io.github.kabirnayeem99.viminfo.data.getCountryFromWmi
import io.github.kabirnayeem99.viminfo.decode.VinChecksum
import io.github.kabirnayeem99.viminfo.decode.VinRegion
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidWmiForCountryException
import io.github.kabirnayeem99.viminfo.exceptions.NoChecksumForEuException
import io.github.kabirnayeem99.viminfo.exceptions.VinChecksumMismatchException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VinInfoTest {
    @Test
    fun `should validate a VIN with a correct format and checksum`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertTrue(vinInfo.isValid)
    }

    @Test
    fun `should not validate a VIN with an invalid length`() {
        val shortResult = VinInfo.fromNumber("WBA3A5G")
        val longResult = VinInfo.fromNumber("WBA3A5G59DNP26082X")
        assertTrue(shortResult.isFailure)
        assertTrue(longResult.isFailure)
        assertIs<InvalidVinLengthException>(shortResult.exceptionOrNull())
        assertIs<InvalidVinLengthException>(longResult.exceptionOrNull())
    }

    @Test
    fun `should not validate a VIN with invalid characters`() {
        val result = VinInfo.fromNumber("WBA3A5G$9DNP26082")
        assertTrue(result.isFailure)
        assertIs<InvalidVinException>(result.exceptionOrNull())
    }

    @Test
    fun `should not validate a North American VIN with an incorrect checksum`() {
        val result = VinInfo.fromNumber("1HGBH41J0MN109186")
        assertTrue(result.isFailure)
        assertIs<VinChecksumMismatchException>(result.exceptionOrNull())
    }

    @Test
    fun `should validate a North American VIN with a correct checksum`() {
        val vinInfo = VinInfo.fromNumber("1HGBH41JXMN109186").getOrThrow()
        assertTrue(vinInfo.isValid)
    }

    @Test
    fun `should require the check digit for North American VINs`() {
        val vinInfo = VinInfo.fromNumber("1HGBH41JXMN109186").getOrThrow()
        assertTrue(vinInfo.isCheckDigitRequired)
    }

    @Test
    fun `should treat the check digit as optional for European VINs`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G52DNP26082").getOrThrow()
        assertFalse(vinInfo.isCheckDigitRequired)
        assertFalse(vinInfo.isCheckDigitValid)
        assertTrue(vinInfo.isValid)
    }

    @Test
    fun `should extract WMI VDS and VIS components from a valid VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertEquals("WBA", vinInfo.wmi)
        assertEquals("3A5G59", vinInfo.vds)
        assertEquals("DNP26082", vinInfo.vis)
    }

    @Test
    fun `should extract the year from a valid VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertEquals(2013, vinInfo.year)
    }

    @Test
    fun `should calculate the correct checksum for a valid VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertEquals('9', vinInfo.calculatedChecksum)
    }

    @Test
    fun `should determine the region and country based on the VIN code`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertEquals("EU", vinInfo.regionCode)
        assertEquals("Europe", vinInfo.region)
        assertEquals("Germany", vinInfo.country)
    }

    @Test
    fun `should determine the manufacturer based on the VIN code`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertEquals("BMW", vinInfo.manufacturer)
    }

    @Test
    fun `should extract the assembly plant and serial number from a valid VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertEquals('N', vinInfo.assemblyPlant)
        assertEquals("P26082", vinInfo.serialNumber)
    }

    @Test
    fun `should throw an exception when creating a VIN with an invalid length`() {
        val result = VinInfo.fromNumber("")
        assertTrue(result.isFailure)
        assertIs<InvalidVinLengthException>(result.exceptionOrNull())
    }

    @Test
    fun `should resolve a small-volume manufacturer from the extended WMI`() {
        // WMI SA9 (small volume) + positions 12-14 = "019" -> TVR.
        val vinInfo = VinInfo.fromNumber("SA9B0000CLA019123").getOrThrow()
        assertTrue(vinInfo.isSmallVolumeManufacturer)
        assertEquals("TVR", vinInfo.manufacturer)
    }

    @Test
    fun `should use positions 15-17 as serial number for small-volume manufacturers`() {
        val vinInfo = VinInfo.fromNumber("SA9B0000CLA019123").getOrThrow()
        assertEquals("123", vinInfo.serialNumber)
    }

    @Test
    fun `should not flag a high-volume manufacturer as small-volume`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertFalse(vinInfo.isSmallVolumeManufacturer)
    }

    @Test
    fun `should fall back to a country range for an unknown WMI`() {
        // WBC is not in the explicit WMI list; the W block resolves to Germany via the range table.
        // Use 'A' at position 10 (index 9) for a valid year code.
        val vinInfo = VinInfo.fromNumber("WBC000000A0000000").getOrThrow()
        assertEquals("Germany", vinInfo.country)
    }

    @Test
    fun `should decode model year for a North American VIN with numeric position 7`() {
        // Position 7 is numeric (1) -> 1980-2009 cycle; year char M -> 1991.
        val vinInfo = VinInfo.fromNumber("1HGBH41JXMN109186").getOrThrow()
        assertEquals(1991, vinInfo.year)
    }

    @Test
    fun `should map first characters to ISO 3780 regions`() {
        // Use direct decoder to test mapping without needing valid 17-char VINs
        assertEquals("AS", VinRegion.code("H"))
        assertEquals("AS", VinRegion.code("L"))
        assertEquals("EU", VinRegion.code("E"))
        assertEquals("NA", VinRegion.code("7"))
        assertEquals("OC", VinRegion.code("6"))
    }

    @Test
    fun `should require the check digit for Chinese VINs`() {
        assertTrue(VinRegion.requiresCheckDigit("H"))
        assertTrue(VinRegion.requiresCheckDigit("L"))
    }

    @Test
    fun `should resolve country ranges using ISO 3780 digit ordering`() {
        // 8X-8Z is Venezuela, 82 is Bolivia (digits ordered after letters).
        // Pad to 17 chars to use VinInfo integration test
        assertEquals("Venezuela", VinInfo.fromNumber("8ZX0000001A000000").getOrThrow().country)
        assertEquals("Bolivia", VinInfo.fromNumber("82X0000001A000000").getOrThrow().country)
        assertEquals("Japan", VinInfo.fromNumber("JTX0000001A000000").getOrThrow().country)
    }

    @Test
    fun `should compute the check digit from the spec worked example`() {
        // 1M8GDM9A_KP042788 -> sum 351, 351 mod 11 = 10 -> X.
        assertEquals('X', VinInfo.fromNumber("1M8GDM9AXKP042788").getOrThrow().calculatedChecksum)
    }

    @Test
    fun `should compute check digit 1 for the straight-ones VIN`() {
        // Sum of weights is 89; 89 mod 11 = 1, so the check digit is 1.
        val vinInfo = VinInfo.fromNumber("11111111111111111").getOrThrow()
        assertEquals('1', vinInfo.calculatedChecksum)
        assertTrue(vinInfo.isCheckDigitValid)
    }

    @Test
    fun `should accept a known-valid North American check digit`() {
        assertTrue(VinInfo.fromNumber("5GZCZ43D13S812715").getOrThrow().isCheckDigitValid)
    }

    @Test
    fun `should reject VINs that fail North American check-digit verification`() {
        // fromNumber now rejects them. Use a North American VIN (starts with 1).
        val result = VinInfo.fromNumber("1HGBH41J0MN109186")
        assertTrue(result.isFailure)
        assertIs<VinChecksumMismatchException>(result.exceptionOrNull())
    }

    @Test
    fun `fromNumber sanitizes O to 0 before validation`() {
        // 1HGBH41JXMN1O9186 (O at index 13) -> 1HGBH41JXMN109186
        val vinInfo = VinInfo.fromNumber("1HGBH41JXMN1O9186").getOrThrow()
        assertEquals("1HGBH41JXMN109186", vinInfo.vinNumber)
    }

    @Test
    fun `fromNumber sanitizes lowercase input`() {
        val vinInfo = VinInfo.fromNumber("1hgbh41jxmn109186").getOrThrow()
        assertEquals("1HGBH41JXMN109186", vinInfo.vinNumber)
    }

    @Test
    fun `fromNumber never throws for any string input`() {
        val inputs = listOf(
            "", " ", "null", "undefined", "! @#$%^&*()",
            "00000000000000000", "a", "this is not a vin at all",
            "QQQQQQQQQQQQQQQQQ"
        )
        inputs.forEach { input ->
            val result = VinInfo.fromNumber(input)
            assertNotNull(result)
            assertTrue(result.isFailure)
        }
    }

    // --- checksum property ---

    @Test
    fun `checksum returns check digit char for non-EU VIN`() {
        val vinInfo = VinInfo.fromNumber("1HGBH41JXMN109186").getOrThrow()
        assertEquals('X', vinInfo.checksum)
        vinInfo.close()
    }

    @Test
    fun `checksum throws NoChecksumForEuException for EU VIN`() {
        val vinInfo = VinInfo.fromNumber("WBA3A5G59DNP26082").getOrThrow()
        assertFailsWith<NoChecksumForEuException> { vinInfo.checksum }
        vinInfo.close()
    }

    // --- toString ---

    @Test
    fun `toString returns the normalized VIN`() {
        val vinInfo = VinInfo.fromNumber("1HGBH41JXMN109186").getOrThrow()
        assertEquals("1HGBH41JXMN109186", vinInfo.toString())
        vinInfo.close()
    }

    // --- withVinInfo extension ---

    @Test
    fun `withVinInfo executes block for valid VIN and closes resource`() {
        var captured = 0
        "1HGBH41JXMN109186".withVinInfo { captured = year }
        assertEquals(1991, captured)
    }

    @Test
    fun `withVinInfo skips block for invalid VIN`() {
        var called = false
        "NOT-A-VIN".withVinInfo { called = true }
        assertFalse(called)
    }

    // --- getCountryFromWmi else branch in secondCharRank ---

    @Test
    fun `getCountryFromWmi with invalid second char hits else branch in secondCharRank`() {
        // '8' has only 2-char start ranges (8A–8E, 8F–8G, …); there is no single-char "8" fallback.
        // '@' is not A-Z or 0-9, so secondCharRank returns -1, which falls outside every range.
        // The WMI "8@X" is also absent from wmiList, so getCountryFromWmi throws.
        assertFailsWith<InvalidWmiForCountryException> {
            getCountryFromWmi("8@X")
        }
    }
}
