package com.example.bruhdroid.exception

class OperationError(message: String = "") :
    Exception("FATAL ERROR:\nOperationError: $message\n\nStack traceback:")