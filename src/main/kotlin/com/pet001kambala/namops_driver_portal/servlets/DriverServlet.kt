package com.pet001kambala.namops_driver_portal.servlets

import com.pet001kambala.namops_driver_portal.model.Driver
import com.pet001kambala.namops_driver_portal.repo.DriverRepo
import com.pet001kambala.namops_driver_portal.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.runBlocking
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "drivers", value = ["/drivers"])
class DriverServlet : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        val surname = req.getParameter("surname")
        val passcode = req.getParameter("passcode")
        runBlocking {
            try {
                val results = DriverRepo().findDriver(surname,passcode)
                if (results is Results.Success<*>) {
                    val data = results.data as List<Driver>
                    resp.contentType = "application/json"
                    resp.writer.print(
                        if (data.isNotEmpty())
                            "{Status:\"Success\",data: ${data[0].toJson()}}"
                        else "{Status:\"Invalid Auth\"}"
                    )
                } else resp.writer.print("{Status: \"Server error\"}")
            } catch (e: Exception) {
                resp.writer.print("{Status: \"${e.message}\"}")
            }
        }
    }
}