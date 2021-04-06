package com.pet001kambala.namops_driver_portal.servlets

import com.pet001kambala.namops_driver_portal.model.Driver
import com.pet001kambala.namops_driver_portal.model.JobCardItem
import com.pet001kambala.namops_driver_portal.model.Trip
import com.pet001kambala.namops_driver_portal.model.TripStatus
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

@WebServlet(
    name = "Trips",
    value = ["/trip", "/trip_update", "/recent_incomplete_trip", "/trip_delete", "/trip_on_jobCard"]
)
class TripServlet : HttpServlet() {

    private var repo: TripRepo = TripRepo()
    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {

        resp.contentType = "application/json"
        val out = resp.writer

        try {
            val passcode = req.getParameter("passcode")
            val surname = req.getParameter("surname")
            val uri = req.requestURI.substring(req.contextPath.length)
            runBlocking {
                var result = DriverRepo().findDriver(surname = surname, passcode = passcode)
                if (result is Results.Success<*> && !(result.data as? ArrayList<Driver>).isNullOrEmpty()) {
                    when (uri) {
                        "/recent_incomplete_trip" -> {
                            val driver = (result.data as? ArrayList<Driver>)?.get(0)
                            result = repo.loadDriverRecentTrip(driverId = driver?.id!!)
                            if (result is Results.Success<*>) {
                                val data = result.data as ArrayList<Trip>
                                if (!data.isNullOrEmpty())
                                    out.print("{Status:\"Success\",data:${data[0].toJson()}}")
                                else out.print("{Status: \"No Data\"}")
                            } else out.print("{Status: \"Server Error\"}")
                        }
                        "/trip_on_jobCard" -> {
                            val jobCardNo = req.getParameter("jobCardNo")
                            if (jobCardNo.isNullOrEmpty())
                                out.print("{Status: \"Invalid request\"}")
                            else {
                                result = repo.loadTripByJobCard(jobCardNo)
                            }
                        }
                        else -> out.print("{Status: \"Invalid path\"}")
                    }

                } else {
                    out.print("{Status: \"Invalid Auth\"}")
                }
            }
        } catch (e: Exception) {
            out.print("{Err: \"Server Error\"}")
        }
    }

    public override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val out = resp.writer
        resp.contentType = "application/json"
        try {
            val passcode = req.getParameter("passcode")
            val surname = req.getParameter("surname")
            val jsonTrip = req.getParameter("trip")

            val uri = req.requestURI.substring(req.contextPath.length)

            runBlocking {
                var result = DriverRepo().findDriver(surname = surname, passcode = passcode)
                if (result is Results.Success<*> && !(result.data as? ArrayList<Driver>).isNullOrEmpty()) {
                    when (uri) {
                        "/trip" -> {
                            val trip = jsonTrip.convert<Trip>()
                            result = repo.addNewModel(trip)
                            if (result is Results.Success<*>)
                                out.print("{Status:\"Success\", data: ${result.data.toJson()}}")
                            else out.print("{Status: \"Server Error\"}")
                        }
                        "/trip_update" -> {

                            val trip = jsonTrip.convert<Trip>()

                            //if there are unknown container picked up, create a jobCardItem for each and add to database
                            val jobCartItemRepo = JobCartItemRepo()

                            listOfNotNull(
                                trip.container1 to trip.container1JobCardId,
                                trip.container2 to trip.container2JobCardId,
                                trip.container3 to trip.container3JobCardId
                            ).filter { it.second?.toLowerCase() == "ja000" }
                                .map {
                                    JobCardItem().apply {
                                        jobCardNo = it.second
                                        containerNo = it.first
                                        driver = trip.driver
                                        wasPickedUp = true
                                        wasDroppedOff = true
                                        jobCardCompleted = true
                                        pickUpLocationName = trip.pickUpLocationName
                                    }
                                }.forEach {
                                    if (trip.tripStatus == TripStatus.COMPLETED)
                                        jobCartItemRepo.addNewModel(it)
                                }

                            result = if (trip.id == null) repo.addNewModel(trip) else repo.updateModel(trip)

                            if (result is Results.Success<*>) {
                                val jobCardNo = listOfNotNull(
                                    trip.container1JobCardId,
                                    trip.container2JobCardId,
                                    trip.container3JobCardId
                                ).firstOrNull { it.toLowerCase() != "ja000" }
                                result = JobCartItemRepo().batchUpdate(trip = trip, jobCardNo = jobCardNo)
                                if (result is Results.Success<*>)
                                    out.print("{Status:\"Success\",data: ${trip.toJson()}}")
                                else out.print("{Status: \"Server Error\"}")
                            } else out.print("{Status: \"Server Error\"}")
                        }

                        "/trip_delete" -> {
                            val trip = jsonTrip.convert<Trip>()
                            result = repo.deleteTrip(tripId = trip.id ?: -1)
                            if (result is Results.Success<*>)
                                out.print("{Status:\"Success\"}")
                            else out.print("{Status: \"Server Error\"}")
                        }
                        else -> out.print("{Status: \"Invalid path\"}")
                    }
                } else out.print("{Status: \"Invalid Auth\"}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            out.print("{Status: \"Server Error\"}")
        }
    }
}