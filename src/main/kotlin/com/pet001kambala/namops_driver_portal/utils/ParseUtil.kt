package com.pet001kambala.namops_driver_portal.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ParseUtil {

    companion object {
        fun path(vararg param: String): String {
            val path = StringBuilder()

            for (p in param)
                path.append(p).append("/")
            return path.toString()
        }


        fun <T> T.toMap(): Map<String, Any> {
            return convert()
        }


        inline fun <I, reified O> I.convert(): O {
            val json = this.toJson()
            return Gson().fromJson(json, object : TypeToken<O>() {}.type)
        }

        inline fun <reified O> String.convert(): O {
            return Gson().fromJson(this, object : TypeToken<O>() {}.type)
        }

        fun <K> K.toJson(): String {
            return Gson().toJson(this)
        }

        fun String?.stripCountryCode(): String? {
            return if (this == null)
                null
            else {
                val match = Regex("^(\\+264)?(\\d+)?(8[1,5]\\d+)$").find(this)
                val (_, _,cell) = match!!.destructured
                "0$cell"
            }
        }

        @JvmStatic
        fun String?.toPhone(): String {

            return "+264${this?.trimStart { it == '0' }}"
        }
    }
}