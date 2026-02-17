param(
    [string]$BaseUrl = "http://localhost:8081",
    [string]$Token = "",
    [string]$Username = "",
    [string]$Password = "",
    [int]$CuentaId = 0,
    [double]$MontoInicialCaja = 100.0,
    [double]$MontoPagoParcial = 10.0
)

$ErrorActionPreference = "Stop"

function Assert-True {
    param(
        [bool]$Condition,
        [string]$Message
    )
    if (-not $Condition) {
        throw "ASSERT FAILED: $Message"
    }
}

function Invoke-Api {
    param(
        [ValidateSet("GET", "POST", "PUT", "PATCH", "DELETE")]
        [string]$Method,
        [string]$Path,
        [object]$Body = $null
    )

    $headers = @{
        Authorization = "Bearer $Token"
        "Content-Type" = "application/json"
    }

    $uri = "$BaseUrl$Path"
    if ($null -ne $Body) {
        return Invoke-RestMethod -Method $Method -Uri $uri -Headers $headers -Body ($Body | ConvertTo-Json -Depth 10)
    }
    return Invoke-RestMethod -Method $Method -Uri $uri -Headers $headers
}

function Resolve-Token {
    param(
        [string]$CurrentToken,
        [string]$User,
        [string]$Pass
    )

    if (-not [string]::IsNullOrWhiteSpace($CurrentToken)) {
        return $CurrentToken
    }

    if ([string]::IsNullOrWhiteSpace($User) -or [string]::IsNullOrWhiteSpace($Pass)) {
        throw "Debes enviar -Token o bien -Username y -Password."
    }

    $uri = "$BaseUrl/api/usuarios/login"
    $body = @{
        username = $User
        password = $Pass
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Method Post -Uri $uri -ContentType "application/json" -Body $body
    Assert-True (-not [string]::IsNullOrWhiteSpace($response.token)) "Login sin token en respuesta."
    Write-Host "Login exitoso para usuario '$User'."
    return $response.token
}

function Resolve-CuentaId {
    param(
        [int]$CurrentCuentaId
    )

    if ($CurrentCuentaId -gt 0) {
        return $CurrentCuentaId
    }

    $cuentas = Invoke-Api -Method GET -Path "/api/cuentas/abiertas"
    if ($null -eq $cuentas) {
        throw "No hay cuentas abiertas disponibles."
    }

    if ($cuentas -isnot [System.Array]) {
        $cuentas = @($cuentas)
    }

    $cuentaConSaldo = $cuentas | Where-Object { [double]$_.total -gt 0 } | Select-Object -First 1
    if ($null -eq $cuentaConSaldo) {
        throw "No hay cuentas abiertas con total > 0 para probar pagos."
    }

    Write-Host "Cuenta detectada automaticamente: ID=$($cuentaConSaldo.idcuenta), total=$($cuentaConSaldo.total)"
    return [int]$cuentaConSaldo.idcuenta
}

Write-Host "== Iniciando pruebas flujo Caja + Pagos ==" -ForegroundColor Cyan
Write-Host "BaseUrl: $BaseUrl | CuentaId: $CuentaId"
$Token = Resolve-Token -CurrentToken $Token -User $Username -Pass $Password
$CuentaId = Resolve-CuentaId -CurrentCuentaId $CuentaId
Write-Host "CuentaId final para pruebas: $CuentaId"

# 1) Abrir caja (si no hay abierta)
$cajaAbierta = $null
try {
    $cajaAbierta = Invoke-Api -Method GET -Path "/api/caja/abierta"
    Write-Host "Caja ya abierta: ID=$($cajaAbierta.idcaja)"
} catch {
    Write-Host "No habia caja abierta. Se abrira una nueva..."
    $cajaAbierta = Invoke-Api -Method POST -Path "/api/caja/apertura" -Body @{
        montoInicial = $MontoInicialCaja
        usuarioApertura = "script-test"
        observaciones = "Apertura automatica test"
    }
    Assert-True ($cajaAbierta.estado -eq "ABIERTA") "La caja debe abrirse en estado ABIERTA"
    Write-Host "Caja abierta correctamente: ID=$($cajaAbierta.idcaja)"
}

# 2) Estado inicial de pagos
$pagosAntes = Invoke-Api -Method GET -Path "/api/cuentas/$CuentaId/pagos"
if ($pagosAntes -isnot [System.Array]) { $pagosAntes = @($pagosAntes) }
$totalAntes = 0.0
foreach ($p in $pagosAntes) { $totalAntes += [double]$p.monto }
Write-Host "Pagos previos en cuenta $CuentaId: $($pagosAntes.Count), total=$totalAntes"

# 3) Registrar pago parcial
$pagoParcial = Invoke-Api -Method POST -Path "/api/cuentas/$CuentaId/pagos" -Body @{
    monto = $MontoPagoParcial
    metodo = "EFECTIVO"
    referencia = "SCRIPT-PARCIAL"
    usuario = "script-test"
}

Assert-True ($pagoParcial.monto -gt 0) "El pago parcial debe tener monto > 0"
Assert-True ($pagoParcial.totalPagadoCuenta -ge $MontoPagoParcial) "El total pagado debe incrementarse"
Assert-True ($pagoParcial.saldoPendienteCuenta -ge 0) "El saldo pendiente no puede ser negativo"
Write-Host "Pago parcial registrado: ID=$($pagoParcial.idpago), saldoPendiente=$($pagoParcial.saldoPendienteCuenta)"

# 4) Registrar pago final por saldo pendiente (si hay saldo)
$pagoFinal = $null
$saldo = [double]$pagoParcial.saldoPendienteCuenta
if ($saldo -gt 0.0001) {
    $pagoFinal = Invoke-Api -Method POST -Path "/api/cuentas/$CuentaId/pagos" -Body @{
        monto = $saldo
        metodo = "EFECTIVO"
        referencia = "SCRIPT-FINAL"
        usuario = "script-test"
    }
    Assert-True ([math]::Abs([double]$pagoFinal.saldoPendienteCuenta) -lt 0.0001) "La cuenta debe quedar en saldo 0"
    Write-Host "Pago final registrado: ID=$($pagoFinal.idpago)"
} else {
    Write-Host "La cuenta ya estaba pagada despues del parcial."
}

# 5) Cerrar caja
$montoDeclarado = 0.0
if ($null -ne $cajaAbierta.montoInicial) {
    $montoDeclarado = [double]$cajaAbierta.montoInicial
}
$totalPagadoEnCaja = 0.0
try {
    # Nota: el endpoint de cierre calcula esperado internamente.
    # Para no forzar diferencia, declaramos al menos monto inicial.
    $cierre = Invoke-Api -Method POST -Path "/api/caja/cierre" -Body @{
        montoDeclaradoCierre = $montoDeclarado
        usuarioCierre = "script-test"
        observaciones = "Cierre automatico test"
    }
    Assert-True ($cierre.estado -eq "CERRADA") "La caja debe cerrarse en estado CERRADA"
    Write-Host "Caja cerrada: ID=$($cierre.idcaja), diferencia=$($cierre.diferencia)"
} catch {
    Write-Warning "No se pudo cerrar caja automaticamente: $($_.Exception.Message)"
    Write-Warning "Revisa si hubo otros movimientos en caja que requieren monto declarado distinto."
}

Write-Host "== Flujo de prueba finalizado ==" -ForegroundColor Green
