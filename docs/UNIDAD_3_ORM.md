# Unidad 3 - ORM con DataNucleus/JDO

La Unidad 3 se incorpora al SistemaDeAerolinea mediante una segunda estrategia
de persistencia, manteniendo la persistencia por archivos como alternativa.

## Arquitectura

```text
                   IRepositorio<T>
                     /          \
                    /            \
       RepositorioArchivo     RepositorioJdo
             |                     |
            .dat              DataNucleus/JDO
                                   |
                                   H2
```

`Servicio<T>` sigue dependiendo solamente de `IRepositorio<T>`.

## Dominio persistente

Jerarquia de vuelos:

```text
Vuelo
|-- VueloNacional
|-- VueloInternacional
`-- VueloCharter
```

Jerarquia de personas:

```text
Persona
|-- Pasajero
`-- Tripulante
```

Relaciones:

```text
Vuelo <------ M:N ------> Pasajero
Vuelo ------ coleccion --> Tripulante
```

## Compilar

Desde la raiz:

```powershell
mvn clean package
```

Durante `process-classes` se ejecuta automaticamente el enhancement JDO.

## Crear el schema ORM

Luego de compilar:

```powershell
mvn datanucleus:schema-create
```

La base se genera en:

```text
data/aerolinea_orm.mv.db
```

## Ejecutar con archivos

Swing:

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar
```

Consola:

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar --consola
```

## Ejecutar con ORM

Swing:

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar --orm
```

Consola:

```powershell
java -jar target/SistemaDeAerolinea-1.0-SNAPSHOT.jar --orm --consola
```

## Importante

Los archivos `.dat` y la base H2 son mecanismos separados.

El modo `--orm` no migra automaticamente los datos existentes de `.dat`.
Esto permite comparar ambos mecanismos de persistencia sin destruir el estado
anterior.

## Conceptos de Unidad 3 demostrados

- ORM.
- DataNucleus.
- JDO.
- Maven.
- anotaciones de persistencia.
- enhancement.
- SchemaTool.
- H2/RDBMS.
- PersistenceManager y Transaction.
- identidad de aplicacion.
- herencia ORM.
- enum persistente.
- relaciones muchos-a-muchos.
- tablas de relacion.
- desacoplamiento mediante IRepositorio.
