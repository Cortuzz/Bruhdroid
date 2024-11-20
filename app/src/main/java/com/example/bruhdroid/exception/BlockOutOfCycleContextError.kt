package com.example.bruhdroid.exception

class BlockOutOfCycleContextError(message: String = "") :
    Exception("FATAL ERROR:\nBlockOutOfCycleContextError: $message\n\nStack traceback:")
