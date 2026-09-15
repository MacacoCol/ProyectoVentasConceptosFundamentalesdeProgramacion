# Proyecto Ventas

Proyecto del módulo Conceptos Fundamentales de Programación - Politécnico Grancolombiano.

## Integrantes

- Julian David Gutierrez Forero
- Santiago Garcia Castañeda
- Sharol Ochoa

## Clases

- **GenerateInfoFiles**: genera los archivos de prueba (`productos.txt`, `vendedores.txt` y los archivos `ventas_*.txt`).
- **main**: lee esos archivos y genera `reporte_vendedores.csv` y `reporte_productos.csv`.

## Cómo ejecutar

1. Importar el proyecto en Eclipse (File > Import > Existing Projects into Workspace).
2. Ejecutar `GenerateInfoFiles` (Run As > Java Application).
3. Ejecutar `main`.

Los archivos se crean en la carpeta del proyecto (dar F5 en Eclipse para verlos).

## Extras

- Un vendedor puede tener más de un archivo de ventas.
- Se ignoran y se avisan las ventas con productos que no existen, cantidades negativas o líneas mal escritas.
