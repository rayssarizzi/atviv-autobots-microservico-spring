# AutoBots — Atividade IV

API REST com login JWT e autorização por perfil. O banco H2 guarda os dados em memória durante a execução.

## Executar

Com Java 17, no PowerShell e na pasta extraída:

```powershell
$env:AUTOBOTS_ADMIN_PASSWORD = Read-Host 'Senha inicial (mínimo de 8 caracteres)'
java -jar automanager.jar
```

Envie `POST http://localhost:8080/api/auth/login` com `{"nomeUsuario":"admin","senha":"SUA_SENHA"}`. Use o campo `token` no cabeçalho `Authorization: Bearer TOKEN` nas demais requisições. O token expira em uma hora.

O administrador cadastra usuários em `/api/usuarios` com `nome`, `empresa: {"id": ID}` e `perfis`. Cadastre o login em `/api/credenciais` com `tipo: "USUARIO_SENHA"`, `usuario: {"id": ID}`, `nomeUsuario` e `senha`.

| Perfil | Permissões |
| --- | --- |
| `ADMINISTRADOR` | CRUD de todos os recursos. |
| `GERENTE` | CRUD de gerentes, vendedores, clientes, serviços, mercadorias e vendas. |
| `VENDEDOR` | CRUD de clientes, leitura de serviços e mercadorias, criação e leitura das próprias vendas. |
| `CLIENTE` | Leitura do próprio cadastro e das vendas em que é consumidor. |

Para testar e recompilar, execute `.\mvnw.cmd verify` na pasta `automanager`.

