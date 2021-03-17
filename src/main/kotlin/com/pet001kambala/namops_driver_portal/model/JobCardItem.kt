package com.pet001kambala.namops_driver_portal.model

import java.time.LocalDateTime
import javax.persistence.*

/**
 * [JobCardItem] can have multiple containers, it might be pre-assigned in which case [driverId] will be set
 */

@Entity(name="JobCardItem")
class JobCardItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "jobCardNo", nullable = false)
    val jobCardNo: Int? = null

    @Column(name = "containerNo", nullable = false)
    val containerNo: String? = null

    @Column(name = "containerSize", nullable = false)
    val containerSize: String? = null

    @Column(name = "isFull", nullable = false)
    val isFull: Boolean = false

    @Column(name = "customerRef", nullable = false)
    val customerRef: String? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driverId", nullable = true)
    var driver: Driver? = null

    @Column(name = "isCompleted", nullable = false)
    val isCompleted: Boolean = false // whether or not the job card item is completed so as not to include it again.

    @Column(name = "designatedPickUpDate", nullable = false)
    var designatePickUpDate: LocalDateTime? = null

    @Column(name = "useBison", nullable = false)
    var useBison: Boolean = false /*whether or not merchandise will be weigh by bison*/

    @Column(name = "useWeighBridge", nullable = false)
    var useWeighBridge: Boolean = false /*whether or not merch will be weigh at the weighbridge*/

    @Column(name = "scanContainer", nullable = false)
    var scanContainer: Boolean = false /*whether or not the container must be thermally scanned*/

}