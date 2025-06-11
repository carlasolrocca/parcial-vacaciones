package ar.edu.unsam.algo2

import java.time.LocalDate

// *** PUNTO 1 ***
abstract class Lugar(){
    val nombre : String = ""
    var habitantes : Int = 10000
    var codigoLugar : Int = 1234

    fun esDivertido() : Boolean{
        return cantidadParLetras() && condicionDivertido()
    }

    fun cantidadParLetras() : Boolean = (nombre.length % 2) == 0

    abstract fun condicionDivertido() : Boolean
}

//Las atraccionesTuristicas podria ser var porque podria tener un metodo para agregar/sacar
class Ciudad(var atraccionesTuristicas : MutableList<String>, val decibeles : Int) : Lugar(){
    override fun condicionDivertido(): Boolean = cantidadAtracciones() && cantidadHabitantes()

    fun cantidadAtracciones() : Boolean = atraccionesTuristicas.size > 3

    fun cantidadHabitantes() : Boolean = habitantes > 100000
}

class Pueblo(val extensionKm : Double, val fechaFundacion : LocalDate, val provincia : String) : Lugar() {
    var provinciasDelLitoral = mutableListOf("Entre Ríos", "Corrientes", "Misiones")

    override fun condicionDivertido(): Boolean = anioFundacion() || esDelLitoral()

    fun esDelLitoral() : Boolean = provincia in provinciasDelLitoral
    fun anioFundacion() : Boolean = fechaFundacion.isBefore(LocalDate.of(1800,1,1))
}

class Balneario(val metrosPlaya : Double, val marPeligroso : Boolean, val tienePeatonal : Boolean) : Lugar() {
    override fun condicionDivertido(): Boolean = metrosPlayaDivertida() && marPeligroso
    fun metrosPlayaDivertida() : Boolean = metrosPlaya > 300
}
// *** FIN PUNTO 1 ***

// *** PUNTO 2 ***
class Persona(val nombre : String, val dni : Int, val presupuestoMaximo : Double){
    var preferenciaVacaciones : PreferenciaVacaciones = PersonaTranquila()  //Por defecto, es PersonaTranquila

}

// Strategy para la preferencia por el lugar para vacacionar
interface PreferenciaVacaciones{
    fun aceptaLugarTuristico(lugar : Lugar): Boolean
}
class PersonaTranquila : PreferenciaVacaciones{
    override fun aceptaLugarTuristico(lugar: Lugar): Boolean {
        TODO("Not yet implemented")
    }

}
class PersonaDivertida : PreferenciaVacaciones{
    override fun aceptaLugarTuristico(lugar: Lugar): Boolean {
        TODO("Not yet implemented")
    }
}
class PersonaTranquilaODivertida : PreferenciaVacaciones{
    override fun aceptaLugarTuristico(lugar: Lugar): Boolean {
        TODO("Not yet implemented")
    }
}
class PersonaCombinado : PreferenciaVacaciones{
    override fun aceptaLugarTuristico(lugar: Lugar): Boolean {
        TODO("Not yet implemented")
    }
}
// *** FIN PUNTO 2 ***