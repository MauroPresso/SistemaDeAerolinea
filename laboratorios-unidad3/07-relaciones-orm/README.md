# Unidad 3 - Etapa 7: Relaciones ORM

Esta etapa agrega las relaciones del dominio real sin modificar aun el `src/main`
del SistemaDeAerolinea.

## Relaciones estudiadas

`Vuelo <-> Pasajero` es una relacion muchos-a-muchos bidireccional: un vuelo
tiene varios pasajeros y un pasajero puede reservar varios vuelos.

El lado propietario define una join table:

```java
@Persistent(table = "VUELO_PASAJERO")
@Join(column = "VUELO_NUMERO")
@Element(column = "PASAJERO_DNI")
private Set<Pasajero> pasajeros;
```

El lado inverso usa:

```java
@Persistent(mappedBy = "pasajeros")
private Set<Vuelo> vuelosReservados;
```

`Vuelo -> Tripulante` se mantiene unidireccional, como en el proyecto real, y
usa la join table `VUELO_TRIPULANTE`.

## Por que Set en este laboratorio

El proyecto real utiliza `ArrayList`. Para una relacion M-N verdadera, `Set`
permite estudiar primero la relacion sin mezclar el problema adicional del
indice/orden de una `List`.

## Paso 1

```powershell
mvn clean process-classes
```

## Paso 2

```powershell
mvn datanucleus:schema-create
```

## Paso 3

```powershell
mvn exec:java "-Dexec.mainClass=aerolinea.demo.VerSchemaRelaciones"
```

Deben aparecer, entre otras, las tablas `VUELO_PASAJERO` y
`VUELO_TRIPULANTE`.

## Paso 4

```powershell
mvn exec:java "-Dexec.mainClass=aerolinea.demo.DemoRelacionesOrm"
```

La prueba muestra las relaciones desde Vuelo y luego desde Pasajero.
