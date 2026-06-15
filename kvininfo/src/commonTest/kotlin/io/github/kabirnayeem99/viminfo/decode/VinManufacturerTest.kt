package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidWmiException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VinManufacturerTest {

    // ── isSmallVolume ─────────────────────────────────────────────────────────

    @Test
    fun `isSmallVolume returns true when 3rd WMI char is 9`() {
        // "SA9..." — pos 2 (0-indexed) = '9'
        assertTrue(VinManufacturer.isSmallVolume("SA9B0000CLA019123"))
    }

    @Test
    fun `isSmallVolume returns false when 3rd WMI char is not 9`() {
        assertFalse(VinManufacturer.isSmallVolume("WBA3A5G59DNP26082"))
    }

    @Test
    fun `isSmallVolume returns false for a digit-suffix WMI other than 9`() {
        assertFalse(VinManufacturer.isSmallVolume("1G1000000000000000".take(17)))
    }

    // ── resolve — 3-char WMI direct hit ──────────────────────────────────────

    @Test
    fun `known 3-char WMI resolves to manufacturer name`() {
        // "WBA" → "BMW" is in the manufacturers map.
        assertEquals("BMW", VinManufacturer.resolve("WBA3A5G59DNP26082"))
    }

    @Test
    fun `known North American 3-char WMI resolves correctly`() {
        // "1G1" → "Chevrolet" is in the manufacturers map.
        assertEquals("Chevrolet", VinManufacturer.resolve("1G100000000000000"))
    }

    // ── resolve — 2-char prefix fallback ─────────────────────────────────────

    @Test
    fun `unknown 3-char WMI falls back to 2-char prefix`() {
        // "1GX" is not in the 3-char map; "1G" → "General Motors" is in the 2-char map.
        // Result must be "General Motors", not an exception.
        assertEquals("General Motors", VinManufacturer.resolve("1GX00000000000000"))
    }

    @Test
    fun `another 2-char fallback resolves Honda via 1H prefix`() {
        // "1HZ" unlikely in 3-char map; "1H" → "Honda".
        assertEquals("Honda", VinManufacturer.resolve("1HZ00000000000000"))
    }

    // ── resolve — resolution order: 3-char beats 2-char ─────────────────────

    @Test
    fun `3-char WMI hit takes precedence over matching 2-char prefix`() {
        // "1G1" is in the 3-char map as "Chevrolet".
        // "1G" is also in the 2-char map as "General Motors".
        // 3-char lookup runs first; must return "Chevrolet", not "General Motors".
        val result = VinManufacturer.resolve("1G100000000000000")
        assertEquals("Chevrolet", result)
    }

    // ── resolve — small-volume extended id ───────────────────────────────────

    @Test
    fun `small-volume VIN resolves via extended id WMI and positions 12 to 14`() {
        // WMI "SA9", extended id suffix at indices 11-13 = "019" → "SA9019" → "TVR".
        assertEquals("TVR", VinManufacturer.resolve("SA9B0000CLA019123"))
    }

    @Test
    fun `extended id indices are exactly 11 12 13 — not 10 or 14`() {
        // Build a VIN where suffix "019" sits precisely at indices 11,12,13.
        // VIN: SA9 + AAAAAAAA + 019 + AAA  (3+8+3+3 = 17)
        val vin = "SA9AAAAAAAA019AAA"
        assertEquals(17, vin.length)
        assertEquals('0', vin[11])
        assertEquals('1', vin[12])
        assertEquals('9', vin[13])
        assertEquals("TVR", VinManufacturer.resolve(vin))
    }

    @Test
    fun `shifting extended id by one position does not match and falls back`() {
        // Put "019" at indices 10,11,12 instead of 11,12,13 — extended id becomes "SA9" + "01X" ≠ "SA9019".
        val vin = "SA9AAAAAAA019AAAA"
        // extended id = "SA9" + vin[11..13] = "SA9" + "19A" = "SA919A" → not in map
        // falls back to "SA9" → "Morgan"
        assertEquals("Morgan", VinManufacturer.resolve(vin))
    }

    @Test
    fun `small-volume VIN with unknown extended id falls back to 3-char WMI`() {
        // "SA9ZZZ" is not in the map; "SA9" → "Morgan".
        val vin = "SA9AAAAAAAAZZAAAA"
        // extended = "SA9" + vin[11..13] = "SA9" + "ZAA" → not in map
        // tries "SA9" → "Morgan"
        assertEquals("Morgan", VinManufacturer.resolve(vin))
    }

    // ── resolve — small-volume, VIN too short for extended lookup ─────────────

    @Test
    fun `small-volume WMI with VIN length 13 throws InvalidVinLengthException`() {
        // Extended id requires indices 11-13 (positions 12-14), so len >= 14 is required.
        // Without the full id, we cannot distinguish TVR from Morgan from Koenigsegg.
        val shortVin = "SA9AAAAAAAAAA"  // 13 chars
        assertEquals(13, shortVin.length)
        assertFailsWith<InvalidVinLengthException> {
            VinManufacturer.resolve(shortVin)
        }
    }

    @Test
    fun `small-volume WMI with VIN length exactly 14 performs extended lookup`() {
        // len=14 == EXTENDED_ID_END: extended id suffix at [11..13] = "019" → "SA9019" → "TVR".
        val vin = "SA9AAAAAAAA019"  // 14 chars
        assertEquals(14, vin.length)
        assertEquals("TVR", VinManufacturer.resolve(vin))
    }

    // ── resolve — length guard ────────────────────────────────────────────────

    @Test
    fun `VIN with 2 characters throws InvalidVinLengthException`() {
        assertFailsWith<InvalidVinLengthException> {
            VinManufacturer.resolve("SA")
        }
    }

    @Test
    fun `empty VIN throws InvalidVinLengthException`() {
        assertFailsWith<InvalidVinLengthException> {
            VinManufacturer.resolve("")
        }
    }

    // ── resolve — no match → InvalidWmiException ─────────────────────────────

    @Test
    fun `completely unknown WMI throws InvalidWmiException`() {
        // "ZZZ" has no 3-char or 2-char entry in the manufacturers map.
        assertFailsWith<InvalidWmiException> {
            VinManufacturer.resolve("ZZZ00000000000000")
        }
    }

    @Test
    fun `InvalidWmiException carries the failing WMI`() {
        val ex = assertFailsWith<InvalidWmiException> {
            VinManufacturer.resolve("ZZZ00000000000000")
        }
        assertTrue(ex.message?.contains("ZZZ") == true, "Exception message should mention the bad WMI")
    }
}
