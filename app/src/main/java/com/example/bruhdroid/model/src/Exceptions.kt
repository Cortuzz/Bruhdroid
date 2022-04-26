package com.example.bruhdroid.model.src

class RuntimeError(message: String = ""): Exception(message)

class LexerError(message: String = ""): Exception(message)

class SyntaxError(message: String = ""): Exception("SyntaxError: $message")

class TypeError(message: String = ""):
    Exception("FATAL ERROR:\nTypeError: $message\nStack traceback:")

class StackCorruptionError(message: String = ""):
    Exception("FATAL ERROR:\nStackCorruptionError: $message\nStack traceback:")

class HeapCorruptionError(message: String = ""):
    Exception("FATAL ERROR:\nHeapCorruptionError: $message\nStack traceback:")

class BadInstructionError(message: String = ""):
    Exception("FATAL ERROR:\nBadInstructionError: $message\nStack traceback:")