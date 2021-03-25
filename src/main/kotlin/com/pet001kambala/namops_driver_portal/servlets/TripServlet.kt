package com.pet001kambala.namops_driver_portal.servlets

import com.pet001kambala.namops_driver_portal.model.Driver
import com.pet001kambala.namops_driver_portal.model.JobCardItem
import com.pet001kambala.namops_driver_portal.model.Trip
import com.pet001kambala.namops_driver_portal.repo.DriverRepo
import com.pet001kambala.namops_driver_portal.repo.JobCartItemRepo
import com.pet001kambala.namops_driver_portal.repo.TripRepo
import com.pet001kambala.namops_driver_portal.utils.ParseUtil.Companion.convert
import com.pet001kambala.namops_driver_portal.utils.ParseUtil.Companion.toJson
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.runBlocking
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "Trips", value = ["/trip", "/trip_update"])
class TripServlet : HttpServlet() {

    private var repo: TripRepo = TripRepo()
    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        val out = resp.writer
        try {
            val tripId = req.getParameter("tripId")
            runBlocking {
                val results = repo.findTrip(tripId.toInt())
                if (results is Results.Success<*>) {
                    val data = results.data as List<Trip>
                    resp.contentType = "application/json"
                    resp.writer.print(
                        if (data.isNotEmpty())
                            "{Status:\"Success\",data: ${data[0].toJson()}}"
                        else "{Status: \"Error\"}"
                    )
                } else resp.writer.print("{Err: \"Server error!\"}")
            }

        } catch (e: Exception) {
            out.print("{Err: \"Server Error!\"}")
        }
    }

    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val out = resp.writer
        resp.contentType = "application/json"
        try {
            val passcode = req.getParameter("passcode")
            val jsonTrip = req.getParameter("trip")
            val jsonJobCardItems = req.getParameter("job_card_items")
            val wasPickedUp = req.getParameter("wasPickedUp")?.toBoolean() ?: false
            val jobCardComplete = req.getParameter("jobCardComplete")?.toBoolean() ?: false


            val uri = req.requestURI.substring(req.contextPath.length)

            runBlocking {
                var result = DriverRepo().findDriverByPassCode(passcode)
                if (result is Results.Success<*> && !(result.data as? ArrayList<Driver>).isNullOrEmpty()) {
                    when (uri) {
                        "/trip" -> {
                            val trip = jsonTrip.convert<Trip>()
                            result = repo.addNewModel(trip)
                            if (result is Results.Success<*>)
                                out.print("{Status:\"Success\", data: ${(result as Results.Success<*>).data.toJson()}}")
                            else out.print("{Status: \"Server Error\"}")
                        }
                        "/trip_update" -> {

                            //update jobCardItems was picked up or jobCard complete

                            jsonJobCardItems?.let {
                                val jobCardItemList = jsonJobCardItems.convert<List<JobCardItem>>()

                                val loadJobCardItemResults =
                                    JobCartItemRepo().batchUpdate(
                                        wasPickedUp = wasPickedUp,
                                        jobCardComplete = jobCardComplete,
                                        jobCardItems = jobCardItemList
                                    )
                                if (loadJobCardItemResults is Results.Error) {
                                    out.print("{Status: \"Server Error\"}")
                                    return@runBlocking //quit whole op if error occurred
                                }
                            }

                            val trip = jsonTrip.convert<Trip>()
                            result = repo.updateModel(trip)
                            if (result is Results.Success<*>)
                                out.print("{Status:\"Success\",data: ${(result as Results.Success<*>).data.toJson()}}")
                            else out.print("{Status: \"Server Error\"}")
                        }
                        else -> {
                            out.print("{Status: \"Invalid path\"}")
                        }
                    }
                } else out.print("{Status: \"Invalid Auth.\"}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            out.print("{Status: \"Server Error!\"}")
        }
    }

    public override fun doDelete(req: HttpServletRequest, resp: HttpServletResponse) {
        val id = req.getParameter("id")
        runBlocking {

        }
    }
}