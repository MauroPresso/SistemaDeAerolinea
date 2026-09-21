# Unidad 3 - Etapa 3: Persistencia real con JDO

En esta etapa dejamos de crear solamente el schema y comenzamos a guardar y
recuperar objetos Java mediante DataNucleus.

## Recorrido

```text
new VueloPersistente(...)
        |
        v
PersistenceManager
        |
        v
Transaction
        |
        v
makePersistent(...)
        |
        v
DataNucleus ORM
        |
        v
H2 / tabla VUELOS
```

Luego se realiza el camino inverso mediante una consulta JDO.

## Conceptos nuevos

### PersistenceManagerFactory

Es la fabrica encargada de crear objetos `PersistenceManager`.

### PersistenceManager

Administra el ciclo de vida de los objetos persistentes y las operaciones con
el datastore.

### Transaction

Agrupa las operaciones que deben confirmarse o deshacerse como una unidad.

```java
tx.begin();
pm.makePersistent(objeto);
tx.commit();
```

Si ocurre un problema:

```java
tx.rollback();
```

## Paso 1 - Enhancement

```powershell
mvn clean process-classes
```

## Paso 2 - Crear schema

Como esta etapa usa su propia base H2, ejecutar:

```powershell
mvn datanucleus:schema-create
```

## Paso 3 - Persistir y consultar

En PowerShell:

```powershell
mvn exec:java "-Dexec.mainClass=ar.edu.ifes.aerolinea.orm.DemoPersistencia"
```

Salida esperada:

```text
Se persistieron 3 vuelos correctamente.

Vuelos recuperados desde H2:
- VueloPersistente{numero='AR1001', origen='Neuquen', destino='Buenos Aires'}
- VueloPersistente{numero='AR2002', origen='Buenos Aires', destino='Bariloche'}
- VueloPersistente{numero='AR3003', origen='Cordoba', destino='Mendoza'}

BUILD SUCCESS
```

El orden de recuperacion puede variar.

## Importante

Cada vez que se ejecuta `DemoPersistencia` se agregan tres filas nuevas. Esto es
intencional: permite comprobar que los datos quedan realmente persistidos entre
ejecuciones.

Todavia no se modifica `SistemaDeAerolinea/src`. Primero se domina JDO de forma
aislada; mas adelante se implementara un repositorio ORM compatible con la
arquitectura existente.
