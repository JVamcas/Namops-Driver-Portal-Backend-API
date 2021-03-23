package com.pet001kambala.namops_driver_portal.utils

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.pet001kambala.namops_driver_portal.model.Driver
import java.lang.reflect.Type
import java.net.http.HttpResponse
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ParseUtil {

    class LocalDateTimeSerializer : JsonSerializer<LocalDateTime?> {
        override fun serialize(
            localDateTime: LocalDateTime?,
            srcType: Type?,
            context: JsonSerializationContext
        ): JsonElement? {

            return if (localDateTime == null) null
            else {
                val year = localDateTime.year
                val month = localDateTime.month
                val day = localDateTime.dayOfMonth

                val hour = localDateTime.hour
                val minute = localDateTime.minute
                val seconds = localDateTime.second
                val json =
                    "{date:{year:$year,month:$month,day:$day},time:{hour:$hour,minute:$minute,second:$seconds}}"
                JsonParser.parseString(json).asJsonObject
            }

        }
    }
    class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime?> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext
        ): LocalDateTime? {
            return if (json != null && !json.isJsonNull) {
                val date = json.asJsonObject.get("date").asJsonObject
                val time = json.asJsonObject.get("time").asJsonObject

                val year = date.get("year").asInt
                val month = date.monthIndex()
                val day = date.get("day").asInt

                val hour = time.get("hour").asInt
                val minute = time.get("minute").asInt
                val seconds = time.get("second").asInt

                LocalDateTime.of(year, month, day, hour, minute, seconds)

            } else null
        }
    }

    object ParseGson {
        var gson: Gson? = null
        val builder = GsonBuilder()
            .apply {
                registerTypeAdapter(LocalDateTime::class.java, ParseUtil.LocalDateTimeSerializer())
                registerTypeAdapter(
                    LocalDateTime::class.java,
                    LocalDateTimeDeserializer()
                )
            }

        init {
            gson = if (gson == null) builder.setPrettyPrinting().create() else gson
        }

    }

    companion object {
        val gson: Gson by lazy { ParseGson.gson!! }
        
        fun <T> T.toMap(): Map<String, Any> {
            return convert()
        }
        fun JsonObject.monthIndex(): Int {
            val value = this.get("month")
            return try {
                value.asInt
            } catch (e: NumberFormatException) {
                val date: Date = SimpleDateFormat("MMMM").parse(value.asString)
                val cal = Calendar.getInstance()
                cal.time = date
                cal[Calendar.MONTH] + 1
            }
        }


        inline fun <I, reified O> I.convert(): O {
            val json = this.toJson()
            return gson.fromJson(json, object : TypeToken<O>() {}.type)
        }

        inline fun <reified O> String.convert(): O {
            return gson.fromJson(this, object : TypeToken<O>() {}.type)
        }

        fun <K> K.toJson(): String {
            return gson.toJson(this)
        }

        fun String?.stripCountryCode(): String? {
            return if (this == null)
                null
            else {
                val match = Regex("^(\\+264)?(\\d+)?(8[1,5]\\d+)$").find(this)
                val (_, _, cell) = match!!.destructured
                "0$cell"
            }
        }

        @JvmStatic
        fun String?.toPhone(): String {

            return "+264${this?.trimStart { it == '0' }}"
        }
    }
}