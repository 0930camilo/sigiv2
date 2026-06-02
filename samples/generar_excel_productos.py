from pathlib import Path
import sys
import subprocess

try:
    from openpyxl import Workbook
except Exception:
    subprocess.check_call([sys.executable, "-m", "pip", "install", "openpyxl", "-q"])
    from openpyxl import Workbook

out = Path(__file__).resolve().parent / "productos-import-ejemplo.xlsx"
out.parent.mkdir(parents=True, exist_ok=True)

wb = Workbook()
ws = wb.active
ws.title = "Productos"

ws.append(["nombre", "descripcion", "cantidad", "precioCompra", "precio", "estado", "proveedorId", "categoriaId"])
ws.append(["Lapicero", "Boligrafo azul", 100, 0.50, 1.00, "Activo", 2, 1])
ws.append(["Cuaderno", "Cuaderno cuadriculado", 50, 2.30, 3.50, "Activo", "", 1])
ws.append(["Borrador", "Borrador blanco", 80, 0.40, 0.80, "Inactivo", 3, 2])

wb.save(out)
print(out)

