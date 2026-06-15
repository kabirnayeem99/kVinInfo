package io.github.kabirnayeem99.viminfo.exceptions

class InvalidVinYearException(yearChar: Char) : VinException(
    "Invalid model year character: '$yearChar'."
)
