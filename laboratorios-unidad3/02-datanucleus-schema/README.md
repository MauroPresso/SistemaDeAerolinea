# Unidad 3 - Etapa 2: RDBMS, H2 y SchemaTool

Esta etapa parte del laboratorio de enhancement de la Etapa 1 y agrega una base
de datos relacional real.

## Objetivo

Demostrar el recorrido:

```text
VueloPersistente.java
        |
        +-- package.jdo
        |
        v
DataNucleus + JDO
        |
        v
datanucleus-rdbms
        |
        v
SchemaTool
        |
        v
H2
        |
        v
tabla VUELOS
```

## Por que H2

H2 es una base relacional embebida. Para este laboratorio no hace falta instalar
MySQL, PostgreSQL ni un servidor externo.

La base se crea como archivo local dentro de:

```text
data/aerolinea_orm.mv.db
```

## Archivos importantes

- `pom.xml`: dependencias DataNucleus, RDBMS, JDO y H2.
- `datanucleus.properties`: conexion al datastore.
- `package.jdo`: mapeo XML objeto-relacional.
- `VueloPersistente.java`: clase Java persistente.
- `VerSchema.java`: inspector didactico de tablas y columnas.

## Paso 1 - Compilar y hacer enhancement

```powershell
mvn clean process-classes
```

Debe finalizar con `BUILD SUCCESS` y DataNucleus debe indicar que
`VueloPersistente` fue enhanced.

## Paso 2 - Crear el schema

```powershell
mvn datanucleus:schema-create
```

SchemaTool usa `datanucleus.properties`, abre la base H2 y genera la tabla
definida por `package.jdo`.

Luego debe aparecer:

```text
data/aerolinea_orm.mv.db
```

## Paso 3 - Verificar la tabla

```powershell
mvn exec:java -Dexec.mainClass=ar.edu.ifes.aerolinea.orm.VerSchema
```

Se espera una salida similar a:

```text
Base de datos: H2
Tablas creadas por SchemaTool:
- VUELOS
    DATASTORE_ID : ...
    NUMERO : ...
    ORIGEN : ...
    DESTINO : ...
```

Los tipos concretos pueden variar segun el dialecto utilizado por DataNucleus.

## Paso 4 - Borrar el schema (opcional)

```powershell
mvn datanucleus:schema-delete
```

Este comando sirve para estudiar el ciclo completo. No es necesario ejecutarlo
si se quiere conservar la base creada.

## Importante

Todavia NO estamos persistiendo objetos desde la aplicacion.

La meta de esta etapa es separar claramente dos procesos:

1. enhancement de las clases persistentes;
2. creacion del schema relacional.

La Etapa 3 incorporara `PersistenceManager`, transacciones y persistencia de
instancias de `VueloPersistente`.
