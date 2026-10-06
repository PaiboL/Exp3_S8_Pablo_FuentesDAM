# SeñalSegura · Entrega Semana 8

Aplicación Android desarrollada en Kotlin con Jetpack Compose y Material 3 para facilitar la comunicación visual de personas con discapacidad auditiva. Este ZIP integra la evolución del proyecto, la documentación de respuesta, las pruebas JUnit y un APK de distribución de pruebas firmado.

## Contenido

- `app/src/main`: aplicación móvil, interfaz Compose, ViewModels, repositorios, ContentProvider y widget.
- `app/src/test`: pruebas unitarias JUnit.
- `Formato_Respuesta_S8_Pablo_Fuentes.docx` y `.pdf`: formato oficial de respuesta completado.
- `documentacion/resultados_pruebas/`: reporte HTML y archivos XML de Gradle/JUnit.
- `distribucion/SenalSegura-1.0.0-release-signed.apk`: APK firmado y verificado.
- `GUIA_DISTRIBUCION_SEMANA8.md`: distribución de pruebas y pasos de publicación.
- `.git`: historial local de cambios.
- Repositorio remoto de las experiencias anteriores: https://github.com/PaiboL/Exp2_S5_Pablo_FuentesDAM (los cambios de S8 incluidos en este ZIP no se han subido).

## Configuración y comandos

Requisitos del proyecto: Android Studio/Gradle, JDK 21, Android SDK 36 y Build Tools 36.1.0. Abre la carpeta del proyecto en Android Studio y sincroniza Gradle. La configuración Firebase se encuentra en `app/google-services.json`; descarga tu configuración de Firebase para el applicationId `cl.duoc.senalsegura` si se reemplaza el archivo.

```bash
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

La construcción de este ZIP se verificó con Gradle 9.5.0, AGP 9.3.1, JDK 21, SDK 36 y Build Tools 36.1.0. Los comandos se ejecutaron en el entorno de preparación de la entrega.

## Funcionalidades principales

Inicio de sesión, registro y recuperación; inicio con mensajes visibles y texto a voz; historial de alertas, filtro por categoría, detalle y operaciones CRUD; sincronización con Firebase Realtime Database; sesión y preferencias básicas con SharedPreferences; accesibilidad mediante TalkBack, alto contraste, grillas adaptativas, Palette y respuesta háptica.

## Pruebas y distribución

`testDebugUnitTest`: 40 pruebas JUnit, cero fallos, errores o pruebas omitidas. El APK `1.0.0` está firmado con esquemas v2/v3, alineado con `zipalign` y cuenta con huella SHA-256 documentada en `documentacion/VERIFICACION_FIRMA_APK.txt`. La clave privada de firma no forma parte del repositorio. El docente indicó que no se debe comprar una cuenta Play Console para esta actividad; por eso la publicación en tienda se describe en el informe, sin afirmar que se haya realizado.
