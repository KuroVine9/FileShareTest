package com.kuro9.fileshare.exception

class NotAuthorizedException(override val message: String) : Exception(message)
class TokenNotValidException(override val message: String) : Exception(message)
