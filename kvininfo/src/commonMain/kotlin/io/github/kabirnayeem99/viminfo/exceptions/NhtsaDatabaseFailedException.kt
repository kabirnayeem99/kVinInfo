package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Indicates a failure to retrieve data from the NHTSA database.
 *
 * This exception is thrown when there's an issue accessing or parsing data from the NHTSA database.
 *
 * @param message An optional message providing additional details about the failure.
 */
class NhtsaDatabaseFailedException(message: String? = "") :
    Exception("Failed to find the value in the NHTSA database. ${message ?: ""}")
