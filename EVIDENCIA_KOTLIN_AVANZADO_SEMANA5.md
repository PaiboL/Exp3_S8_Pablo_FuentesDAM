# Evidencia de Kotlin avanzado - Semana 5

Proyecto: SeñalSegura  
Estudiante: Pablo Andrés Fuentes Castillo  
Entrega: Experiencia 2, Semana 5

## Trazabilidad técnica

| Contenido | Evidencia implementada | Uso funcional |
|---|---|---|
| Función de orden superior | `buildAlertPredicate(...)` retorna `(AccessibleAlert) -> Boolean`. | Combina categoría, favoritas y texto de búsqueda sin duplicar condiciones. |
| Función `inline` | `executeAccessibleAction(...)` recibe las lambdas `action`, `onError` y `onFinally`. | Ejecuta la validación del mensaje, presenta el error esperado y registra cada intento. |
| Lambdas | `filter(predicate)`, `sortedBy { ... }`, `count { ... }` y callbacks de Compose. | Filtra y ordena alertas, cuenta palabras y conecta eventos de la interfaz. |
| Función de extensión | `String.toAccessibleMessage()`. | Normaliza espacios y valida que el mensaje no esté vacío ni supere 180 caracteres. |
| Propiedad de extensión | `String.wordCount`. | Centraliza el conteo usado por `MessageAnalyzer`. |
| Propiedad de extensión | `AccessibleAlert.accessibilitySummary`. | Construye la descripción que la tarjeta entrega a TalkBack. |
| `try/catch/finally` | Implementado dentro de `executeAccessibleAction`. | Captura solo `InvalidAccessibleMessageException`; el bloque `finally` registra el intento aun cuando falla. |
| Excepción específica | `InvalidAccessibleMessageException`. | Evita capturar indiscriminadamente errores de programación no relacionados. |

## Archivos principales

- `app/src/main/java/cl/duoc/senalsegura/util/AdvancedKotlin.kt`
- `app/src/main/java/cl/duoc/senalsegura/data/AlertRepository.kt`
- `app/src/main/java/cl/duoc/senalsegura/util/MessageAnalyzer.kt`
- `app/src/main/java/cl/duoc/senalsegura/viewmodel/HomeViewModel.kt`
- `app/src/main/java/cl/duoc/senalsegura/viewmodel/AlertsViewModel.kt`
- `app/src/main/java/cl/duoc/senalsegura/ui/screens/HomeScreen.kt`
- `app/src/main/java/cl/duoc/senalsegura/ui/screens/AlertsScreen.kt`

## Pruebas relacionadas

- `AdvancedKotlinTest`: extensiones, normalización, excepción y ejecución de `finally`.
- `AlertRepositoryTest`: combinación de filtros mediante la lambda retornada.
- `AlertsViewModelTest`: búsqueda integrada en la pantalla de alertas.
- `HomeViewModelTest`: resultado visible, mensaje de error y contador actualizado por `finally`.

## Decisión de diseño

La aplicación no usa excepciones para condiciones normales de navegación. La excepción específica se limita a la frontera de validación del mensaje escrito, donde existe una recuperación clara para el usuario. Las demás excepciones no se silencian, lo que facilita detectar errores reales durante el desarrollo.
