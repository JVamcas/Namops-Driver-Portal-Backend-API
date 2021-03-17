package com.pet001kambala.namops_driver_portal.servlets

import com.pet001kambala.namops_driver_portal.model.JobCardItem
import com.pet001kambala.namops_driver_portal.repo.JobCartItemRepo
import com.pet001kambala.namops_driver_portal.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.runBlocking
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "JobCardCardItem", value = ["/job-card-by-container", "/all-job-cards"])
class JobCardItemServlet : HttpServlet() {

    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val out = resp.writer
        val uri = req.requestURI.substring(req.contextPath.length)
        var result: Results

        when (uri) {
            "/job-card-by-container" -> {
                val containerNo = req.getParameter("containerNo")
                runBlocking {
                    try {
                        result = JobCartItemRepo().findJobCardItemByContainerNumber(containerNo)
                        if (result is Results.Success<*>) {
                            val data = (result as Results.Success<*>).data as List<JobCardItem>
                            resp.contentType = "json"
                            out.println(
                                if (data.isNotEmpty())
                                    "{data: ${data[0].toJson()}}"
                                else "{data: \"\"}"
                            )
                        }
                    } catch (e: Exception) {
                        out.println("Err: ${e.message}")
                    }
                }
            }
            "/all-job-cards" -> {
                runBlocking {
                    try {
                        result = JobCartItemRepo().loadAllInCompleteJobCardItems()
                        if (result is Results.Success<*>) {
                            val data = (result as Results.Success<*>).data as List<JobCardItem>
                            resp.contentType = "json"
                            out.println(
                                if (data.isNotEmpty())
                                    "{data: ${data.toJson()}}"
                                else "{data: \"\"}"
                            )
                        }
                    } catch (e: Exception) {
                        out.println("Err: ${e.message}")
                    }
                }
            }
            else -> {
                out.println("Err: Do not know you! $uri")
            }
        }
    }
}