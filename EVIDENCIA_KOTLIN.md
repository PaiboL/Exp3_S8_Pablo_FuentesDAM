# Evidencia de contenidos Kotlin - Semana 3

Proyecto: SeñalSegura  
Estudiante: Pablo Fuentes  
Entrega: Experiencia 2, Semana 3

## Aplicación de los contenidos

| Contenido | Evidencia en el proyecto |
|---|---|
| `val` y `var` | Estados de las pantallas, variables del analizador y contador de palabras. |
| Tipos de datos | Uso explícito de `String`, `Int`, `Boolean`, `List<QuickPhrase>` y `String?`. |
| Operadores | Cálculo del tiempo de lectura, comparaciones de cantidad de palabras y operadores `&&`, `||` y `!`. |
| `if/else` | Validación de mensajes, registro, recuperación y disponibilidad de voz. |
| `when` | Clasificación del mensaje y selección del color según prioridad. |
| Ciclo `for` | Filtrado de frases, conteo de palabras y búsqueda de palabras de emergencia. |
| Colecciones | Usuarios locales, frases rápidas, categorías, perfiles y preferencias de accesibilidad. |
| Funciones | Validación de correo, análisis de mensajes, autenticación, registro, filtrado y actualización de estado. |
| Seguridad nula | Autenticación retorna `User?`, validación retorna `String?` y el analizador acepta `String?`. |

## Archivos principales

- `util/MessageAnalyzer.kt`: tipos, variables, operadores, `if`, `when`, ciclos y seguridad nula.
- `data/QuickPhraseRepository.kt`: colecciones, ciclo `for` y filtrado combinado.
- `util/UserValidator.kt`: funciones reutilizables y validaciones centralizadas.
- `viewmodel/HomeViewModel.kt`: separación del estado y lógica de la pantalla principal.
- `ui/components/MessageStatsTable.kt`: tabla reutilizable creada con componentes Compose.
- `ui/screens/HomeScreen.kt`: integración de la lógica Kotlin con Jetpack Compose y grilla adaptativa.
- `ui/screens/RecoveryScreen.kt`: indica visiblemente que la recuperación es una simulación local.

## Pruebas incluidas

Las pruebas unitarias comprueban autenticación, registro, filtros, clasificación de mensajes, valores nulos y validación de correo.
