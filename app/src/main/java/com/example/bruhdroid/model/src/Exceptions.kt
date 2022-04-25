package com.example.bruhdroid.model.src

class RuntimeError(message: String = ""): Exception(message)

class LexerError(message: String = ""): Exception(message)

class SyntaxError(message: String = ""): Exception("SyntaxError: $message")

class TypeError(message: String = ""):
    Exception("\nFATAL ERROR:\nTypeError: $message\nStack traceback:")

class StackCorruptionError(message: String = ""):
    Exception("\nFATAL ERROR:\nStackCorruptionError: $message\nStack traceback:")

class HeapCorruptionError(message: String = ""):
    Exception("\nFATAL ERROR:\nHeapCorruptionError: $message\nStack traceback:")

class BadInstructionError(message: String = ""):
    Exception("\nFATAL ERROR:\nBadInstructionError: $message\nStack traceback:")