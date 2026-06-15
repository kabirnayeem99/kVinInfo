package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinRegionCharException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VinRegionTest {

    // helper: only position 0 matters for region; rest is padding
    private fun vinStartingWith(c: Char) = "${c}0000000000000000"

    // ── code — range boundaries ───────────────────────────────────────────────

    @Test
    fun `A maps to Africa AF`() {
        assertEquals("AF", VinRegion.code(vinStartingWith('A')))
    }

    @Test
    fun `C maps to Africa AF — upper boundary of A-C block`() {
        assertEquals("AF", VinRegion.code(vinStartingWith('C')))
    }

    @Test
    fun `E maps to Europe EU`() {
        assertEquals("EU", VinRegion.code(vinStartingWith('E')))
    }

    @Test
    fun `H maps to Asia AS — lower boundary of H-R block`() {
        assertEquals("AS", VinRegion.code(vinStartingWith('H')))
    }

    @Test
    fun `R maps to Asia AS — upper boundary of H-R block`() {
        assertEquals("AS", VinRegion.code(vinStartingWith('R')))
    }

    @Test
    fun `S maps to Europe EU — lower boundary of S-Z block`() {
        assertEquals("EU", VinRegion.code(vinStartingWith('S')))
    }

    @Test
    fun `Z maps to Europe EU — upper boundary of S-Z block`() {
        assertEquals("EU", VinRegion.code(vinStartingWith('Z')))
    }

    @Test
    fun `1 maps to North America NA — lower boundary of 1-5 block`() {
        assertEquals("NA", VinRegion.code(vinStartingWith('1')))
    }

    @Test
    fun `5 maps to North America NA — upper boundary of 1-5 block`() {
        assertEquals("NA", VinRegion.code(vinStartingWith('5')))
    }

    @Test
    fun `6 maps to Oceania OC`() {
        assertEquals("OC", VinRegion.code(vinStartingWith('6')))
    }

    @Test
    fun `7 maps to North America NA`() {
        assertEquals("NA", VinRegion.code(vinStartingWith('7')))
    }

    @Test
    fun `8 maps to South America SA — lower boundary of 8-9 block`() {
        assertEquals("SA", VinRegion.code(vinStartingWith('8')))
    }

    @Test
    fun `9 maps to South America SA — upper boundary of 8-9 block`() {
        assertEquals("SA", VinRegion.code(vinStartingWith('9')))
    }

    // ── code — gap / unassigned chars ────────────────────────────────────────

    @Test
    fun `D throws InvalidVinRegionCharException — gap between C and E`() {
        // ISO 3780 assigns A-C to Africa but D is unassigned in this implementation.
        // Possible ISO mismatch: spec may assign D to Africa too.
        assertFailsWith<InvalidVinRegionCharException> {
            VinRegion.code(vinStartingWith('D'))
        }
    }

    @Test
    fun `F maps to Europe EU — France per ISO 3780`() {
        assertEquals("EU", VinRegion.code(vinStartingWith('F')))
    }

    @Test
    fun `G maps to Europe EU — Great Britain per ISO 3780`() {
        assertEquals("EU", VinRegion.code(vinStartingWith('G')))
    }

    @Test
    fun `digit 0 as first char throws InvalidVinRegionCharException`() {
        assertFailsWith<InvalidVinRegionCharException> {
            VinRegion.code(vinStartingWith('0'))
        }
    }

    @Test
    fun `lowercase first char throws — no normalisation`() {
        // 'a' is not in any range; falls to else branch.
        assertFailsWith<InvalidVinRegionCharException> {
            VinRegion.code(vinStartingWith('a'))
        }
    }

    // ── code — empty string ───────────────────────────────────────────────────

    @Test
    fun `empty string throws InvalidVinLengthException`() {
        assertFailsWith<InvalidVinLengthException> {
            VinRegion.code("")
        }
    }

    // ── name — all six region names ───────────────────────────────────────────

    @Test
    fun `Africa region name`() {
        assertEquals("Africa", VinRegion.name(vinStartingWith('A')))
    }

    @Test
    fun `Asia region name`() {
        assertEquals("Asia", VinRegion.name(vinStartingWith('J')))
    }

    @Test
    fun `Europe region name — via E`() {
        assertEquals("Europe", VinRegion.name(vinStartingWith('E')))
    }

    @Test
    fun `Europe region name — via S-Z block`() {
        assertEquals("Europe", VinRegion.name(vinStartingWith('W')))
    }

    @Test
    fun `North America region name`() {
        assertEquals("North America", VinRegion.name(vinStartingWith('1')))
    }

    @Test
    fun `Oceania region name`() {
        assertEquals("Oceania", VinRegion.name(vinStartingWith('6')))
    }

    @Test
    fun `South America region name`() {
        assertEquals("South America", VinRegion.name(vinStartingWith('8')))
    }

    // ── requiresCheckDigit ────────────────────────────────────────────────────

    @Test
    fun `NA chars 1 through 5 require check digit`() {
        for (c in '1'..'5') {
            assertTrue(VinRegion.requiresCheckDigit(vinStartingWith(c)), "char '$c' should require check digit")
        }
    }

    @Test
    fun `NA char 7 requires check digit`() {
        assertTrue(VinRegion.requiresCheckDigit(vinStartingWith('7')))
    }

    @Test
    fun `H requires check digit — China`() {
        assertTrue(VinRegion.requiresCheckDigit(vinStartingWith('H')))
    }

    @Test
    fun `L requires check digit — China`() {
        assertTrue(VinRegion.requiresCheckDigit(vinStartingWith('L')))
    }

    @Test
    fun `European VIN E does not require check digit`() {
        assertFalse(VinRegion.requiresCheckDigit(vinStartingWith('E')))
    }

    @Test
    fun `Oceania VIN 6 does not require check digit`() {
        assertFalse(VinRegion.requiresCheckDigit(vinStartingWith('6')))
    }

    @Test
    fun `South American VIN 8 does not require check digit`() {
        assertFalse(VinRegion.requiresCheckDigit(vinStartingWith('8')))
    }

    @Test
    fun `African VIN A does not require check digit`() {
        assertFalse(VinRegion.requiresCheckDigit(vinStartingWith('A')))
    }

    @Test
    fun `requiresCheckDigit on empty string throws InvalidVinLengthException`() {
        assertFailsWith<InvalidVinLengthException> {
            VinRegion.requiresCheckDigit("")
        }
    }
}
