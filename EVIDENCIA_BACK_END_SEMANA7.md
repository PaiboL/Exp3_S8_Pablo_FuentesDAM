# Evidencia técnica del back end - Semana 7

## Correspondencia con la pauta

La instrucción institucional utiliza como ejemplo las vistas Minuta, Receta y BuscarDispositivo. SeñalSegura conserva el tema libre aprobado en las semanas anteriores y aplica esas responsabilidades equivalentes al listado de alertas, el detalle editable y la búsqueda de alertas sincronizadas.

| Criterio | Evidencia implementada | Ruta principal |
|---|---|---|
| Interfaz Android | Pantallas Compose adaptativas, formularios validados, diálogos de confirmación y estados de carga/error | `ui/screens/BackendScreen.kt` |
| Repositorio Git | Historial continuo desde Semana 2 y commit específico de Semana 7 | `.git/` |
| Extensiones KTX | `SharedPreferences.edit`, `use`, `toUri`, lifecycle KTX y ViewModel KTX | `data/SessionPreferences.kt` y evidencia de Semana 6 |
| Navegación intuitiva | Destino `backend`, acceso desde Inicio, retorno superior y confirmaciones antes de eliminar | `navigation/AppNavigation.kt` |
| Persistencia Firebase | Authentication, Realtime Database, sincronización, CRUD y reglas por usuario | `data/firebase/`, `firebase.database.rules.json` |

## Autenticación y autorización

`FirebaseAuthRepository` implementa:

- creación de cuentas con `createUserWithEmailAndPassword`;
- inicio de sesión con `signInWithEmailAndPassword`;
- recuperación mediante `sendPasswordResetEmail`;
- cierre de sesión con `signOut`;
- creación del perfil del usuario en `users/{uid}`.

Las reglas de la base exigen `auth != null` y verifican que el `uid` autenticado coincida con la ruta solicitada. Así, un usuario no puede leer ni modificar la información de otro.

## Persistencia y sincronización

`FirebaseBackendRepository` observa `alerts/{uid}` mediante `addValueEventListener`. Cuando Realtime Database cambia, el nuevo listado actualiza `BackendUiState` y Compose vuelve a dibujar la pantalla.

La persistencia sin conexión se habilita al iniciar la aplicación mediante `setPersistenceEnabled(true)`. La disponibilidad de Firebase se centraliza en `FirebaseBootstrap`.

## CRUD de alertas

- Crear: genera una clave con `push()` y almacena el objeto con `setValue()`.
- Leer: convierte cada `DataSnapshot` en `FirebaseAlertRecord`.
- Actualizar: modifica los campos mediante `updateChildren()`.
- Eliminar: remueve el nodo con `removeValue()`.

El formulario valida título, mensaje, categoría y límites de caracteres antes de solicitar una operación remota.

## CRUD de usuarios

- Crear: Registro crea la cuenta Auth y el perfil en Realtime Database.
- Leer: la pantalla Back end observa `users/{uid}`.
- Actualizar: permite modificar nombre y preferencia de comunicación.
- Eliminar: elimina el perfil de Realtime Database, conservando deliberadamente la cuenta de Authentication para evitar una eliminación irreversible accidental.

## Persistencia local y KTX

`SessionPreferences` conserva nombre, correo, origen de la sesión y modo de alto contraste. Usa la extensión `SharedPreferences.edit` de Android KTX para evitar llamadas manuales a `Editor.apply()`.

El modo local de respaldo permite ejecutar la aplicación sin un proyecto Firebase externo. La interfaz informa claramente este estado; no simula una conexión remota.

## Pruebas

`FirebaseModelsTest` verifica normalización, validación y consistencia de los registros iniciales. Estas pruebas se agregan a las existentes para Kotlin, repositorios, validadores y ViewModels.
