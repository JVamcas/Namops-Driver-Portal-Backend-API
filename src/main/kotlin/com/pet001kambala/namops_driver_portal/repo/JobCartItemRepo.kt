package com.pet001kambala.namops_driver_portal.repo

import com.pet001kambala.namops_driver_portal.model.JobCardItem
import com.pet001kambala.namops_driver_portal.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session
import org.hibernate.Transaction

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
                val strqry = "SELECT * FROM jobcarditem where jobCardCompleted=false"
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



    suspend fun batchUpdate(transactions: List<JobCardItem>): Results {
        var trans: Transaction? = null
        var session: Session? = null
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory?.openSession()
                trans = session?.beginTransaction()
                transactions.forEach {
                    session?.update(it)
                }
                trans?.commit()
                Results.Success<JobCardItem>(code = Results.Success.CODE.UPDATE_SUCCESS)
            }
        } catch (e: Exception) {
            trans?.rollback()
            Results.Error(e)
        } finally {
            session?.close()
        }
    }
}