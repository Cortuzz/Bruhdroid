package com.example.bruhdroid.model

class RuntimeError(message: String = ""): Exception(message)

class SyntaxError(message: String = ""):
    Exception("FATAL EXCEPTION:\nSyntaxError: $message\nStack traceback:")

class TypeError(message: String = ""):
    Exception("FATAL EXCEPTION:\nTypeError: $message\nStack traceback:")

class StackCorruptionError(message: String = ""):
    Exception("FATAL EXCEPTION:\nStackCorruptionError: $message\nStack traceback:")

class HeapCorruptionError(message: String = ""):
    Exception("FATAL EXCEPTION:\nHeapCorruptionError: $message\nStack traceback:")

class BadInstructionError(message: String = ""):
    Exception("FATAL EXCEPTION:\nBadInstructionError: $message\nStack traceback:")