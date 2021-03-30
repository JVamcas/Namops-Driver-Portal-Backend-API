package com.pet001kambala.namops_driver_portal.repo

import com.pet001kambala.namops_driver_portal.model.Driver
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session

class DriverRepo : AbstractRepo<Driver>() {

    suspend fun findDriver(surname: String, passcode: String): Results {
        var session: Session? = null
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.openSession()
                val strqry = "SELECT * FROM driver where passcode=:passcode and surname=:surname"
                val data = session!!.createNativeQuery(strqry, Driver::class.java)
                    .setParameter("passcode", passcode)
                    .setParameter("surname",surname)
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