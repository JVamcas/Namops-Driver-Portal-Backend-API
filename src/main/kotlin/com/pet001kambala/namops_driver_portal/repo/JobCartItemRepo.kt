package com.pet001kambala.namops_driver_portal.repo

import com.pet001kambala.namops_driver_portal.model.Driver
import com.pet001kambala.namops_driver_portal.model.JobCardItem
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session

class JobCartItemRepo : AbstractRepo<JobCardItem>() {

    suspend fun findJobCardItemByContainerNumber(containerNo: String): Results {
        var session: Session? = null
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.openSession()
                val strqry = "SELECT * FROM jobcarditem where containerno=:containerNo"
                val data = session!!.createNativeQuery(strqry, JobCardItem::class.java)
                    .setParameter("containerNo", containerNo)
                    .resultList.filterNotNull()
                Results.Success(data = data, code = Results.Success.CODE.LOAD_SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Results.Error(e)
        } finally {
            session?.close()
        }
    }

    suspend fun loadAllInCompleteJobCardItems(): Results {
        var session: Session? = null
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.openSession()
                val strqry = "SELECT * FROM jobcarditem where iscompleted=false"
                val data = session!!.createNativeQuery(strqry, JobCardItem::class.java)
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