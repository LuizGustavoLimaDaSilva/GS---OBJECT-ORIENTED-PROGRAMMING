# Territory Predictive Insights — CitizenTech

**Global Solution 2º/2026 — POO (Turma de Fevereiro)**
Plataforma de monitoramento territorial que lê sensores, prevê o que vai acontecer nos próximos dias e emite alertas explicados, com histórico persistido em banco de dados.

## Desenvolvido por

| Integrante | RM |
|---|---|
| João Pedro Tomas Dominguito | 562166 |
| Luiz Gustavo Lima da Silva | 563554 |
| Vicente Casellato Rodriguez | 563865 |

---

## Recorte escolhido: Clima local — chuva intensa e risco de alagamento

O território fictício é a **Bacia do Córrego Verde (Zona Leste, São Paulo)**, descrito apenas por região e pontos de monitoramento (nunca por pessoas). Há três pontos vulneráveis, cada um com um sensor:

| Ponto | Sensor | Grandeza |
|---|---|---|
| Praça da Várzea (ponto de alagamento) | `CHUVA-01` — `SensorChuva` | chuva (mm/h) |
| Ponte do Córrego Verde | `NIVEL-01` — `SensorNivelCorrego` | nível do córrego (cm) |
| Encosta do Bosque Alto | `SOLO-01` — `SensorUmidadeSolo` | umidade do solo (%) |

### Público beneficiado e decisão apoiada

- **Quem sofre o problema:** moradores das margens do córrego, comerciantes da várzea e famílias das encostas, que ficam expostos a alagamentos e deslizamentos quando chove forte.
- **Quem usa o sistema:** a associação de bairro / defesa civil local.
- **Decisão que o sistema apoia:** *"devemos avisar os moradores, limpar bueiros, acionar a Defesa Civil ou preparar a retirada de pessoas hoje?"* O alerta indica sensor, valor previsto, limite estourado e uma **sugestão de ação**, mas a decisão continua sendo humana.

---

## Arquitetura (pacotes)

```
src/main/java/
├── dominio/        ComponenteTerritorial (abstrata), PontoVulneravel, Sensor (interface),
│                   SensorSimulado, SensorChuva, SensorNivelCorrego, SensorUmidadeSolo,
│                   DadosTerritorio, Limiares, EstrategiaPrevisao (abstrata),
│                   PrevisaoMediaMovel, PrevisaoComparacao, Medicao, Alerta, NivelAlerta, ...
├── excecao/        DadosInvalidosException, FonteIndisponivelException
├── persistencia/   MedicaoDAO, AlertaDAO, ConexaoBanco  (JDBC + H2)
├── servico/        ServicoMonitoramento, FabricaPrevisao, ConfiguracaoSistema,
│                   RelatorioBuilder, ResultadoPrevisao
├── SistemaMonitoramento.java   (menu principal)
└── Tema.java                   (tema visual verde/natureza no console)
src/test/java/      MedicaoTest, PrevisaoMediaMovelTest, DadosTerritorioTest,
                    FabricaPrevisaoTest, ServicoMonitoramentoTest
```

### Como cada conceito de POO foi aplicado

1. **Domínio orientado a objetos**
   - *Abstração/herança:* `ComponenteTerritorial` (abstrata, com `verificarStatus()` abstrato) → `PontoVulneravel`.
   - *Interface e polimorfismo:* `Sensor` com 3 implementações (`SensorChuva`, `SensorNivelCorrego`, `SensorUmidadeSolo`).
   - *Encapsulamento:* `DadosTerritorio` tem atributos privados; limiares e população só mudam por setters que validam.
   - *Polimorfismo de previsão:* `EstrategiaPrevisao` (abstrata) → `PrevisaoMediaMovel` e `PrevisaoComparacao`, ambas sobrescrevendo `prever()` e chamando `super(...)` / `super.prever(...)`. A escolha da estratégia **não usa `if tipo ==` nem `instanceof`**: o menu percorre `TipoPrevisao.values()` e a fábrica devolve o objeto certo.
2. **Exceções customizadas**
   - `DadosInvalidosException`: medição fora da faixa, população ≤ 0, limiares incoerentes, janela inválida, histórico insuficiente. Lançada em construtores, setters e serviços.
   - `FonteIndisponivelException`: sensor fora do ar, banco inacessível, arquivo CSV não gravado.
   - O menu captura ambas com mensagem clara; **nenhum `catch` vazio**.
3. **Persistência com JDBC (caminho escolhido)**
   - `MedicaoDAO` e `AlertaDAO` usam **`PreparedStatement`** (nunca concatenação de String no SQL) e **try-with-resources**.
   - Toda medição válida e todo alerta emitido são gravados; o menu lista histórico por sensor e alertas.
   - Banco **H2 em arquivo** (`./data/territorio`): os dados **sobrevivem ao reiniciar**. Para correção sem criar arquivos: `-Dterritorio.db=mem` (H2 em memória).
4. **Padrões criacionais** (usamos três, cada um onde faz sentido)
   - **Factory — `FabricaPrevisao`:** centraliza a criação das estratégias. O menu e o serviço não conhecem as classes concretas nem espalham `if`; criar uma terceira estratégia é registrar uma linha na fábrica.
   - **Singleton — `ConfiguracaoSistema`:** os limiares e a janela de previsão precisam ser **uma só fonte da verdade**. Se o menu alterasse um limiar numa cópia e o serviço de alertas lesse outra, os alertas ficariam incoerentes com o que o usuário configurou. Construtor privado + `getInstance()`.
   - **Builder — `RelatorioBuilder`:** o status completo é montado por partes (cabeçalho, componentes, últimas leituras, alertas), cada uma opcional, sem um construtor com muitos parâmetros.
5. **Testes unitários (JUnit 5 + Mockito)** — 25 testes, todos no padrão **AAA**:
   - `assertThrows` validando `DadosInvalidosException` e `FonteIndisponivelException`.
   - `ServicoMonitoramentoTest` usa **Mockito** para mockar `MedicaoDAO` e `AlertaDAO`, testando a lógica de alerta **sem banco**.
   - Os cálculos são testados de verdade: se a média ou a tendência errar, o teste fica vermelho.

---

## Como rodar

**Requisitos:** JDK 17 ou superior e Maven 3.8+.

```bash
# 1) Rodar os testes
mvn test

# 2) Executar o sistema (menu no console)
mvn compile exec:java
```

Opções úteis:

```bash
# Banco em memória (não cria a pasta data/)
mvn compile exec:java -Dterritorio.db=mem

# Desativar cores ANSI (terminais antigos do Windows)
mvn compile exec:java -Dsem.cor=true
```

> No Windows, se os acentos aparecerem estranhos, use o Windows Terminal/PowerShell ou rode `chcp 65001` antes. O sistema já força saída em UTF-8.

**Primeiro uso sugerido:**
1. Menu **1** → informe `10` rodadas (cria 10 leituras por sensor no banco).
2. Menu **2** → veja as duas previsões por sensor e os alertas emitidos.
3. Menu **5** → reduza um limiar para forçar um alerta e rode **2** de novo.
4. Menu **3** e **4** → histórico e alertas persistidos. Feche, abra de novo e veja que continuam lá.

### Menu

| Opção | O que faz |
|---|---|
| 1 | Verifica o status dos pontos e lê/grava medições (simuladas, com validação) |
| 2 | Roda as duas estratégias sobre o histórico do banco e emite alertas |
| 3 | Lista o histórico de medições por sensor |
| 4 | Lista os alertas já emitidos |
| 5 | Gerencia dados do território (população, limiares, janela, restaurar padrões) |
| 6 | Status completo (relatório montado pelo `RelatorioBuilder`) |
| 7 | **Bônus:** exporta o histórico de um sensor para CSV |

### Limiares padrão

| Sensor | Atenção | Alerta | Crítico |
|---|---|---|---|
| Chuva (mm/h) | 25 | 50 | 80 |
| Nível do córrego (cm) | 150 | 250 | 350 |
| Umidade do solo (%) | 70 | 85 | 95 |

Os valores são simulados com passeio aleatório (cada leitura parte da anterior), em faixas realistas, e os sensores têm 3% de chance de falhar a cada leitura para exercitar a `FonteIndisponivelException`.

---

##  Bônus

**Exportar o histórico consultado em arquivo CSV** (menu opção 7 → `historico_<SENSOR>.csv`).

---

##  Reflexão crítica

**Quando o sistema deve se calar?**
Com menos de 5 leituras no banco, uma média ou uma tendência são praticamente um chute: uma única leitura atípica domina o resultado. Por isso `EstrategiaPrevisao.prever()` recusa o histórico curto e lança `DadosInvalidosException` explicando o motivo, em vez de emitir um número bonito e enganoso. Um falso alerta também tem custo: se o sistema "gritar" sem base, a comunidade aprende a ignorá-lo (efeito "pastor mentiroso"), e isso pesa mais justamente na hora em que o alerta for verdadeiro. Calar-se, nesse caso, também é uma forma de proteger a confiança.

**O que os dados não representam?**
Um sensor por ponto diz pouco sobre o território inteiro: a chuva pode ser forte na várzea e fraca na encosta a poucas centenas de metros. Os dados aqui são simulados, não capturam drenagem entupida, obras, lixo no córrego, o tipo de solo ou o estado das casas, e nada sabem sobre quem está em situação de maior vulnerabilidade (idosos, pessoas com mobilidade reduzida). Média móvel e comparação com o período anterior também não enxergam eventos raros e bruscos: uma pancada súbita só aparece depois de entrar no histórico.

**Por que o alerta apoia a decisão da pessoa, em vez de decidir por ela?**
O sistema sabe valores e limiares; a pessoa sabe o contexto: se a rua já está interditada, se há um evento no local, se a equipe consegue chegar a tempo. Por isso cada alerta explica **qual sensor, qual valor previsto, qual limite foi estourado e pela qual estratégia**, e traz só uma sugestão de ação. Quem decide, e responde pela decisão, é a associação de bairro ou a defesa civil, com a transparência necessária para confiar ou discordar do alerta.

---

### Uso Consciente de Inteligência Artificial

A Inteligência Artificial foi utilizada como ferramenta de apoio durante o desenvolvimento do projeto, principalmente na estruturação inicial da aplicação, na organização de algumas implementações e no auxílio à elaboração dos testes unitários.

As sugestões e os conteúdos gerados pela IA não foram incorporados sem análise. Todos os códigos, estruturas e implementações sugeridos passaram por revisão, análise e validação dos integrantes da equipe, que verificaram sua compatibilidade com os requisitos do desafio, realizaram ajustes quando necessários e buscaram compreender o funcionamento de cada parte desenvolvida.

A IA foi utilizada como um recurso complementar para otimizar o processo de desenvolvimento e apoiar a resolução de dúvidas, sem substituir a participação, as decisões técnicas ou a responsabilidade dos integrantes sobre o projeto. A implementação final, a validação e a compreensão das funcionalidades são de responsabilidade da equipe.

##  Divisão de tarefas

| Integrante | Responsabilidades |
|---|---|
| João Pedro Tomas Dominguito | Domínio (`ComponenteTerritorial`, `PontoVulneravel`, `Sensor` e implementações, `DadosTerritorio`, `Medicao`, `Alerta`), exceções customizadas e testes do domínio (`MedicaoTest`, `DadosTerritorioTest`) |
| Luiz Gustavo Lima da Silva | Estrutura do projeto (Maven, README), persistência JDBC/H2 (`ConexaoBanco`, `MedicaoDAO`, `AlertaDAO`), padrões Singleton (`ConfiguracaoSistema`) e Factory (`FabricaPrevisao`) e `FabricaPrevisaoTest` |
| Vicente Casellato Rodriguez | Estratégias de previsão, serviço de monitoramento e alertas, Builder (`RelatorioBuilder`), menu principal, tema visual e testes (`PrevisaoMediaMovelTest`, `ServicoMonitoramentoTest` com Mockito) |

Todos os integrantes fizeram commits no GitHub, cada um com a própria conta.
