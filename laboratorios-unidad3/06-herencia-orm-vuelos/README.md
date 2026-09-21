# Unidad 3 - Etapa 6: Herencia ORM con la jerarquia real de vuelos

Esta etapa reproduce una version controlada de la jerarquia real del proyecto:

```text
Vuelo (abstracta)
|-- VueloNacional
|-- VueloInternacional
`-- VueloCharter
```

Todavia NO se modifican las clases de `SistemaDeAerolinea/src/main`.

Se excluyen temporalmente:

- pasajeros;
- tripulacion;
- reservas;
- relaciones bidireccionales.

El objetivo es aislar y comprobar la herencia ORM.

## Estrategia elegida: NEW_TABLE

Cada clase tiene su propia tabla:

```text
VUELOS
VUELOS_NACIONALES
VUELOS_INTERNACIONALES
VUELOS_CHARTER
```

La identidad (`numero`) se define una sola vez en la raiz `Vuelo`.

Los campos comunes quedan asociados a `VUELOS` y los campos propios de cada
subtipo quedan en su tabla correspondiente.

Al persistir un objeto concreto, DataNucleus utiliza la tabla de la raiz y la
tabla del subtipo.

## Enum EstadoVuelo

`EstadoVuelo` se persiste como parte del objeto `Vuelo`. DataNucleus soporta
persistencia de enum y, por defecto, puede representarlo mediante su nombre.

## Paso 1 - Enhancement

```powershell
mvn clean process-classes
```

Se espera enhancement de:

```text
aerolinea.dominio.Vuelo
aerolinea.dominio.VueloNacional
aerolinea.dominio.VueloInternacional
aerolinea.dominio.VueloCharter
```

## Paso 2 - Crear schema

```powershell
mvn datanucleus:schema-create
```

## Paso 3 - Inspeccionar las tablas

En PowerShell:

```powershell
mvn exec:java "-Dexec.mainClass=aerolinea.demo.VerSchemaHerencia"
```

## Paso 4 - Persistir subtipos y consultar desde la clase base

```powershell
mvn exec:java "-Dexec.mainClass=aerolinea.demo.DemoHerenciaOrm"
```

La salida debe recuperar los tres objetos aunque la consulta se realice desde
`Vuelo`, demostrando polimorfismo persistente.

## Relacion con el proyecto real

Los campos incluidos son los que ya existen en la jerarquia principal:

### Vuelo

- numero
- origen
- destino
- fecha
- capacidad
- estado

### VueloNacional

- provinciaDestino

### VueloInternacional

- paisDestino
- requierePasaporte

### VueloCharter

- empresaContratante
- costoTotal

La siguiente etapa incorporara relaciones con Persona/Pasajero y despues se
decidira como trasladar el mapeo a las clases reales.
