# Checklist de Pruebas API - Pagos y Caja

## Precondiciones
1. Tener usuario `ADMIN` o `CAMARERO` autenticado.
2. Tener al menos una `cuenta` abierta con `total > 0`.
3. Tener el JWT de login.

## Variables de entorno sugeridas (PowerShell)
```powershell
$BASE = "http://localhost:8081"
$TOKEN = "REEMPLAZAR_TOKEN"
$HEADERS = @{ Authorization = "Bearer $TOKEN"; "Content-Type" = "application/json" }
```

## 1. Apertura de caja
```powershell
Invoke-RestMethod -Method Post -Uri "$BASE/api/caja/apertura" -Headers $HEADERS -Body (@{
  montoInicial = 100
  usuarioApertura = "admin"
  observaciones = "Apertura turno manana"
} | ConvertTo-Json)
```
Validar:
1. `estado = "ABIERTA"`.
2. `montoInicial` correcto.
3. `fechaApertura` no nula.

## 2. No permitir segunda caja abierta
Repetir apertura anterior.
Validar:
1. Respuesta HTTP 4xx (regla de negocio).
2. Mensaje indicando que ya existe caja abierta.

## 3. Consultar caja abierta
```powershell
Invoke-RestMethod -Method Get -Uri "$BASE/api/caja/abierta" -Headers $HEADERS
```
Validar:
1. Devuelve la misma caja abierta.

## 4. Registrar pago parcial de cuenta
Reemplazar `ID_CUENTA` por una cuenta ABIERTA con saldo.
```powershell
$ID_CUENTA = 1
Invoke-RestMethod -Method Post -Uri "$BASE/api/cuentas/$ID_CUENTA/pagos" -Headers $HEADERS -Body (@{
  monto = 10
  metodo = "EFECTIVO"
  referencia = "CAJA-001"
  usuario = "admin"
} | ConvertTo-Json)
```
Validar:
1. Crea pago con `metodo` y `monto` correctos.
2. `totalPagadoCuenta` aumenta.
3. `saldoPendienteCuenta` disminuye.
4. La cuenta sigue `ABIERTA` si falta saldo.

## 5. Listar pagos de cuenta
```powershell
Invoke-RestMethod -Method Get -Uri "$BASE/api/cuentas/$ID_CUENTA/pagos" -Headers $HEADERS
```
Validar:
1. Se listan los pagos registrados para esa cuenta.

## 6. Registrar pago final y cierre automatico de cuenta
Registrar otro pago por el saldo pendiente exacto.
Validar:
1. `saldoPendienteCuenta = 0`.
2. Cuenta cambia a estado `PAGADA`.
3. Cuenta tiene `fechaCierre`.

## 7. No permitir sobrepago
Intentar registrar pago mayor al saldo.
Validar:
1. HTTP 4xx.
2. Mensaje de sobrepago.

## 8. Cierre de caja
```powershell
Invoke-RestMethod -Method Post -Uri "$BASE/api/caja/cierre" -Headers $HEADERS -Body (@{
  montoDeclaradoCierre = 110
  usuarioCierre = "admin"
  observaciones = "Cierre normal"
} | ConvertTo-Json)
```
Validar:
1. `estado = "CERRADA"`.
2. `montoEsperadoCierre = montoInicial + pagos`.
3. `diferencia = montoDeclaradoCierre - montoEsperadoCierre`.
4. `fechaCierre` no nula.

## 9. No permitir pagos sin caja abierta
Con caja cerrada, intentar registrar pago.
Validar:
1. HTTP 4xx.
2. Mensaje indicando que no hay caja abierta.

## 10. Listar historico de cajas
```powershell
Invoke-RestMethod -Method Get -Uri "$BASE/api/caja" -Headers $HEADERS
```
Validar:
1. Aparece la caja cerrada en el historico.

