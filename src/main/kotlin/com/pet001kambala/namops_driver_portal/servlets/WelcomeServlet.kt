package com.pet001kambala.namops_driver_portal.servlets

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name="welcome", value=["/"])
class WelcomeServlet: HttpServlet() {

    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        resp.writer.print("What do you want?")
    }
}