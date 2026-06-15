package io.github.kabirnayeem99.viminfo.exceptions

class ChecksumNotAvailableException(region: String) : VinException(
    "Checksum is not available for region '$region'."
)
