# Plan de Implementación: App de Apuntes Académicos (MVC + DAO)

Este plan detalla la implementación de la aplicación Android en **Java** cumpliendo estrictamente con los requisitos de la rúbrica (15 puntos + 1 punto extra) y el patrón arquitectónico **MVC + DAO**.

## 1. Arquitectura y Estructura de Clases (MVC + DAO)

- **Model (`com.uth.exa1par0212.model`)**:
  - `Apunte`: Clase modelo con los 6 campos requeridos (`id`, `asignatura`, `tema`, `fecha`, `descripción`, `audio`).
  - `Asignatura`: Modelo para gestionar asignaturas (soporte para el punto extra de agregar asignaturas).
- **DAO (`com.uth.exa1par0212.dao`)**:
  - `DatabaseHelper`: Extiende `SQLiteOpenHelper` para la gestión de la base de datos SQLite.
  - `ApunteDAO`: Encapsula operaciones CRUD (`insert`, `getAll`, `getByAsignatura`, `update`, `delete`) utilizando consultas parametrizadas (`?`).
  - `AsignaturaDAO`: Gestión de asignaturas predeterminadas y personalizadas.
- **Controller (`com.uth.exa1par0212.controller`)**:
  - `RegistroController`: Lógica de validación (regex para fecha `AAAA-MM-DD`, regex para tema sin símbolos, campos obligatorios, control de grabación de audio con `MediaRecorder`).
  - `ApuntesController`: Lógica para listar, filtrar por asignatura, actualizar, eliminar y reproducir audio (`MediaPlayer`).
  - `DictadoController`: Lógica de reconocimiento de voz (`RecognizerIntent`) y gestión de permisos (`RECORD_AUDIO` con `AlertDialog.Builder`).
- **View (`com.uth.exa1par0212.view` o raíz)**:
  - `MainActivity` / `RegistroActivity`: Pantalla 1 (Registro de Apuntes).
  - `ApuntesActivity`: Pantalla 2 (Consulta, listado, filtro por asignatura, CRUD, reproducción de audio).
  - `DictadoActivity`: Pantalla 3 (Dictado por voz a texto).

---

## 2. Pantallas y Componentes

### Pantalla 1: Registro de Apuntes (`MainActivity` / `RegistroActivity`)
- Spinner de Asignaturas (Programación Móvil I, Base de Datos, Redes I, Inglés Técnico + botón/opción para agregar nueva asignatura - **1 pto extra**).
- Campos: Tema, Fecha (`AAAA-MM-DD`), Descripción.
- Grabación de voz: Botón Grabar / Detener (`MediaRecorder`), indicador de tiempo o estado, botón Reproducir localmente.
- Validaciones con regex:
  - Campos obligatorios (alertas si están vacíos).
  - Fecha (`^\d{4}-\d{2}-\d{2}$`).
  - Tema sin símbolos (`^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s]+$`).
- Botón "Salvar Apunte" y botón "Ver Apuntes".
- Botón para invocar la Pantalla de Dictado (Speech-to-Text).

### Pantalla 2: Apuntes (`ApuntesActivity`)
- Spinner o filtro de asignaturas con consulta parametrizada (`?`).
- Lista (`ListView`) de apuntes almacenados.
- Botones / Acciones por ítem o selección:
  - Reproducir nota de voz (`MediaPlayer`).
  - Actualizar apunte (editar campos y guardar).
  - Eliminar apunte.

### Pantalla 3: Dictado (`DictadoActivity`)
- Solicitar permiso `RECORD_AUDIO` en tiempo de ejecución.
- Si se deniega, explicar mediante `AlertDialog.Builder`.
- Lanzar `RecognizerIntent` (`ACTION_RECOGNIZE_SPEECH`).
- Devolver el texto reconocido a la Pantalla de Registro para rellenar la descripción.

---

## 3. Plan de Verificación

### Pruebas Automatizadas y Compilación
- Ejecutar `gradle_build` para verificar que compila correctamente sin errores ni advertencias de lint.
- Verificar pruebas unitarias e instrumentadas existentes.

### Verificación Manual
- Desplegar en emulador o dispositivo físico.
- Comprobar validaciones de fecha y tema con expresiones regulares.
- Comprobar grabación de audio con `MediaRecorder` y reproducción con `MediaPlayer`.
- Probar inserción, consulta, actualización, eliminación y filtrado parametrizado en SQLite.
- Probar el dictado por voz y manejo de permisos con `AlertDialog.Builder`.
