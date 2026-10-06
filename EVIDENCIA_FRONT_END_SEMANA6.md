# Evidencia de front end - Semana 6

Proyecto: SeñalSegura  
Estudiante: Pablo Andrés Fuentes Castillo  
Entrega: Experiencia 3, Semana 6

## Correspondencia con las instrucciones

| Requerimiento | Evidencia implementada | Uso en SeñalSegura |
|---|---|---|
| Login | `ui/screens/LoginScreen.kt` | Acceso con validación y campos adaptativos. |
| Registro | `ui/screens/RegisterScreen.kt` | Creación local de un perfil y preferencias de accesibilidad. |
| Recuperar contraseña | `ui/screens/RecoveryScreen.kt` | Validación del correo y respuesta visible. |
| HomeMenú | `ui/screens/HomeScreen.kt` | Menú principal, mensaje grande, voz, alerta visual y frases rápidas. |
| Minuta | `ui/screens/AlertsScreen.kt` | Catálogo filtrable de cinco alertas accesibles. |
| Receta | `ui/screens/AlertDetailScreen.kt` | Detalle, acción sugerida y selección de una alerta. |
| Fragments | Pantallas `@Composable` conectadas por `NavHost` | Cada pantalla es modular, reutilizable y mantiene su estado de navegación. |
| Views | `Text`, `Button`, `OutlinedTextField`, `Switch`, `Checkbox` e `Image` | Presentación e interacción accesible. |
| ViewGroups | `Scaffold`, `Column`, `Row`, `Card` y `LazyVerticalGrid` | Organización adaptable en teléfonos y pantallas anchas. |
| Content Provider | `provider/AlertContentProvider.kt` | Expone las alertas como un cursor estructurado de solo lectura. |
| Content Resolver | `data/AlertProviderClient.kt` | Consulta la URI del proveedor y reconstruye los objetos de dominio. |
| Widgets | `widget/SignalSafeWidgetProvider.kt` y `res/layout/widget_signal_safe.xml` | Acceso rápido desde la pantalla de inicio de Android. |
| Palette Texts | `ui/components/AlertPalette.kt` y `PaletteHeader` | Aplica al texto colores extraídos de la ilustración. |
| Buttons | Botones de acceso, filtrado, uso de alerta y navegación | Ejecutan acciones visibles con áreas táctiles amplias. |
| Palette Widgets | `AccessibleAlertCard` y `AlertDetailScreen` | Personaliza tarjetas y botones con colores dominantes, vibrantes y apagados. |
| Events | `onClick`, `onValueChange`, `onCheckedChange` y `combinedClickable` | Responde a escritura, selección, toque y pulsación prolongada. |

La aplicación mantiene Jetpack Compose porque la sesión sincrónica establece su equivalencia con Fragments, Views y ViewGroups tradicionales.

## Evidencia de extensiones KTX

| Extensión KTX | Archivo | Aplicación |
|---|---|---|
| `String.toUri()` | `provider/AlertContract.kt` | Construye la URI segura del ContentProvider. |
| `Cursor.getStringOrNull()` | `data/AlertProviderClient.kt` | Lee la acción opcional sin comprobaciones manuales del cursor. |

La consulta también utiliza `use` para cerrar automáticamente el cursor. Las dependencias `androidx.core:core-ktx`, `androidx.lifecycle:lifecycle-runtime-ktx`, `androidx.lifecycle:lifecycle-viewmodel-ktx` y `androidx.palette:palette-ktx` están declaradas en Gradle.

## Seguridad y accesibilidad

- El proveedor está registrado con `android:exported="false"` y es de solo lectura.
- No comparte credenciales ni información personal del usuario.
- Los elementos visuales incluyen descripciones semánticas para TalkBack.
- Los colores dinámicos calculan texto blanco o negro según la luminancia del fondo.
- La grilla usa `GridCells.Adaptive`, por lo que cambia el número de columnas según el ancho.
- El alto contraste, texto a voz, vibración y alertas visuales continúan disponibles.

## Pruebas y trazabilidad

- `AlertRepositoryTest` comprueba búsqueda por identificador y filtrado de datos provenientes del proveedor.
- Las pruebas anteriores de usuarios, mensajes, POO, colecciones y Kotlin avanzado se conservan.
- El repositorio Git mantiene los commits de las semanas 2, 3, 4, 5 y 6.
