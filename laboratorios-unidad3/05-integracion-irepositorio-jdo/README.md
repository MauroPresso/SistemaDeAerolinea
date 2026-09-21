# Unidad 3 - Etapa 5: Integracion con IRepositorio

Esta etapa conecta ORM/JDO con la arquitectura que ya usa SistemaDeAerolinea.

## Objetivo

Hasta ahora los laboratorios usaban directamente `PersistenceManager`.

Ahora se introduce una implementacion JDO detras de la misma abstraccion de
repositorio que usa el proyecto principal:

```text
IRepositorio<VueloPersistente>
            ^
            |
   RepositorioJdoVuelo
            |
            v
       DataNucleus
            |
            v
            H2
```

y `Servicio<T>` sigue dependiendo solamente de `IRepositorio<T>`:

```text
Servicio<VueloPersistente>
            |
            v
IRepositorio<VueloPersistente>
            |
            v
RepositorioJdoVuelo
```

## Por que RepositorioJdoVuelo no es generico todavia

El proyecto real contiene:

- `Vuelo` abstracto;
- `VueloNacional`;
- `VueloInternacional`;
- `VueloCharter`;
- `Pasajero`;
- `Tripulante`;
- listas y relaciones bidireccionales.

Mapear todo eso en una sola etapa mezclaria demasiados conceptos.

Por eso esta primera integracion usa un modelo ORM controlado de vuelo. La
arquitectura queda demostrada antes de tocar el dominio estable.

## Semantica de guardar

El actual `RepositorioArchivo<T>` guarda una fotografia completa de la lista.

Para mantener el mismo significado, `RepositorioJdoVuelo.guardar(...)`:

1. inicia una transaccion;
2. elimina las filas actuales;
3. persiste copias nuevas de los vuelos recibidos;
4. hace commit.

Esto permite que `Servicio.reemplazarTodos(...)` y `Servicio.guardar()` sigan
teniendo el mismo comportamiento conceptual.

## Paso 1 - Enhancement

```powershell
mvn clean process-classes
```

## Paso 2 - Schema

```powershell
mvn datanucleus:schema-create
```

La tabla esperada es:

```text
VUELOS_REPOSITORIO
```

y `NUMERO` funciona como clave primaria de aplicacion.

## Paso 3 - Ejecutar la integracion

En PowerShell:

```powershell
mvn exec:java "-Dexec.mainClass=aerolinea.demo.DemoIntegracionRepositorio"
```

La demostracion crea tres instancias distintas de `Servicio`.

El primer servicio guarda AR5001 y AR5002.

El segundo servicio se crea desde cero y debe recuperarlos desde H2. Luego agrega
AR5003 y vuelve a guardar.

El tercer servicio se crea nuevamente desde cero y debe recuperar los tres vuelos.

## Idea central

La capa de servicio no necesita saber si la persistencia esta implementada con:

```text
RepositorioArchivo -> ObjectOutputStream -> .dat
```

o con:

```text
RepositorioJdoVuelo -> DataNucleus -> H2
```

Ese desacoplamiento es precisamente el valor de programar contra
`IRepositorio<T>`.

## Siguiente etapa

Una vez validado este laboratorio se puede decidir como llevar ORM al dominio
real. Antes de modificar `aerolinea.dominio.Vuelo` conviene modelar:

- estrategia de herencia;
- identidad de vuelos;
- persistencia de enum `EstadoVuelo`;
- relaciones con pasajeros y tripulacion;
- reservas bidireccionales.

No se recomienda migrar todo de una sola vez.
