package com.pet001kambala.namops_driver_portal.repo


import com.pet001kambala.namops_driver_portal.utils.Results
import com.pet001kambala.namops_driver_portal.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hibernate.Session
import java.util.*

abstract class AbstractRepo<T> {

    val sessionFactory by lazy { SessionManager.newInstance }

    open suspend fun addNewModel(model: T): Results {
        var session: Session? = null;
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.withOptions()
                    .jdbcTimeZone(TimeZone.getTimeZone("GMT+2"))
                    .openSession()
                val transaction = session!!.beginTransaction()
                session!!.persist(model)
                transaction.commit()
                Results.Success(data = model,code = Results.Success.CODE.WRITE_SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            session?.transaction?.rollback()
            Results.Error(e)
        }
        finally {
            session?.close()
        }
    }

    open suspend fun updateModel(model: T): Results {
        var session: Session? = null
        return try {
            withContext(Dispatchers.Default) {
                session = sessionFactory!!.withOptions()
                    .jdbcTimeZone(TimeZone.getTimeZone("GMT+2"))
                    .openSession()

                val transaction = session!!.beginTransaction()
                session!!.update(model)
                transaction.commit()
                Results.Success(data = model,code = Results.Success.CODE.WRITE_SUCCESS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            session?.transaction?.rollback()
            Results.Error(e)
        }
        finally {
            session?.close()
        }
    }
}