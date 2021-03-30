package com.pet001kambala.namops_driver_portal.repo

import com.pet001kambala.namops_driver_portal.model.Trip
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session

class TripRepo: AbstractRepo<Trip>() {


    suspend fun findTrip(tripId: Int): Results{

        var session: Session? = null
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.openSession()
                val strqry = "SELECT * FROM tbljoblogistics where id=:tripId"
                val data = session!!.createNativeQuery(strqry, Trip::class.java)
                    .setParameter("tripId", tripId)
                    .resultList.filterNotNull()
                Results.Success(data = data, code = Results.Success.CODE.LOAD_SUCCESS)
            }
        } catch (e: Exception) {
            Results.Error(e)
        } finally {
            session?.close()
        }
    }

    suspend fun loadDriverRecentTrip(driverId: Int): Results{
        var session: Session? = null
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.openSession()
                val strqry = "SELECT * FROM tbljoblogistics where driverId=:driverId and dropOffDate is null"
                val data = session!!.createNativeQuery(strqry, Trip::class.java)
                    .setParameter("driverId", driverId)
                    .resultList.filterNotNull()
                Results.Success(data = data, code = Results.Success.CODE.LOAD_SUCCESS)
            }
        } catch (e: Exception) {
            Results.Error(e)
        } finally {
            session?.close()
        }
    }
}