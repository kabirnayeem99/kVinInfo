package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinYearException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VinModelYearTest {
    // ── VIN template helpers ──────────────────────────────────────────────────
    //
    // Template: "AAAAAA" + pos7Char + "AA" + yearChar + "AAAAAAA"  (total 17)
    //   index 6  = position 7  (determines cycle: digit→1980-2009, letter→2010-2039)
    //   index 9  = position 10 (year code character)
    //
    private fun vinWith(
        pos7: Char,
        yearChar: Char,
    ): String = "AAAAAA${pos7}AA${yearChar}AAAAAAA"

    // ── yearCharacter extraction ──────────────────────────────────────────────

    @Test
    fun `yearCharacter returns character at index 9`() {
        val vin = "AAAAAAAAA" + "K" + "AAAAAAA" // index 9 = 'K'
        assertEquals('K', VinModelYear.yearCharacter(vin))
    }

    @Test
    fun `yearCharacter throws for VIN shorter than 10 characters`() {
        assertFailsWith<InvalidVinLengthException> {
            VinModelYear.yearCharacter("AAAAAAAAA") // 9 chars, index 9 missing
        }
    }

    @Test
    fun `yearCharacter throws for empty string`() {
        assertFailsWith<InvalidVinLengthException> {
            VinModelYear.yearCharacter("")
        }
    }

    // ── all 30 year codes — numeric pos7 (1980–2009 cycle) ───────────────────

    @Test
    fun `all 30 year codes with numeric position 7 decode to 1980-2009 cycle`() {
        val numericPos7 = '1' // digit → old cycle
        val expectedYears =
            mapOf(
                'A' to 1980,
                'B' to 1981,
                'C' to 1982,
                'D' to 1983,
                'E' to 1984,
                'F' to 1985,
                'G' to 1986,
                'H' to 1987,
                'J' to 1988,
                'K' to 1989,
                'L' to 1990,
                'M' to 1991,
                'N' to 1992,
                'P' to 1993,
                'R' to 1994,
                'S' to 1995,
                'T' to 1996,
                'V' to 1997,
                'W' to 1998,
                'X' to 1999,
                'Y' to 2000,
                '1' to 2001,
                '2' to 2002,
                '3' to 2003,
                '4' to 2004,
                '5' to 2005,
                '6' to 2006,
                '7' to 2007,
                '8' to 2008,
                '9' to 2009,
            )
        for ((yearChar, expectedYear) in expectedYears) {
            val vin = vinWith(numericPos7, yearChar)
            assertEquals(
                expectedYear,
                VinModelYear.decode(vin),
                "Year code '$yearChar' with numeric pos7 should decode to $expectedYear",
            )
        }
    }

    // ── all 30 year codes — alpha pos7 (2010–2039 cycle) ─────────────────────

    @Test
    fun `all 30 year codes with alpha position 7 decode to 2010-2039 cycle`() {
        val alphaPos7 = 'A' // letter → new cycle (+30)
        val expectedYears =
            mapOf(
                'A' to 2010,
                'B' to 2011,
                'C' to 2012,
                'D' to 2013,
                'E' to 2014,
                'F' to 2015,
                'G' to 2016,
                'H' to 2017,
                'J' to 2018,
                'K' to 2019,
                'L' to 2020,
                'M' to 2021,
                'N' to 2022,
                'P' to 2023,
                'R' to 2024,
                'S' to 2025,
                'T' to 2026,
                'V' to 2027,
                'W' to 2028,
                'X' to 2029,
                'Y' to 2030,
                '1' to 2031,
                '2' to 2032,
                '3' to 2033,
                '4' to 2034,
                '5' to 2035,
                '6' to 2036,
                '7' to 2037,
                '8' to 2038,
                '9' to 2039,
            )
        for ((yearChar, expectedYear) in expectedYears) {
            val vin = vinWith(alphaPos7, yearChar)
            assertEquals(
                expectedYear,
                VinModelYear.decode(vin),
                "Year code '$yearChar' with alpha pos7 should decode to $expectedYear",
            )
        }
    }

    // ── boundary: 'A' (first code) ────────────────────────────────────────────

    @Test
    fun `A with numeric pos7 decodes to 1980`() {
        assertEquals(1980, VinModelYear.decode(vinWith('1', 'A')))
    }

    @Test
    fun `A with alpha pos7 decodes to 2010`() {
        assertEquals(2010, VinModelYear.decode(vinWith('A', 'A')))
    }

    // ── boundary: '9' (last code) ─────────────────────────────────────────────

    @Test
    fun `9 with numeric pos7 decodes to 2009`() {
        assertEquals(2009, VinModelYear.decode(vinWith('1', '9')))
    }

    @Test
    fun `9 with alpha pos7 decodes to 2039`() {
        assertEquals(2039, VinModelYear.decode(vinWith('A', '9')))
    }

    // ── real-world VIN crosscheck ─────────────────────────────────────────────

    @Test
    fun `known BMW VIN decodes to 2013`() {
        // WBA3A5G59DNP26082 — pos7='5' (digit), year char='D' → 1983+30? No:
        // pos7 index 6 = 'G', which is a letter → 2010-2039 cycle; 'D' base 1983 → 1983+30=2013.
        assertEquals(2013, VinModelYear.decode("WBA3A5G59DNP26082"))
    }

    @Test
    fun `known Honda VIN decodes to 1991`() {
        // 1HGBH41JXMN109186 — pos7 index 6 = '1' (digit) → old cycle; year char 'M' base 1991.
        assertEquals(1991, VinModelYear.decode("1HGBH41JXMN109186"))
    }

    // ── invalid year characters ───────────────────────────────────────────────

    @Test
    fun `year char I throws InvalidVinYearException`() {
        assertFailsWith<InvalidVinYearException> { VinModelYear.decode(vinWith('1', 'I')) }
    }

    @Test
    fun `year char O throws InvalidVinYearException`() {
        assertFailsWith<InvalidVinYearException> { VinModelYear.decode(vinWith('1', 'O')) }
    }

    @Test
    fun `year char Q throws InvalidVinYearException`() {
        assertFailsWith<InvalidVinYearException> { VinModelYear.decode(vinWith('1', 'Q')) }
    }

    @Test
    fun `year char U throws InvalidVinYearException`() {
        assertFailsWith<InvalidVinYearException> { VinModelYear.decode(vinWith('1', 'U')) }
    }

    @Test
    fun `year char Z throws InvalidVinYearException`() {
        assertFailsWith<InvalidVinYearException> { VinModelYear.decode(vinWith('1', 'Z')) }
    }

    @Test
    fun `year char 0 throws InvalidVinYearException`() {
        // '0' is explicitly excluded from the year code table.
        assertFailsWith<InvalidVinYearException> { VinModelYear.decode(vinWith('1', '0')) }
    }

    // ── VIN too short ─────────────────────────────────────────────────────────

    @Test
    fun `decode throws for VIN shorter than 10 characters`() {
        // yearCharacter(vin) reads index 9 first → throws before positionSeven check.
        assertFailsWith<InvalidVinLengthException> {
            VinModelYear.decode("AAAAAAAAA") // 9 chars
        }
    }

    @Test
    fun `decode throws for empty string`() {
        assertFailsWith<InvalidVinLengthException> {
            VinModelYear.decode("")
        }
    }

    // ── exception ordering: len=7 throws at yearCharacter (index 9), not positionSeven (index 6) ──

    @Test
    fun `VIN of length 7 throws InvalidVinLengthException from yearCharacter not positionSeven`() {
        // index 6 (pos7) exists for len=7, but index 9 (year char) does not.
        // yearCharacter is called first in decode, so InvalidVinLengthException fires there.
        assertFailsWith<InvalidVinLengthException> {
            VinModelYear.decode("AAAAAAA") // 7 chars — pos7 readable, year char not
        }
    }

    // ── pos7 digit vs alpha boundary ─────────────────────────────────────────

    @Test
    fun `pos7 digit 0 is treated as digit — routes to 1980-2009 cycle`() {
        // '0'.isDigit() == true in Kotlin.
        assertEquals(1980, VinModelYear.decode(vinWith('0', 'A')))
    }

    @Test
    fun `pos7 digit 9 is treated as digit — routes to 1980-2009 cycle`() {
        assertEquals(1988, VinModelYear.decode(vinWith('9', 'J'))) // J → 1988
    }

    @Test
    fun `pos7 letter Z is treated as alpha — routes to 2010-2039 cycle`() {
        assertEquals(2010, VinModelYear.decode(vinWith('Z', 'A'))) // A → 1980+30=2010
    }

    // ── possibleYears — returns both cycle candidates ─────────────────────────

    @Test
    fun `possibleYears returns both cycle options for year code S`() {
        // 'S' base = 1995; second option = 1995+30 = 2025.
        // Callers that cannot trust the pos7 heuristic should use this.
        assertEquals(listOf(1995, 2025), VinModelYear.possibleYears(vinWith('1', 'S')))
    }

    @Test
    fun `possibleYears returns both cycle options for year code A`() {
        assertEquals(listOf(1980, 2010), VinModelYear.possibleYears(vinWith('1', 'A')))
    }

    @Test
    fun `possibleYears result is identical regardless of pos7`() {
        // possibleYears ignores pos7 — it just returns both cycles.
        val withDigitPos7 = VinModelYear.possibleYears(vinWith('1', 'S'))
        val withAlphaPos7 = VinModelYear.possibleYears(vinWith('A', 'S'))
        assertEquals(withDigitPos7, withAlphaPos7)
    }

    @Test
    fun `possibleYears throws for invalid year char`() {
        assertFailsWith<InvalidVinYearException> {
            VinModelYear.possibleYears(vinWith('1', 'I'))
        }
    }

    @Test
    fun `possibleYears throws for VIN too short`() {
        assertFailsWith<InvalidVinLengthException> {
            VinModelYear.possibleYears("AAAAAAAAA") // 9 chars
        }
    }

    // ── decode heuristic — documented ambiguity ────────────────────────────────

    @Test
    fun `decode with digit pos7 returns 1980-2009 cycle — use possibleYears when ambiguous`() {
        // The heuristic picks 1995 for 'S' with digit pos7.
        // A 2025 vehicle whose VDS happens to have digit pos7 would be mislabelled.
        // Use possibleYears to surface both options when the heuristic is unreliable.
        assertEquals(1995, VinModelYear.decode(vinWith('1', 'S')))
        assertEquals(listOf(1995, 2025), VinModelYear.possibleYears(vinWith('1', 'S')))
    }
}
