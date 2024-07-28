package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Indicates an invalid year character in the VIN.
 *
 * This exception is thrown when the year character in the VIN is not valid.
 *
 * @param yearChar The invalid year character.
 */
class InvalidVinYearException(yearChar: Char) :
    InvalidVinException("Invalid model year character: '$yearChar'. Expected a valid year character within the allowed range. Please check the VIN and try again.")
