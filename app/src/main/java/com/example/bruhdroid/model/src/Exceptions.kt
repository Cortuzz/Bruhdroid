package com.example.bruhdroid.model.src

class RuntimeError(message: String = ""): Exception(message)

class LexerError(message: String = ""): Exception(message)

class SyntaxError(message: String = ""): Exception("SyntaxError: $message")

class TypeError(message: String = ""):
    Exception("FATAL ERROR:\nTypeError: $message\n\nStack traceback:")

class OperationError(message: String = ""):
    Exception("FATAL ERROR:\nOperationError: $message\n\nStack traceback:")

class StackCorruptionError(message: String = ""):
    Exception("FATAL ERROR:\nStackCorruptionError: $message\n\nStack traceback:")

class HeapCorruptionError(message: String = ""):
    Exception("FATAL ERROR:\nHeapCorruptionError: $message\n\nStack traceback:")

class BadInstructionError(message: String = ""):
    Exception("FATAL ERROR:\nBadInstructionError: $message\n\nStack traceback:")