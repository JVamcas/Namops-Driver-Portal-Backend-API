package com.pet001kambala.namops_driver_portal.servlets

import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.runBlocking
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "Trips", value = ["/trip", "/trip-update"])
class TripServlet : HttpServlet() {

    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        val id = req.getParameter("id")

        runBlocking {

        }
    }

    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {

        val trip = req.getParameter("trip")
        val out = resp.writer
        val uri = req.requestURI.substring(req.contextPath.length)
        var result: Results

        runBlocking {
            when (uri) {
                "/trip" -> {

                }
                else -> {
                    //trip update
                }
            }
        }
    }

    public override fun doDelete(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id")
        runBlocking {

        }
    }
}