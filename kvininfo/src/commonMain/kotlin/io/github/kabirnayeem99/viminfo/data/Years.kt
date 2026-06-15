package io.github.kabirnayeem99.viminfo.data

/**
 * Maps the model-year character (VIN position 10) to its base year in the first
 * 30-year cycle (1980–2009).
 *
 * The model-year code repeats every 30 years, so a single character is ambiguous on its own
 * (e.g. `A` is 1980, 2010 and 2040). VIN position 7 disambiguates the cycle: a numeric position 7
 * means the 1980–2009 cycle, an alphabetic position 7 means the 2010–2039 cycle.
 *
 * The codes skip the letters I, O, Q, U, Z and the digit 0, giving exactly 30 valid codes.
 */
val yearCodeBaseValues =
    mapOf<Char, Int>(
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

/** Length of one full model-year code cycle, in years. */
const val YEAR_CODE_CYCLE_LENGTH = 30
