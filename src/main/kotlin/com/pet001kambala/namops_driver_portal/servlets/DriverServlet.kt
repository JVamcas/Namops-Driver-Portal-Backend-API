package com.pet001kambala.namops_driver_portal.servlets

import com.pet001kambala.namops_driver_portal.model.Driver
import com.pet001kambala.namops_driver_portal.repo.DriverRepo
import com.pet001kambala.namops_driver_portal.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "drivers", value = ["/drivers"])
class DriverServlet : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        val passcode = req.getParameter("passcode")
        runBlocking {
            try {
                val results = DriverRepo().findDriverByPassCode(passcode)
                if (results is Results.Success<*>) {
                    val data = results.data as List<Driver>
                    resp.contentType = "json"
                    resp.writer.println(
                        if (data.isNotEmpty())
                            "{data: ${data[0].toJson()}}"
                        else "{data: \"\"}"
                    )
                } else resp.writer.println("{Err: Server error!}")
            } catch (e: Exception) {
                resp.writer.println("{Err: ${e.message}}")
            }
        }
    }
}