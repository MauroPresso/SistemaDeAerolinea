# Unidad 3 - Etapa 4: Metadatos mediante anotaciones JDO

Hasta la Etapa 3 el mapeo ORM se declaraba en:

```text
src/main/resources/.../package.jdo
```

En esta etapa NO existe `package.jdo`.

La metadata se declara directamente en la clase Java mediante anotaciones:

```java
@PersistenceCapable(...)
@DatastoreIdentity(...)
public class VueloAnotado {

    @Persistent
    @Column(name = "NUMERO")
    private String numero;
}
```

## Comparacion

### XML

```text
VueloPersistente.java
        +
package.jdo
        |
        v
DataNucleus
```

### Anotaciones

```text
VueloAnotado.java
(con @PersistenceCapable, @Column, ...)
        |
        v
DataNucleus
```

El mecanismo ORM posterior es el mismo:

```text
enhancement -> SchemaTool -> PersistenceManager -> H2
```

## Paso 1

```powershell
mvn clean process-classes
```

Debe aparecer el enhancement de:

```text
ar.edu.ifes.aerolinea.orm.VueloAnotado
```

## Paso 2

```powershell
mvn datanucleus:schema-create
```

Debe crear:

```text
data/aerolinea_anotaciones.mv.db
```

y la tabla:

```text
VUELOS_ANOTACIONES
```

## Paso 3 - Inspeccionar schema

En PowerShell:

```powershell
mvn exec:java "-Dexec.mainClass=ar.edu.ifes.aerolinea.orm.VerSchemaAnotaciones"
```

## Paso 4 - Persistir objetos

```powershell
mvn exec:java "-Dexec.mainClass=ar.edu.ifes.aerolinea.orm.DemoAnotaciones"
```

## Idea central para el examen

XML y anotaciones son dos formas distintas de suministrar metadata de
persistencia.

En XML la configuracion queda separada de la clase Java.

Con anotaciones la configuracion queda junto al codigo de la entidad.

En ambos casos DataNucleus puede usar esa metadata para enhancement, schema y
persistencia JDO.
