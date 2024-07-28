package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Indicates that a checksum is not available for EU VINs.
 *
 * This exception is thrown when attempting to access the checksum for a European VIN, which doesn't have a checksum character.
 */
class NoChecksumForEuException : InvalidVinException(
    "No checksum character available for EU VINs. European VINs do not have a checksum."
)