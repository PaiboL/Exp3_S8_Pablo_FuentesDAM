# Evidencia de distribución · Semana 8

## Compilación y versión

- Application ID: `cl.duoc.senalsegura`
- `versionCode`: 8
- `versionName`: 1.0.0
- `minSdk`: 24; `targetSdk`: 36; `compileSdk`: 36.
- Variante: `release`, generada con `assembleRelease`.
- Archivo: `distribucion/SenalSegura-1.0.0-release-signed.apk` (10.6 MB).

## Firma e integridad

- Firma válida con APK Signature Scheme v2 y v3; llave RSA de 4096 bits.
- `zipalign -c -v 4`: correcto.
- SHA-256 APK: `9eb620f0c229a786d851e2d48958e11f41bb9811894df8c5a9a7f809cc15a949`.
- SHA-256 del certificado: `cd2b7832c27dcd724388692576acb3cae9be4d3cfefc6b84c5a0576aa760e0f4`.
- Salida completa de `apksigner verify --verbose --print-certs`: `documentacion/VERIFICACION_FIRMA_APK.txt`.

La clave privada temporal usada para esta compilación no se incluye. Para futuras actualizaciones de producción se debe crear y resguardar una clave de carga permanente fuera del repositorio.

## Estado de publicación

Se preparó el APK firmado para instalación y distribución de prueba. Según la aclaración del docente publicada el 3 de octubre, la actividad solicita explicar el procedimiento de Play Store y no comprar una cuenta de desarrollador. Por eso no se declara una publicación efectiva en Google Play. Los pasos de distribución quedan descritos en `GUIA_DISTRIBUCION_SEMANA8.md` y en el formato de respuesta.
