# Distribución y publicación · SeñalSegura

## Distribución de prueba

El APK firmado de la entrega está en `distribucion/SenalSegura-1.0.0-release-signed.apk`. Para compartirlo con un grupo reducido se puede usar Firebase App Distribution: habilitar el producto en el proyecto Firebase, crear un grupo de testers, subir el APK firmado, invitar sus correos y revisar sus comentarios. Como alternativa de prueba, se puede compartir el archivo APK mediante un enlace privado y explicar a los testers cómo instalarlo en Android.

## Publicación en Google Play (proceso documentado)

1. Preparar nombre, descripción, icono, capturas de pantalla, política de privacidad y declaración de datos.
2. En Android Studio, seleccionar **Build → Generate Signed Bundle / APK → Android App Bundle** y crear el bundle de distribución con una clave de carga protegida. El APK de prueba incluido no sustituye a un AAB requerido por el flujo de Play.
3. Crear la ficha de la app en Play Console; completar categoría, clasificación de contenido, público, seguridad de datos y declaraciones solicitadas por la plataforma.
4. Cargar el bundle en una pista de pruebas interna/cerrada; revisar compatibilidad, instalación, permisos, accesibilidad y comentarios.
5. Corregir observaciones, enviar a revisión y, tras aprobación, publicar gradualmente en producción. Mantener `versionCode` creciente y resguardar la clave de carga y credenciales fuera del repositorio.

La tarea no ejecuta una publicación real en Google Play: el anuncio del docente del 3 de octubre especifica que basta explicar el proceso y no se solicita comprar una cuenta de desarrollador.
