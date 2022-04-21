package com.example.bruhdroid.model

class SyntaxError(message: String = ""): Exception(message)

class TypeError(message: String = ""): Exception(message)

class StackCorruptionError(message: String = ""): Exception(message)

class HeapCorruptionError(message: String = ""): Exception(message)
