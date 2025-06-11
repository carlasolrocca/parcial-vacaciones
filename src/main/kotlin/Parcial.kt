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

    abstract fun esTranquilo() : Boolean        //PUNTO 2
}

//Las atraccionesTuristicas podria ser var porque podria tener un metodo para agregar/sacar
class Ciudad(var atraccionesTuristicas : MutableList<String>, val decibeles : Int) : Lugar(){
    override fun condicionDivertido(): Boolean = cantidadAtracciones() && cantidadHabitantes()

    fun cantidadAtracciones() : Boolean = atraccionesTuristicas.size > 3

    fun cantidadHabitantes() : Boolean = habitantes > 100000

    override fun esTranquilo(): Boolean = decibeles < 20  //PUNTO 2
}

class Pueblo(val extensionKm : Double, val fechaFundacion : LocalDate, val provincia : String) : Lugar() {
    var provinciasDelLitoral = mutableListOf("Entre Ríos", "Corrientes", "Misiones")

    override fun condicionDivertido(): Boolean = anioFundacion() || esDelLitoral()

    fun esDelLitoral() : Boolean = provincia in provinciasDelLitoral
    fun anioFundacion() : Boolean = fechaFundacion.isBefore(LocalDate.of(1800,1,1))

    override fun esTranquilo(): Boolean = provincia == "La Pampa" //PUNTO 2
}

class Balneario(val metrosPlaya : Double, val marPeligroso : Boolean, val tienePeatonal : Boolean) : Lugar() {
    override fun condicionDivertido(): Boolean = metrosPlayaDivertida() && marPeligroso
    fun metrosPlayaDivertida() : Boolean = metrosPlaya > 300
    override fun esTranquilo(): Boolean = !tienePeatonal  //PUNTO 2
}
// *** FIN PUNTO 1 ***

// *** PUNTO 2 ***
//El presupuesto podria ser var y ser atributo? No se que me conviene mas
class Persona(val nombre : String, val dni : Int, val presupuestoMaximo : Double){
    var preferenciaVacaciones : PreferenciaVacaciones = PersonaTranquila  //Por defecto, es PersonaTranquila

}

// Strategy para la preferencia por el lugar para vacacionar
interface PreferenciaVacaciones{
    fun aceptaLugarTuristico(lugar : Lugar): Boolean
}
object PersonaTranquila : PreferenciaVacaciones{
    override fun aceptaLugarTuristico(lugar: Lugar): Boolean = lugar.esTranquilo()
}
object PersonaDivertida : PreferenciaVacaciones{
    override fun aceptaLugarTuristico(lugar: Lugar): Boolean = lugar.esDivertido()
}

//No me gusta pero tiene un flag interno que alterna para cambiar entre tranquila y divertida
//El tema es que no reutilizo el object PersonaTranquila ni PersonaDivertida sino que copypasteo
//la misma logica que hacen esos strategys (si o si lo hago porque tengo que devolver Boolean)
class PersonaTranquilaODivertida(val persona : Persona) : PreferenciaVacaciones{
    var flagAlternante : Boolean = true             //true para tranquila, false para divertida

    override fun aceptaLugarTuristico(lugar: Lugar): Boolean {
        val preferenciaActual = if (flagAlternante) lugar.esTranquilo() else lugar.esDivertido()
        flagAlternante = !flagAlternante            //Cambia para la proxima vez
        return preferenciaActual
    }
}
class PersonaCombinado(var criterios : MutableList<PreferenciaVacaciones>) : PreferenciaVacaciones{
    override fun aceptaLugarTuristico(lugar: Lugar): Boolean = criterios.any { it.aceptaLugarTuristico(lugar) }
}
// *** FIN PUNTO 2 ***