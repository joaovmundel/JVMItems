# JVMItems

Plugin Spigot/Bukkit inspirado no RPGItems para criar itens customizados
(nome e descrição coloridos, dano, armadura, e um sistema de **Powers**
configuráveis — flecha, bola de fogo, gelo, poção, lifesteal, dano em área,
raio, nocaute, comando, etc.), compatível a partir da **1.12.2** até as
versões mais recentes.

## Como funciona a compatibilidade 1.12 → atual

A API pública do Bukkit mudou bastante nesse intervalo (flattening de
`Material` na 1.13, API de atributos de item só a partir da 1.13,
`PersistentDataContainer` só a partir da 1.14). O plugin compila contra uma
API recente, mas isola todo código que depende de recursos mais novos em
classes separadas, escolhidas em runtime de acordo com a versão do servidor
(veja `com.jvmitems.compat`):

- **Identificação do item** (para saber que um `ItemStack` é um item do
  JVMItems): usa `PersistentDataContainer` a partir da 1.14; em 1.12/1.13 usa
  uma linha de lore composta só por códigos de cor (não aparece nenhum texto,
  só uma linha em branco a mais no tooltip — única limitação cosmética
  conhecida nessas duas versões).
- **Dano/Armadura (atributos do item)**: usa a API `ItemMeta#addAttributeModifier`
  a partir da 1.13; na 1.12 (que não tinha essa API) grava a tag NBT
  `AttributeModifiers` diretamente via reflection.

Isso significa que **um único jar** funciona em todas as versões suportadas,
sem precisar compilar builds separados.

## Build

Requer JDK 8+ e Maven.

```
mvn package
```

O jar final fica em `target/JVMItems-1.0.0.jar`. O `pom.xml` baixa o
`spigot-api` do repositório oficial do Spigot (`hub.spigotmc.org`), usado só
em tempo de compilação (`provided`) — nada dele é embutido no jar.

## Permissões

- `jvmitems.admin` — criar/editar/remover itens (padrão: `op`)
- `jvmitems.give` — dar itens a jogadores (padrão: `op`)
- `jvmitems.*` — todas as permissões acima

## Sistema de Powers (inspirado no RPGItems)

Cada item pode ter **vários Powers**. Um Power é a combinação de:

- **Tipo** — o que ele faz: `ARROW`, `FIREBALL`, `SNOWBALL` (gelo), `POTION`,
  `LIFESTEAL`, `DAMAGE_MULTIPLIER`, `KNOCKBACK`, `LIGHTNING`, `HEAL`,
  `FIREWORK`, `AOE_DAMAGE`, `COMMAND` (executa um comando — serve de "escape
  hatch" para integrar com outros plugins).
- **Trigger** — quando ele dispara: `RIGHT_CLICK`, `LEFT_CLICK`,
  `SNEAK_RIGHT_CLICK`, `SNEAK_LEFT_CLICK`, `HIT` (acertar alguém no corpo a
  corpo), `TAKE_DAMAGE` (o dono do item leva dano — funciona em qualquer peça
  equipada: mão, mão secundária ou armadura), `BLOCK_BREAK`,
  `PROJECTILE_HIT` (quando um projétil lançado por outro Power do mesmo item
  atinge algo — usado para aplicar poção em quem foi atingido pela flecha/gelo).
- **Cooldown** (em segundos), independente por Power.
- **Conditions** (opcionais, todas precisam passar): `permission`,
  `sneaking`/`notsneaking`, `healthbelow`/`healthabove` (percentual de vida
  0–1), `chance` (probabilidade 0–1).

Isso permite montar itens com múltiplos efeitos combináveis, do jeito que o
RPGItems faz com seus Powers/Triggers/Conditions — só que de forma mais
enxuta (sem GUI, tudo por comando).

## Comandos (`/jvmitems`, aliases `/jvmi`, `/jvi`)

O menu de ajuda (`/jvmi` ou `/jvmi help`) é paginado e clicável: cada comando
aparece com o subcomando em amarelo, os argumentos em azul e a descrição numa
linha própria em cinza. Argumentos longos são quebrados em linhas indentadas
(nada quebra sozinho no meio da palavra) e os botões `« Anterior` / `Próxima »`
trocam de página sem precisar rolar o chat. Passando o mouse em um comando
aparece a assinatura completa, exemplos, a permissão necessária e a lista de
triggers/condições válidas; clicando, o comando é preenchido no chat.

- `/jvmi help <página>` — pula direto para uma página
- `/jvmi help <busca>` — filtra por categoria (`itens`, `aparencia`,
  `atributos`, `powers`, `geral`) ou por qualquer texto do comando/descrição
  (ex.: `/jvmi help lifesteal`)

No console (que não suporta hover/clique) o mesmo menu é impresso em texto
puro, com as instruções de paginação por extenso. O menu só mostra os comandos
para os quais o sender tem permissão.

| Comando | Descrição |
|---|---|
| `help [página\|busca]` | Mostra o menu de ajuda paginado |
| `create <id> <material>` | Cria um novo item customizado |
| `delete <id>` | Remove um item |
| `list` | Lista os ids de itens criados |
| `info <id>` | Mostra os detalhes de um item |
| `give <id> [jogador] [qtd]` | Dá o item a um jogador |
| `setname <id> <nome>` | Define o título (aceita `&` para cores) |
| `addlore <id> <linha>` | Adiciona uma linha de descrição (aceita `&`) |
| `clearlore <id>` | Limpa toda a descrição |
| `setmaterial <id> <material>` | Troca o material base do item |
| `setdamage <id> <valor|none>` | Define o dano (atributo de ataque) |
| `setarmor <id> <valor|none>` | Define a armadura |
| `setarmortoughness <id> <valor|none>` | Define a resistência de armadura (toughness) |
| `setunbreakable <id> <true|false>` | Torna o item inquebrável |
| `enchant add|remove <id> <encantamento> [nível]` | Gerencia encantamentos |
| `power add arrow|fireball|snowball <id> <trigger> <cooldown> [qtd] [velocidade] [espalhamento]` | Adiciona um power de projétil |
| `power add potion <id> <trigger> <cooldown> <self|opponent> <poção> <duraçãoTicks> <amplificador>` | Aplica poção em si mesmo ou em quem foi atingido |
| `power add lifesteal <id> <trigger> <cooldown> <percentual 0-1>` | Cura com base no dano causado |
| `power add damagemultiplier <id> <trigger> <cooldown> <multiplicador>` | Multiplica o dano do golpe |
| `power add knockback <id> <trigger> <cooldown> <força>` | Empurra o oponente |
| `power add lightning <id> <trigger> <cooldown> <causaDano:true|false>` | Cai um raio no alvo |
| `power add heal <id> <trigger> <cooldown> <quantidade>` | Cura o usuário |
| `power add firework <id> <trigger> <cooldown> <potência 0-2>` | Lança um fogo de artifício |
| `power add aoedamage <id> <trigger> <cooldown> <raio> <dano>` | Dano em área ao redor do alvo |
| `power add command <id> <trigger> <cooldown> <console:true|false> <comando>` | Executa um comando (`%player%`/`%opponent%`) |
| `power setfireball <id> <índice> <incendiary> <explosive>` | Ajusta se a bola de fogo pega fogo / explode blocos |
| `power condition add <id> <índice> <tipo> [valor]` | Adiciona uma condição a um power |
| `power condition remove <id> <índice> <índiceCondição>` | Remove uma condição |
| `power list <id>` / `power remove <id> <índice>` | Lista/remove powers |
| `reload` | Recarrega `config.yml` e `items.yml` |

### Exemplo rápido

```
/jvmitems create flaming_sword DIAMOND_SWORD
/jvmitems setname flaming_sword &6&lEspada Flamejante
/jvmitems addlore flaming_sword &7Forjada nas chamas do Nether.
/jvmitems setdamage flaming_sword 12
/jvmitems power add potion flaming_sword hit 0 opponent POISON 100 1
/jvmitems power condition add flaming_sword 0 chance 0.5
/jvmitems power add lifesteal flaming_sword hit 0 0.25
/jvmitems power add fireball flaming_sword sneak_right_click 8
/jvmitems power setfireball flaming_sword 2 true false
/jvmitems give flaming_sword Steve
```

```
/jvmitems create ice_bow BOW
/jvmitems power add snowball ice_bow right_click 5 1 1.6 0
/jvmitems power add potion ice_bow projectile_hit 0 opponent SLOW 100 3
/jvmitems power add potion ice_bow projectile_hit 0 opponent WEAKNESS 100 2
/jvmitems give ice_bow Steve
```

O segundo exemplo cria um item que atira um projétil de "gelo" (snowball) e,
ao acertar o inimigo, aplica Lentidão + Fraqueza por 5 segundos — o efeito de
"stun" pedido, já que o Minecraft vanilla não tem um status de stun nativo.

## Proteções contra griefing

Por padrão (`anti-grief: true` no `config.yml`) nada que um Power lança pode
alterar o mundo. Tudo que o plugin cria (flecha, bola de fogo, bola de neve,
raio) é marcado com a metadata do plugin, e o `AntiGriefListener` barra:

- **quebrar blocos** — a explosão da bola de fogo continua machucando quem
  está perto, mas a lista de blocos da explosão é esvaziada;
- **começar fogo** — tanto o fogo da bola de fogo (`incendiary`) quanto o que
  o raio normalmente deixa no chão;
- **ativar blocos** — placa de pressão e tripwire (via `EntityInteractEvent`)
  e botão de madeira / target block / campainha (cancelando a ação do
  impacto);
- **destruir decoração** — quadro, moldura de item, barco, carrinho e cristal
  do end (o dano em jogadores e mobs continua normal).

As flechas lançadas por um Power também **não podem ser recolhidas do chão**
(`setPickupStatus(DISALLOWED)`, aplicado via reflection porque o tipo do
parâmetro mudou de lugar na 1.14) e **somem sozinhas**: por padrão são
apagadas logo depois de acertar algo e, se não acertarem nada, depois de 20
segundos. Isso evita tanto o dupe de flechas quanto o acúmulo de projéteis no
mundo.

```yml
anti-grief: true

projectiles:
  allow-pickup: false      # true devolve o comportamento normal da flecha
  remove-on-hit: true      # apaga o projétil assim que ele acerta algo
  lifetime-seconds: 20     # 0 = sem limite (deixa o Minecraft cuidar)
```

Os powers `AOE_DAMAGE`, `KNOCKBACK`, `LIFESTEAL`, `POTION`, `HEAL` e
`FIREWORK` nunca tocam em blocos. O `COMMAND` é a única exceção: ele executa
o comando que o admin configurou, então o que ele faz depende do comando —
por isso `jvmitems.admin` só deve ir para quem é confiável.

## Onde os itens ficam salvos

`plugins/JVMItems/items.yml` — pode ser editado manualmente (o plugin lê o
arquivo inteiro de novo com `/jvmitems reload`).

## Limitações conhecidas

- Em 1.12/1.13 o item customizado tem uma linha a mais (em branco) na
  descrição, usada para guardar o id do item de forma invisível.
- Não há GUI/menu — toda a configuração é por comando (mantém o plugin leve
  e sem dependências externas).
- `armor`/`armor-toughness` só fazem efeito se o jogador realmente vestir o
  item num slot de armadura; `attack-damage` só conta enquanto o item está
  na mão.
- Os tooltips padrão do Minecraft (atributos, "unbreakable", lista de
  encantamentos) ficam escondidos por padrão — quem define o que aparece é
  a descrição (`lore`) do item.
- O índice de um power (usado em `power remove`/`power condition`/`power
  setfireball`) é a posição dele na lista (`power list <id>`); remover um
  power desloca os índices dos powers seguintes.
- O power `COMMAND` executa qualquer comando como console ou como o
  jogador — só dê `jvmitems.admin` a quem for realmente confiável. É o único
  power que o `anti-grief` não consegue conter.
- A ativação de botão de madeira / target block / campainha por impacto de
  flecha só é bloqueada nas versões em que o Bukkit permite cancelar o
  `ProjectileHitEvent` (o plugin testa isso em runtime). Nas versões antigas
  os outros vetores — quebra de bloco, fogo, placa de pressão, tripwire,
  quadro/moldura — continuam bloqueados.
- Cooldowns ficam só em memória (zeram ao reiniciar o servidor).
