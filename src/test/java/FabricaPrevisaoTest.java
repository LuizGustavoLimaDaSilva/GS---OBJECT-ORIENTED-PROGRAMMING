// Global Solution 2º/2026 - POO - Territory Predictive Insights (CitizenTech)
// Desenvolvido por: João Pedro Tomas Dominguito (RM 562166), Luiz Gustavo Lima da Silva (RM 563554)
// e Vicente Casellato Rodriguez (RM 563865).
import static org.junit.jupiter.api.Assertions.*;

import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import dominio.TipoPrevisao;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import servico.ConfiguracaoSistema;
import servico.FabricaPrevisao;

class FabricaPrevisaoTest {

    @Test
    void criaMediaMovelPeloTipo() {
        assertInstanceOf(PrevisaoMediaMovel.class, FabricaPrevisao.criar(TipoPrevisao.MEDIA_MOVEL, 3));
    }

    @Test
    void criaComparacaoPeloTipo() {
        assertInstanceOf(PrevisaoComparacao.class, FabricaPrevisao.criar(TipoPrevisao.COMPARACAO, 3));
    }

    @Test
    void janelaInvalidaLancaDadosInvalidos() {
        assertThrows(DadosInvalidosException.class, () -> FabricaPrevisao.criar(TipoPrevisao.MEDIA_MOVEL, 1));
    }

    @Test
    void configuracaoSistemaEhSingleton() {
        assertSame(ConfiguracaoSistema.getInstance(), ConfiguracaoSistema.getInstance());
    }
}
