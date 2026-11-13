# EcoRuta · Recepción y Valoración de Materiales Reciclables
Parcial 3 - Programación Orientada a Objetos (Java 17+)

Sistema en memoria para el centro de aprovechamiento EcoRuta que gestiona la recepción, validación y valoración económica de materiales reciclables organizados en entregas y lotes, con cálculo polimórfico de tarifas, comprobantes inmutables y etiquetas de control interno.

## Estructura del Proyecto
- `ecoruta.modelo`: Records, enums, interfaz Etiquetable, clase abstracta Lote y subclases polimórficas.
- `ecoruta.servicio`: Servicio en memoria con reglas de negocio y cálculo acumulado por aportante.
- `ecoruta.pruebas`: Suite de las 5 pruebas funcionales exigidas en el examen.
- `ecoruta.app`: Menú interactivo en consola.

## Requisitos de Ejecución
- Java JDK 17 o superior.

## Ejecución Rápida
- Menú interactivo: ejecutar `compilar_y_ejecutar.bat` o `compilar_y_ejecutar.ps1`.
- Pruebas funcionales: ejecutar `ejecutar_pruebas.bat`.
