# Compila e abre o Gerenciador de Tarefas pelo terminal (sem IntelliJ).
# Uso (na raiz do repositório):  powershell -ExecutionPolicy Bypass -File executar.ps1

$ErrorActionPreference = 'Stop'
$projeto = Join-Path $PSScriptRoot 'GerenciadorTelas'
$saida = Join-Path $projeto 'out'

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'Java (JDK) não encontrado. Instale o JDK 17 ou superior e abra o terminal de novo.'
}

$driver = Get-ChildItem (Join-Path $projeto 'lib\postgresql-*.jar') | Select-Object -First 1 -ExpandProperty FullName
if (-not $driver) { throw 'Driver do PostgreSQL não encontrado em GerenciadorTelas\lib.' }

# Terminal aberto antes do setup-banco.ps1 não enxerga a variável; busca direto na configuração do Windows.
if (-not $env:DB_PASSWORD) {
    $env:DB_PASSWORD = [Environment]::GetEnvironmentVariable('DB_PASSWORD', 'User')
}
if (-not $env:DB_PASSWORD) {
    throw 'Banco não configurado. Rode primeiro: powershell -ExecutionPolicy Bypass -File banco\setup-banco.ps1'
}

$servico = Get-Service 'postgresql*' -ErrorAction SilentlyContinue | Select-Object -First 1
if ($servico -and $servico.Status -ne 'Running') {
    Write-Warning "O serviço $($servico.Name) está parado. Inicie-o em services.msc (ou reinicie o PC)."
}

Write-Host 'Compilando...' -ForegroundColor Cyan
if (Test-Path $saida) { Remove-Item -Recurse -Force $saida }
$fontes = (Get-ChildItem -Recurse (Join-Path $projeto 'src') -Filter *.java).FullName
& javac -encoding UTF-8 -d $saida -cp $driver $fontes
if ($LASTEXITCODE -ne 0) { throw 'Erro de compilação (veja as mensagens acima).' }

# Imagens e outros arquivos que não são .java precisam ir junto com as classes (o IntelliJ faz isso sozinho).
$src = Join-Path $projeto 'src'
Get-ChildItem -Recurse $src -File | Where-Object { $_.Extension -ne '.java' } | ForEach-Object {
    $destino = Join-Path $saida $_.FullName.Substring($src.Length + 1)
    New-Item -ItemType Directory -Force (Split-Path $destino) | Out-Null
    Copy-Item $_.FullName $destino
}

Write-Host 'Abrindo o Gerenciador de Tarefas...' -ForegroundColor Green
& java -cp "$saida;$driver" Main
