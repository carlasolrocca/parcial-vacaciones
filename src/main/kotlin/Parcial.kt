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
class Persona(val nombre : String, val dni : Int, val presupuestoMaximo : Double, val email : String){
    var preferenciaVacaciones : PreferenciaVacaciones = PersonaTranquila  //Por defecto, es PersonaTranquila

    //Metodo que responde si acepta UN lugar para vacacionar
    fun aceptaLugar(lugar: Lugar): Boolean = preferenciaVacaciones.aceptaLugarTuristico(lugar)

    //Metodo que responde si acepta TODOS los lugares de un tour
    fun aceptaLugaresTour(tour : Tour) : Boolean = tour.lugaresTuristicos.all { aceptaLugar(it) }
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

// *** PUNTO 3 ***
class Tour(
    val fechaSalida : LocalDate,
    var cantidadPersonasRequerida : Int,
    val lugaresTuristicos : MutableList<Lugar>,
    val montoPagar : Double)
{
    var participantesDelTour : MutableList<Persona> = mutableListOf() //hubiera hecho un map de persona+preferencia pero no se
    lateinit var administradorDelTour : Administrador               //C/Tour tiene que tener su propio Admin, eso entiendo yo

    fun agregarParticipante(persona : Persona) {
        if (limiteParticipantesAlcanzado()) {
            administradorDelTour.confirmarTour(persona, this)            //PUNTO 4
        }else{
            participantesDelTour.add(persona)
        }
    }

    fun limiteParticipantesAlcanzado() : Boolean = participantesDelTour.size == cantidadPersonasRequerida
    fun estaConfirmado() : Boolean = limiteParticipantesAlcanzado()
    fun fechaLimitePago() : LocalDate = fechaSalida.minusDays(30)
    fun superaMontoAFIP() : Boolean = montoPagar > 1000000
}

//La Casa de Turismo está a cargo del Armado de Tours
class CasaTurismo(){
    var toursDisponibles : MutableList<Tour> = mutableListOf()           //La Casa de Turismo conoce a varios Tours
    var listaEsperaPersonas : MutableList<Persona> = mutableListOf()    //Lista de espera de personas que no pudieron acceder a un tour

    var listaPersonas : MutableList<Persona> = mutableListOf()          //Lista de personas que quieren comprar un tour

    //Filtra lista de Tours cuyos lugares (TODOS) son aceptados por la persona
    fun toursLocacionesAceptadas(persona : Persona) : List<Tour> = toursDisponibles.filter{ tour -> persona.aceptaLugaresTour(tour) }

    //Filtra lista de Tours con el lugar + todos los que valgan menos que el presupuesto de la persona y agarra el MAS barato
    fun tourBaratoDisponible(persona : Persona) : Tour? = toursLocacionesAceptadas(persona)
        .filter { it.montoPagar <= persona.presupuestoMaximo }  //Lista con los que valen lo mismo o menos que el presupuesto
        .minByOrNull { it.montoPagar }                          //Agarra al + barato

    //La consigna dice que "PARA CADA PERSONA" ergo quiere recorrer una lista. CasaTurismo tiene una lista de personas
    //que quieren acceder a un tour, entonces recorro esa lista y hago la venta.
    fun ventaToursAPersonas(listaPersonas: MutableList<Persona>){
        listaPersonas.forEach { persona -> validacionVentaTour(persona) }
    }

    //Abstraigo logica para validar si persona accede a un tour o va a lista espera
    private fun validacionVentaTour(persona : Persona) {
        val tourDisponible = tourBaratoDisponible(persona)
        if(tourDisponible != null){
            tourDisponible.agregarParticipante(persona)
        }else{
            agregarPersonaListaEspera(persona)      //Si no hay tour disponible, la agrego a la lista de espera
        }
    }

    private fun agregarPersonaListaEspera(persona: Persona) = listaEsperaPersonas.add(persona)

}
// *** FIN PUNTO 3 ***

/*
* Aca está el diferencial del parcial de Vacaciones que decia Dodino:
* hay una confirmacion manual (la hace un administrador)
* y una acción asincronica (la hacen los observers) después de que se confirmo el Tour.
*/

// *** PUNTO 4 ***
//Como el Admin es quien confirma el Tour debe tener la lista de Observers
class Administrador() {
    var listaObservers: MutableList<TourObserver> = mutableListOf()

    fun agregarObserver(observer: TourObserver) {
        listaObservers.add(observer)
    }

    fun eliminarObserver(observer: TourObserver) {
        listaObservers.remove(observer)
    }

    //Hay algo con las dependencias que no me gusta, pero no se como resolverlo
    fun confirmarTour(persona : Persona, tour : Tour) {
        //Cuando confirma el Tour, hace algo más el Admin ademas de llamar a los observers?
        listaObservers.forEach { observer -> observer.accionesTourConfirmado(persona, tour) }
    }

    //Un Admin debe poder agregar/eliminar gente de un Tour
   //(sin importar limite de participantes o preferencias/presupuesto de la persona)
    fun agregarPersonaAlTour(persona : Persona, tour : Tour){
        tour.participantesDelTour.add(persona)      //O conoce la estructura interna del Tour y bypassea la validacion
    }                                               //O yo deberia agregar algo a la validacion que chequee por Admin (se puede?)
    fun eliminarPersonaDelTour(persona : Persona, tour : Tour){
        tour.participantesDelTour.remove(persona)
    }
}

//Observers
interface TourObserver {
    fun accionesTourConfirmado(persona : Persona, tour : Tour){} //Sin definir aun
}

class NotificacionMail(val mailSender : MailSender) : TourObserver {
    override fun accionesTourConfirmado(persona : Persona, tour : Tour) {
        mailSender.sendMail(
            Mail(from = "agenciaviajesDodain@gmail.com",
                to = persona.email,
                subject = "Confirmación de Tour!!!",
                content = "Le informamos que su tour ha sido confirmado!" +
                        "El tour comenzara el dia ${tour.fechaSalida} y tiene un costo de ${tour.montoPagar}." +
                        "Tenes la posibilidad de pagar hasta el día ${tour.fechaLimitePago()}"+
                        "Los destinos a recorrer son: ${tour.lugaresTuristicos}.")
        )
    }
}
class NotificacionAFIP(val informeAfip : InformeAFIP) : TourObserver {
    override fun accionesTourConfirmado(persona : Persona, tour : Tour) {
        if(tour.superaMontoAFIP()){             //Solo si supera el monto, manda el informe
            informeAfip.sendInforme(
                Informe(
                    from = "agenciaviajesDodain@gmail.com",
                    to = "informes@arca.com.ar",
                    codigos = tour.lugaresTuristicos.map { it.codigoLugar },
                    documentos = tour.participantesDelTour.map { it.dni })
            )
        }
    }
}
class CambioPreferencia() : TourObserver {
    override fun accionesTourConfirmado(persona : Persona, tour : Tour) {

    }
}

interface MailSender {
    fun sendMail(mail: Mail)
}

interface InformeAFIP {
    fun sendInforme(informe: Informe)
}

//El content puede tener la info especifica del Tour
data class Mail(val from : String, val to : String, val subject : String, val content : String)

data class Informe(val from : String, val to : String, val codigos : List<Int>, val documentos : List<Int>)
// *** FIN PUNTO 4 ***