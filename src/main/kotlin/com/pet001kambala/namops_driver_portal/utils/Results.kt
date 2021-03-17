package com.pet001kambala.namops_driver_portal.utils

import java.net.ConnectException
import java.net.SocketException

sealed class Results {

    object Loading : Results()

    class Success<T>(
            val data: T? = null,
            val code: CODE
    ) : Results() {
        enum class CODE {
            WRITE_SUCCESS,
            UPDATE_SUCCESS,
            LOAD_SUCCESS,
            DELETE_SUCCESS,
        }
    }

    class Error(e: Exception) : Results() {
        enum class CODE {

            NO_CONNECTION,
            UNKNOWN
        }

        val code: CODE = when (e) {

            is SocketException -> CODE.NO_CONNECTION
            is ConnectException -> CODE.NO_CONNECTION
            else -> {
                CODE.UNKNOWN
            }
        }
    }
}