package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Indicates that the NHTSA database API client has already been closed.
 *
 * This exception is thrown when attempting to use the NHTSA API after it has been closed.
 */
class NhtsaDatabaseAlreadyClosedException :
    Exception("The NHTSA database API client has already been closed.")
