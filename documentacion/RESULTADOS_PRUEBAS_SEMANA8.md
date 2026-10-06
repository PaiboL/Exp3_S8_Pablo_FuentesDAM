# Resultados de pruebas · Semana 8

**Herramienta:** JUnit 4 ejecutado mediante Gradle `testDebugUnitTest`. **Total:** 40 pruebas; **aprobadas:** 40; **fallidas:** 0; **errores:** 0; **omitidas:** 0; **tiempo reportado por JUnit:** 0.108 s.

## Clases ejecutadas

| Clase | Pruebas | Resultado |
|---|---:|---|
| `AlertRepositoryTest` | 7 | 7/7 aprobadas |
| `FirebaseModelsTest` | 4 | 4/4 aprobadas |
| `QuickPhraseRepositoryTest` | 4 | 4/4 aprobadas |
| `UserRepositoryTest` | 3 | 3/3 aprobadas |
| `AdvancedKotlinTest` | 5 | 5/5 aprobadas |
| `MessageAnalyzerTest` | 3 | 3/3 aprobadas |
| `RegistrationValidationTest` | 6 | 6/6 aprobadas |
| `UserValidatorTest` | 3 | 3/3 aprobadas |
| `AlertsViewModelTest` | 3 | 3/3 aprobadas |
| `HomeViewModelTest` | 2 | 2/2 aprobadas |

## Cobertura funcional

- Validación de email, registro, contraseña, consentimiento y casos límite.
- Análisis de mensajes, conteo de palabras y prioridad de alertas.
- Repositorios locales, modelos Firebase y frases rápidas.
- ViewModels de inicio y alertas, incluidos filtro/acciones CRUD y estados.
- Funciones Kotlin avanzadas y helpers de transformación.

Los archivos XML y el reporte HTML de Gradle se adjuntan en `documentacion/resultados_pruebas/`. Son pruebas unitarias locales (`src/test`); no se reporta ejecución de Espresso, Robolectric ni un emulador.
