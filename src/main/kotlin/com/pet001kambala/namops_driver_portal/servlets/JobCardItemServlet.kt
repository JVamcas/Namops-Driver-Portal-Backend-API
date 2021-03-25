package com.pet001kambala.namops_driver_portal.servlets

import com.pet001kambala.namops_driver_portal.model.Driver
import com.pet001kambala.namops_driver_portal.model.JobCardItem
import com.pet001kambala.namops_driver_portal.repo.DriverRepo
import com.pet001kambala.namops_driver_portal.repo.JobCartItemRepo
import com.pet001kambala.namops_driver_portal.utils.ParseUtil.Companion.convert
import com.pet001kambala.namops_driver_portal.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.runBlocking
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "JobCardCardItem", value = ["/job_card_by_container", "/all_job_cards", "/job_card_item_update"])
class JobCardItemServlet : HttpServlet() {

    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val out = resp.writer
        try {
            runBlocking {
                val uri = req.requestURI.substring(req.contextPath.length)
                val result: Results
                val passcode = req.getParameter("passcode")
                val results = DriverRepo().findDriverByPassCode(passcode)
                if (results is Results.Success<*> && !(results.data as? ArrayList<Driver>).isNullOrEmpty()) {
                    when (uri) {
                        "/job_card_by_container" -> {
                            val containerNo = req.getParameter("containerNo")

                            try {
                                result = JobCartItemRepo().findJobCardItemByContainerNumber(containerNo)
                                if (result is Results.Success<*>) {
                                    val data = result.data as List<JobCardItem>
                                    resp.contentType = "application/json"
                                    out.print(
                                        if (data.isNotEmpty())
                                            data[0].toJson()
                                        else ""
                                    )
                                } else resp.writer.println("{Err: \"${(result as Results.Error).code.name}!\"}")
                            } catch (e: Exception) {
                                out.print("Err: \"${e.message}\"")
                            }
                        }

                        "/all_job_cards" -> {
                            try {
                                result = JobCartItemRepo().loadAllInCompleteJobCardItems()
                                if (result is Results.Success<*>) {
                                    val data = result.data as List<JobCardItem>
                                    resp.contentType = "application/json"
                                    out.print(
                                        if (data.isNotEmpty())
                                            data.toJson()
                                        else ""
                                    )
                                } else resp.writer.print("{Err: ${(result as Results.Error).code.name}!}")
                            } catch (e: Exception) {
                                out.print("Err: ${e.message}")
                            }
                        }
                        else -> {
                            out.print("Err: \"No Resource\"")
                        }
                    }
                } else out.print("{Err: \"Invalid Auth.\"}")
            }
        } catch (e: Exception) {
            out.print("{Err: \"Server Error!}\"")
        }
    }

    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doPost(req, resp)
        val out = resp.writer

        try {
            val passcode = req.getParameter("passcode")
            val jobCardItems = req.getParameter("job_card_items")

            val uri = req.requestURI.substring(req.contextPath.length)
            resp.contentType = "application/json"

            runBlocking {
                val results = DriverRepo().findDriverByPassCode(passcode)
                if (results is Results.Success<*> && !(results.data as? ArrayList<Driver>).isNullOrEmpty()) {
                    when (uri) {
                        "/job_card_item_update" -> {
                            val jobCardItemList = jobCardItems.convert<List<JobCardItem>>()

                            val loadJobCardItemResults = JobCartItemRepo().batchUpdate(jobCardItemList)
                            if (loadJobCardItemResults is Results.Success<*>)
                                out.print("{Status:\"Success\"}")
                            else
                                out.print("{Status: \"Server Error\"}")
                        }
                    }
                } else out.print("{Status: \"Invalid Auth.\"}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            out.print("{Status: \"Server Error!\"}")
        }
    }
}