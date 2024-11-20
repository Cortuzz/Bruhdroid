package com.example.bruhdroid.exception

class TypeError(message: String = "") :
    Exception("FATAL ERROR:\nTypeError: $message\n\nStack traceback:")