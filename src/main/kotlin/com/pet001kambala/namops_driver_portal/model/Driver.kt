package com.pet001kambala.namops_driver_portal.model

import javax.persistence.*

@Entity(name="Driver")
class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "firstname", nullable = false)
    val firstName: String = ""

    @Column(name = "surname", nullable = false)
    val lastName: String = ""

    @Column(name = "passcode", nullable = false)
    val passCode: String = ""

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Driver)
            return false
        return other.id == id
    }

    override fun toString(): String {
        return "$firstName $lastName"
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        return result
    }
}