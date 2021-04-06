package com.pet001kambala.namops_driver_portal.repo

import com.pet001kambala.namops_driver_portal.model.JobCardItem
import com.pet001kambala.namops_driver_portal.model.Trip
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

    suspend fun findJobCardItemByJobCardNo(jobCardNo: String): Results {
        var session: Session? = null
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.openSession()
                val strqry = "SELECT * FROM jobcarditem where jobCardNo=:jobCardNo"
                val data = session!!.createNativeQuery(strqry, JobCardItem::class.java)
                    .setParameter("jobCardNo", jobCardNo)
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
                val strqry = "SELECT * FROM jobcarditem where jobCardCompleted=false "
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

    suspend fun batchUpdate(
        trip: Trip,
        jobCardNo: String? = null
    ): Results {
        var session: Session? = null
        var trans: Transaction? = null

        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.openSession()
                trans = session!!.beginTransaction()

                val strqry = //whether or not the container was picked up
                    "UPDATE jobcarditem j set j.wasPickepUp=true WHERE j.containerNo in(:list)"

                val strDroppedOff = //whether or not the container was dropped off up
                    "UPDATE jobcarditem j set j.wasDroppedOff=true WHERE j.containerNo in(:list)"

                val strJobCardCompleted =//set that the jobcard is complete if all the containers on it were dropped off
                    "update jobcarditem as card,(select t.wasDroppedOff from jobcarditem t where t.jobCardNo=:jobCardNo) as temp " +
                            "set card.jobCardCompleted=(IF(false in(temp.wasDroppedOff),false,true)) where card.jobCardNo=:jobCardNo"

                //todo there is a caveat here, need to make sure drop of date for all container is non-null

                val pickedUp = listOfNotNull(trip.container1, trip.container2, trip.container3)

                session!!.createNativeQuery(strqry, JobCardItem::class.java)
                    .setParameter("list", pickedUp).executeUpdate()

                trip.dropOffDate?.let {
                    session!!.createNativeQuery(strDroppedOff, JobCardItem::class.java)
                        .setParameter("list", pickedUp).executeUpdate()
                }

                jobCardNo?.let {
                    session!!.createNativeQuery(strJobCardCompleted, JobCardItem::class.java)
                        .setParameter("jobCardNo", jobCardNo).executeUpdate()
                }

                trans!!.commit()

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