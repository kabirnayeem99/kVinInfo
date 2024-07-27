package io.github.kabirnayeem99.viminfo.exceptions

class InvalidVinYearException(yearChar: Char) :
    Exception("Invalid model year character: '$yearChar'. Expected a valid year character within the allowed range. Please check the VIN and try again.")