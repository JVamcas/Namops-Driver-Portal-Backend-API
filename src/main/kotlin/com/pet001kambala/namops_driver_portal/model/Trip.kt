package com.pet001kambala.namops_driver_portal.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity(name="tblJobLogistics")
class Trip{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driverId", nullable = false)
    var driver: Driver? = null

    /** truck and trailer info start **/
    @Column(name = "truckReg", nullable = false)
    var truckReg: String? = null

    @Column(name = "firstTrailerReg", nullable = false)
    var firstTrailerReg: String? = null

    @Column(name = "secondTrailerReg", nullable = true)
    var secondTrailerReg: String? = null

    /** truck and trailer info end **/
    @Column(name = "designatePickUpDate", nullable = true)
    var designatePickUpDate: LocalDateTime? = null
    /**designated container pick up date*/

    /** trip start info start **/
    @Column(name = "startODM", nullable = false)
    var startODM: String? = null

    @Column(name = "startLocationName", nullable = true)
    var startLocationName: String? = null

    @Column(name = "startLocationGPS", nullable = true)
    var startLocationGPS: String? = null

    /** trip start info end **/

    /** container weighing info start **/
    @Column(name = "useBison", nullable = false)
    var useBison: Boolean = false /*whether or not merchandise will be weigh by bison*/

    @Column(name = "useWeighBridge", nullable = false)
    var useWeighBridge: Boolean = false /*whether or not merch will be weigh at the weighbridge*/

    @Column(name = "dateWeightBridgeEmpty", nullable = true)
    var dateWeightBridgeEmpty: LocalDateTime? = null

    @Column(name = "emptyTruckWeight", nullable = true)
    var emptyTruckWeight: Long? = 0L

    @Column(name = "dateWeightBridgeFull", nullable = true)
    var dateWeightBridgeFull: LocalDateTime? = null

    @Column(name = "fullTruckWeight", nullable = true)
    var fullTruckWeight: Long = 0L

    /** container weighing info end **/

    /** container scanning info start **/
    @Column(name = "scanContainer", nullable = false)
    var scanContainer: Boolean = false /*whether or not the container must be thermally scanned*/

    @Column(name = "containerScanDate", nullable = true)
    var containerScanDate: LocalDateTime? = null
    /** container scanning info end **/

    /** container pick up info start **/
    @Column(name = "actualPickUpDate", nullable = true)
    var actualPickUpDate: LocalDateTime? = null

    @Column(name = "pickUpLocationGPS", nullable = true)
    var pickUpLocationGPS: String? = null

    @Column(name = "pickUpLocationName", nullable = true)
    var pickUpLocationName: String? = null

    @Column(name = "pickUpODM", nullable = true)
    var pickUpODM: String? = null

    @Column(name = "container1", nullable = true)
    var container1: String? = null

    @Column(name = "container2", nullable = true)
    var container2: String? = null

    @Column(name = "container3", nullable = true)
    var container3: String? = null

    @Column(name = "unknownContainer", nullable = true)
    var unknownContainer: String? = null

    @Column(name = "weighBillNo", nullable = true)
    var weighBillNo: String? = null

    @Column(name = "container1JobCardId", nullable = true)
    var container1JobCardId: String? = null

    @Column(name = "container2JobCardId", nullable = true)
    var container2JobCardId: String? = null

    @Column(name = "container3JobCardId", nullable = true)
    var container3JobCardId: String? = null

    @Column(name = "unknownContainerJobCardId", nullable = true)
    var unknownContainerJobCardId: String? = null

    /** container pick up info end **/

    /** drop off location info start **/
    @Column(name = "dropOffODM", nullable = true)
    var dropOffODM: String? = null

    @Column(name = "dropOffLocationGPS", nullable = true)
    var dropOffLocationGPS: String? = null

    @Column(name = "dropOffLocationName", nullable = true)
    var dropOffLocationName: String? = null

    @Column(name = "memNotes", nullable = true)
    var memNotes: String? = null

    /** drop off location info end  **/
    @Column(name = "tripStatus", nullable = false)
    val tripStatus: TripStatus = TripStatus.START
}

enum class TripStatus {
    START, WEIGH_EMPTY, PICK_UP, WEIGH_FULL, BISON, DROP_OFF
}