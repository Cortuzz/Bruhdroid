package com.example.bruhdroid.model.src

class RuntimeError(message: String = "") : Exception(message)

class UnhandledError(message: String = "") : Exception(message)

class LexerError(message: String = "") : Exception(message)

class TypeError(message: String = "") :
    Exception("FATAL ERROR:\nTypeError: $message\n\nStack traceback:")

class OperationError(message: String = "") :
    Exception("FATAL ERROR:\nOperationError: $message\n\nStack traceback:")

class StackCorruptionError(message: String = "") :
    Exception("FATAL ERROR:\nStackCorruptionError: $message\n\nStack traceback:")

class IndexOutOfRangeError(message: String = "") :
    Exception("FATAL ERROR:\nIndexOutOfRangeError: $message\n\nStack traceback:")

class BlockOutOfCycleContextError(message: String = "") :
    Exception("FATAL ERROR:\nBlockOutOfCycleContextError: $message\n\nStack traceback:")
