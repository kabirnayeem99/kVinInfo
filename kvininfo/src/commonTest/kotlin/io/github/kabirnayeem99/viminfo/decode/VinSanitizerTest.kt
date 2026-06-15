package io.github.kabirnayeem99.viminfo.decode

import kotlin.test.Test
import kotlin.test.assertEquals

class VinSanitizerTest {

    @Test
    fun `sanitize returns empty string for empty input`() {
        assertEquals("", VinSanitizer.sanitize(""))
    }

    @Test
    fun `sanitize returns empty string for whitespace-only input`() {
        assertEquals("", VinSanitizer.sanitize("   "))
    }

    @Test
    fun `sanitize does not modify already clean VIN`() {
        val vin = "1HGBH41JXMN109186"
        assertEquals(vin, VinSanitizer.sanitize(vin))
    }

    @Test
    fun `sanitize uppercases lowercase input`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize("1hgbh41jxmn109186"))
    }

    @Test
    fun `sanitize uppercases mixed case input`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize("1HgBh41jXmN109186"))
    }

    @Test
    fun `sanitize removes leading and trailing spaces`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize("  1HGBH41JXMN109186  "))
    }

    @Test
    fun `sanitize removes leading and trailing tabs`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize("\t1HGBH41JXMN109186\t"))
    }

    @Test
    fun `sanitize removes internal spaces`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize("1HGBH 41J XMN10 9186"))
    }

    @Test
    fun `sanitize removes internal tabs`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize("1HGBH41J\tXMN109186"))
    }

    @Test
    fun `sanitize removes hyphens`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize("1HGBH-41J-XMN109186"))
    }

    @Test
    fun `sanitize removes hyphens and spaces together`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize("1HGBH - 41JX - MN109186"))
    }

    @Test
    fun `sanitize replaces uppercase I with 1`() {
        assertEquals("11111111111111111", VinSanitizer.sanitize("IIIIIIIIIIIIIIIII"))
    }

    @Test
    fun `sanitize replaces uppercase O with 0`() {
        assertEquals("00000000000000000", VinSanitizer.sanitize("OOOOOOOOOOOOOOOOO"))
    }

    @Test
    fun `sanitize replaces uppercase Q with 0`() {
        assertEquals("00000000000000000", VinSanitizer.sanitize("QQQQQQQQQQQQQQQQQ"))
    }

    @Test
    fun `sanitize replaces lowercase i with 1`() {
        assertEquals("11111111111111111", VinSanitizer.sanitize("iiiiiiiiiiiiiiiii"))
    }

    @Test
    fun `sanitize replaces lowercase o with 0`() {
        assertEquals("00000000000000000", VinSanitizer.sanitize("ooooooooooooooooo"))
    }

    @Test
    fun `sanitize replaces lowercase q with 0`() {
        assertEquals("00000000000000000", VinSanitizer.sanitize("qqqqqqqqqqqqqqqqq"))
    }

    @Test
    fun `sanitize replaces mixed I O Q`() {
        assertEquals("100", VinSanitizer.sanitize("IOQ"))
    }

    @Test
    fun `sanitize replaces O in mid VIN position`() {
        assertEquals("10GBH41JXMN109186", VinSanitizer.sanitize("1OGBH41JXMN109186"))
    }

    @Test
    fun `sanitize replaces I in mid VIN position`() {
        assertEquals("1HGBH41JXMN119186", VinSanitizer.sanitize("1HGBH41JXMN1I9186"))
    }

    @Test
    fun `sanitize handles real dirty VIN with spaces hyphens and case`() {
        assertEquals("1HGBH41JXMN109186", VinSanitizer.sanitize(" 1hgbh-41j xMN1o9186 "))
    }

    @Test
    fun `sanitize preserves other special characters unchanged`() {
        assertEquals("WBA3A5G$9DNP26082", VinSanitizer.sanitize("WBA3A5G$9DNP26082"))
    }
}
