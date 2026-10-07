# Cria o banco gerenciador_tarefas e roda os 4 scripts SQL.
# Uso (na raiz do repositório):  powershell -ExecutionPolicy Bypass -File banco\setup-banco.ps1

$ErrorActionPreference = 'Stop'
$raiz = $PSScriptRoot
$psql = Get-ChildItem 'C:\Program Files\PostgreSQL\*\bin\psql.exe' |
    Sort-Object { [int]$_.Directory.Parent.Name } -Descending |
    Select-Object -First 1 -ExpandProperty FullName
if (-not $psql) { throw 'psql.exe não encontrado em C:\Program Files\PostgreSQL.' }

function Ler-Senha([string]$mensagem) {
    $segura = Read-Host -AsSecureString $mensagem
    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($segura)
    try { return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
}

function Rodar-Sql([string]$usuario, [string]$banco, [string]$arquivo) {
    Write-Host "`n>> $arquivo (como $usuario)" -ForegroundColor Cyan
    & $psql -h localhost -U $usuario -d $banco -v ON_ERROR_STOP=1 -f (Join-Path $raiz $arquivo)
    if ($LASTEXITCODE -ne 0) { throw "Falha ao rodar $arquivo." }
}

$senhaPostgres = Ler-Senha 'Senha do usuario postgres (definida na instalacao)'
$senhaApp = Ler-Senha 'Nova senha para o usuario gerenciador_app (minimo 8 caracteres)'
$confirmacao = Ler-Senha 'Confirme a senha do gerenciador_app'
if ($senhaApp -ne $confirmacao) { throw 'As senhas do gerenciador_app não conferem.' }
if ($senhaApp.Length -lt 8) { throw 'A senha do gerenciador_app precisa ter pelo menos 8 caracteres.' }

try {
    $env:PGPASSWORD = $senhaPostgres
    $env:APP_DB_PASSWORD = $senhaApp
    Rodar-Sql 'postgres' 'postgres' 'sql\01_create_database.sql'

    $env:PGPASSWORD = $senhaApp
    Rodar-Sql 'gerenciador_app' 'gerenciador_tarefas' 'sql\02_schema.sql'
    Rodar-Sql 'gerenciador_app' 'gerenciador_tarefas' 'sql\03_seed.sql'
    Rodar-Sql 'gerenciador_app' 'gerenciador_tarefas' 'sql\04_foto_perfil.sql'

    Write-Host "`n>> Tabelas criadas:" -ForegroundColor Cyan
    & $psql -h localhost -U gerenciador_app -d gerenciador_tarefas -c '\dt'

    # Variável de usuário do Windows lida pela classe ConexaoBD.
    [Environment]::SetEnvironmentVariable('DB_PASSWORD', $senhaApp, 'User')
    Write-Host "`nPronto! DB_PASSWORD configurada. Reabra o VS Code/IntelliJ para enxergá-la." -ForegroundColor Green
}
finally {
    Remove-Item Env:PGPASSWORD, Env:APP_DB_PASSWORD -ErrorAction SilentlyContinue
}
