package com.example.bruhdroid.exception

class StackCorruptionError(message: String = "") :
    Exception("FATAL ERROR:\nStackCorruptionError: $message\n\nStack traceback:")