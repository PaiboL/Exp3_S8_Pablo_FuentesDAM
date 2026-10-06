# Evidencia de programación orientada a objetos - Semana 4

Proyecto: SeñalSegura  
Estudiante: Pablo Fuentes  
Entrega: Experiencia 2, Semana 4

## Cumplimiento técnico

| Contenido | Evidencia implementada |
|---|---|
| Clases y objetos | `AccessibleAlert`, `EmergencyAlert`, `InformationAlert` y cinco objetos creados en `AlertRepository`. |
| Propiedades y métodos | Cada alerta posee identificador, título, mensaje, categoría y métodos para mostrar o reproducir el contenido. |
| Constructores | Las alertas se inicializan mediante constructores principales parametrizados. |
| `private` | Protege el array de alertas, el mapa de descripciones y la acción sugerida. |
| `protected` | Permite que las subclases utilicen el mensaje definido en la clase base. |
| `internal` | Limita el texto de búsqueda al módulo de la aplicación. |
| Herencia | `EmergencyAlert` e `InformationAlert` heredan de `AccessibleAlert`. |
| Interfaz | `AccessibleContent` exige símbolo y método `textForSpeech()`. |
| `Array` | `alertArray` almacena exactamente cinco alertas accesibles. |
| `List` | Expone las alertas y categorías ordenadas para la interfaz. |
| `Set` | Mantiene identificadores favoritos sin duplicados. |
| `Map` | Registra cantidad de usos y descripciones por categoría. |

## Integración funcional

- La pantalla principal permite abrir “Alertas accesibles”.
- La nueva pantalla filtra las alertas por categoría.
- El usuario puede marcar favoritas sin duplicarlas.
- Cada selección incrementa un contador almacenado en un mapa.
- Al usar una alerta, el mensaje vuelve a la pantalla principal para mostrarlo en grande o reproducirlo por voz.
- Las alertas de emergencia agregan una acción específica mediante sobrescritura de métodos.
- La grilla se adapta al ancho disponible en distintos dispositivos Android.

## Archivos principales

- `model/AccessibleAlert.kt`
- `data/AlertRepository.kt`
- `viewmodel/AlertsViewModel.kt`
- `ui/screens/AlertsScreen.kt`
- `navigation/AppNavigation.kt`

## Pruebas incluidas

Las pruebas comprueban que existen cinco alertas únicas, el filtrado por categoría y favoritos, el comportamiento de la subclase de emergencia, la búsqueda, el conjunto de favoritos y el mapa de contadores.
