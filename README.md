# Sistema de Gestión de Tours

Este sistema permite modelar distintos tipos de **lugares turísticos**, **personas con preferencias de vacaciones** y la **organización de tours**. A continuación se detallan los requerimientos y funcionalidades del sistema.

---

## 🏞 Punto 1: Lugares

Existen diferentes tipos de lugares:

### 🏙 Ciudades
- Cantidad de habitantes.
- Atracciones turísticas (ej: "Obelisco", "Cabildo", etc.).
- Cantidad de decibeles promedio.

### 🏡 Pueblos
- Extensión en km².
- Fecha de fundación.
- Provincia en la que se ubica.

### 🏖 Balnearios
- Metros de playa promedio.
- Si el mar es peligroso.
- Si tiene peatonal.

### 🎉 Lugar Divertido
Un **lugar es divertido** si:
- Tiene **una cantidad par de letras** en su nombre, **y además**:
    - **Ciudades**: más de 3 atracciones **y** más de 100.000 habitantes.
    - **Pueblos**: fundado antes de 1800 **o** ubicado en el Litoral ("Entre Ríos", "Corrientes" o "Misiones").
    - **Balnearios**: más de 300 metros de playa **y** el mar es peligroso.

---

## 🧑‍🌾 Punto 2: Personas

Las personas tienen **preferencias de vacaciones**, las cuales determinan si un lugar es adecuado para ellas:

### Tipos de preferencias:
1. **Tranquilidad**:
    - **Ciudad**: menos de 20 decibeles.
    - **Pueblo**: debe estar en La Pampa.
    - **Balneario**: no debe tener peatonal.

2. **Diversión**:
    - El lugar debe cumplir con los criterios de "lugar divertido".

3. **Alternancia entre tranquilidad y diversión**:
    - En cada vacación cambia su criterio.

4. **Combinación de preferencias**:
    - Si **alguna de las preferencias** se cumple, acepta el lugar.

> ⚙️ El diseño debe permitir cambiar la preferencia fácilmente y agregar nuevas en el futuro.

---

## 🚌 Punto 3: Armado de Tours

Los tours tienen la siguiente información:
- Fecha de salida.
- Cantidad de personas requerida.
- Lista de lugares a recorrer.
- Monto a pagar por persona.

### Elección de tours:
- Cada persona elige el **tour más barato** que:
    - Esté dentro de su **presupuesto máximo**.
    - Todos los lugares sean **adecuados según su preferencia**.

### Reglas:
- Si no hay un tour adecuado, la persona queda en una **lista de pendientes**.
- Al llegar al número requerido de personas, el tour se **confirma automáticamente** y no acepta más gente.
- Un **administrador** puede:
    - Agregar o eliminar personas del tour.
    - Esto puede hacerse incluso si el tour está completo o las condiciones no se cumplen.

---

## ✅ Punto 4: Confirmación de un Tour

Cuando el administrador confirma un tour:

1. Se envía un **mail a cada persona** con:
    - Fecha de salida.
    - Fecha límite de pago (30 días antes o la fecha actual, la que sea más próxima).
    - Lista de lugares a visitar.

2. Si el tour cuesta **más de 10M por persona**, se informa a la **AFIP**:
    - Códigos de lugares a visitar.
    - Lista de DNIs de los participantes.

3. Las personas que alternan entre tranquilidad y diversión deben **cambiar su estado actual** para la próxima vacación.

> ⚙️ Se busca que sea **fácil agregar o quitar acciones** que se ejecutan durante la confirmación del tour.

---

## 🧠 Diseño y Decisiones

### Principales ideas aplicadas:
- Uso del patrón **Strategy** para modelar preferencias de las personas.
- Posible uso del patrón **Composite** para combinar preferencias.
- Se utiliza el patrón **Command** o **Observer** para acciones durante la confirmación del tour.
- Diseño abierto a **extensiones sin modificar código existente** (Principio de Abierto/Cerrado - SOLID).
- Se permite la **mutabilidad controlada** en objetos como el tour mediante el administrador.

### Alternativas consideradas:
- Implementar preferencias como enums → fue descartado por falta de extensibilidad.
- Uso de herencia rígida en lugar de composición → se optó por composición para mayor flexibilidad.
- Un solo método para confirmar → se prefirió dividir en pasos para facilitar futuras adiciones.

---

## 📊 Diagrama de Clases (resumen conceptual)

*(Agregar aquí una imagen o representación en PlantUML, UMLet, etc.)*

Clases principales:
- `Lugar` (abstracta) con subclases: `Ciudad`, `Pueblo`, `Balneario`.
- `Persona` con referencia a su `Preferencia`.
- `Preferencia` (interfaz) con implementaciones: `Tranquila`, `Divertida`, `Alternada`, `Combinada`.
- `Tour`: contiene fecha, lugares, personas, precio, etc.
- `Administrador`: realiza acciones sobre los tours.
- `AccionDeConfirmacion`: interfaz para comportamientos al confirmar (enviar mail, avisar a AFIP, etc).

---

> 📌 Este sistema busca ser fácilmente extensible para nuevos tipos de lugares, preferencias y comportamientos ante eventos como la confirmación del tour.
