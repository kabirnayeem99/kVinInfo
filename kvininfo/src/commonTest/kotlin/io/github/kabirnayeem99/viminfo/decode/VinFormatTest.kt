package io.github.kabirnayeem99.viminfo.decode

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VinFormatTest {
    // ── Happy-path ────────────────────────────────────────────────────────────

    @Test
    fun `valid 17-char VIN from allowed alphabet is accepted`() {
        assertTrue(VinFormat.isValid("WBA3A5G59DNP26082"))
    }

    @Test
    fun `17 all-digit characters are accepted`() {
        // Digits 1-9 are all valid; 0 is also valid in a VIN.
        assertTrue(VinFormat.isValid("12345678901234567"))
    }

    @Test
    fun `17 all-letter characters from allowed alphabet are accepted`() {
        // Uses only chars from A-H, J-N, P, R-Z (excludes I, O, Q).
        assertTrue(VinFormat.isValid("ABCDEFGHJKLMNPRSZ"))
    }

    @Test
    fun `VIN with mix of allowed letters and digits is accepted`() {
        assertTrue(VinFormat.isValid("1HGBH41JXMN109186"))
    }

    // ── Forbidden letters ─────────────────────────────────────────────────────

    @Test
    fun `VIN containing letter I is rejected`() {
        // I looks like digit 1.
        assertFalse(VinFormat.isValid("WBA3A5G59DNP2608I"))
    }

    @Test
    fun `VIN containing letter O is rejected`() {
        // O looks like digit 0.
        assertFalse(VinFormat.isValid("WBA3A5G59DNP2608O"))
    }

    @Test
    fun `VIN containing letter Q is rejected`() {
        // Q looks like digit 9.
        assertFalse(VinFormat.isValid("WBA3A5G59DNP2608Q"))
    }

    @Test
    fun `VIN with forbidden letter I at position 0 is rejected`() {
        assertFalse(VinFormat.isValid("IBA3A5G59DNP26082"))
    }

    @Test
    fun `VIN with forbidden letter O at position 0 is rejected`() {
        assertFalse(VinFormat.isValid("OBA3A5G59DNP26082"))
    }

    @Test
    fun `VIN with forbidden letter Q at position 0 is rejected`() {
        assertFalse(VinFormat.isValid("QBA3A5G59DNP26082"))
    }

    // ── Length violations ────────────────────────────────────────────────────

    @Test
    fun `VIN with 16 characters is rejected`() {
        assertFalse(VinFormat.isValid("WBA3A5G59DNP2608"))
    }

    @Test
    fun `VIN with 18 characters is rejected`() {
        assertFalse(VinFormat.isValid("WBA3A5G59DNP26082X"))
    }

    @Test
    fun `empty string is rejected`() {
        assertFalse(VinFormat.isValid(""))
    }

    @Test
    fun `single character is rejected`() {
        assertFalse(VinFormat.isValid("W"))
    }

    // ── Case sensitivity ─────────────────────────────────────────────────────

    @Test
    fun `lowercase letters are rejected — no normalisation`() {
        // VIN alphabet is uppercase only; lowercase must not slip through.
        assertFalse(VinFormat.isValid("wba3a5g59dnp26082"))
    }

    @Test
    fun `mixed-case VIN is rejected`() {
        assertFalse(VinFormat.isValid("WBa3A5G59DNP26082"))
    }

    // ── Special characters and whitespace ────────────────────────────────────

    @Test
    fun `VIN with space character is rejected`() {
        assertFalse(VinFormat.isValid("WBA3A5G59DNP2608 "))
    }

    @Test
    fun `VIN with hyphen is rejected`() {
        assertFalse(VinFormat.isValid("WBA-3A5G59DNP2608"))
    }

    @Test
    fun `VIN with dollar sign is rejected`() {
        assertFalse(VinFormat.isValid("WBA3A5G\$9DNP26082"))
    }

    @Test
    fun `VIN with unicode character is rejected`() {
        // 'Ä' is outside ASCII VIN alphabet.
        assertFalse(VinFormat.isValid("ÄBA3A5G59DNP26082"))
    }

    @Test
    fun `VIN with newline character is rejected`() {
        assertFalse(VinFormat.isValid("WBA3A5G59DNP2608\n"))
    }

    // ── VIN_LENGTH constant ───────────────────────────────────────────────────

    @Test
    fun `VIN_LENGTH constant equals 17`() {
        kotlin.test.assertEquals(17, VinFormat.VIN_LENGTH)
    }
}
