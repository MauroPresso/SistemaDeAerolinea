# Unidad 3 - Etapa 1: DataNucleus, JDO, XML y enhancement

Este laboratorio aislado inicia la incorporacion de la Unidad 3 al proyecto
SistemaDeAerolinea sin modificar todavia la aplicacion principal.

## Objetivo

Demostrar cuatro ideas:

1. DataNucleus se incorpora mediante Maven.
2. Se usa JDO como API de persistencia.
3. La clase persistente se declara mediante metadatos XML (`package.jdo`).
4. Maven ejecuta el enhancement de la clase despues de compilar.

## Estructura

```text
01-datanucleus-jdo-xml/
|-- pom.xml
|-- README.md
`-- src/
    `-- main/
        |-- java/
        |   `-- ar/edu/ifes/aerolinea/orm/VueloPersistente.java
        `-- resources/
            `-- ar/edu/ifes/aerolinea/orm/package.jdo
```

## Que cambia respecto de la persistencia actual

La aplicacion principal sigue usando:

```text
Servicio<T> -> IRepositorio<T> -> RepositorioArchivo<T> -> .dat
```

Este laboratorio estudia la tecnologia que luego permitira construir otra
implementacion de persistencia basada en ORM.

## Ejecutar

Desde esta carpeta:

```powershell
mvn clean compile
```

Durante `process-classes`, el plugin de DataNucleus ejecuta el goal `enhance`.

Tambien puede invocarse manualmente:

```powershell
mvn datanucleus:enhance
```

## Por que empezamos con XML

La Unidad 3 distingue metadatos XML de anotaciones. En esta etapa se usa XML
intencionalmente para que la clase Java no tenga anotaciones de persistencia y
la configuracion quede separada.

## Proximo paso

Agregar el datastore relacional y SchemaTool para crear el schema. Luego se
hara el mismo ejemplo con anotaciones y finalmente se evaluara como integrar
esta persistencia con `IRepositorio<T>` del SistemaDeAerolinea.
