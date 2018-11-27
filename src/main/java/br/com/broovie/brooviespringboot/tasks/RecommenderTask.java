package br.com.broovie.brooviespringboot.tasks;

import br.com.broovie.brooviespringboot.models.Avaliacao;
import br.com.broovie.brooviespringboot.models.Recomendacao;
import br.com.broovie.brooviespringboot.models.Usuario;
import br.com.broovie.brooviespringboot.repositories.AvaliacaoRepository;
import br.com.broovie.brooviespringboot.repositories.FilmeRepository;
import br.com.broovie.brooviespringboot.repositories.RecomendacaoRepository;
import br.com.broovie.brooviespringboot.repositories.UsuarioRepository;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.jdbc.ConnectionPoolDataSource;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

@Component
public class RecommenderTask {
    final Logger logger = LoggerFactory.getLogger(RecommenderTask.class);

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    FilmeRepository filmeRepository;

    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @Autowired
    RecomendacaoRepository recomendacaoRepository;

    @Autowired
    Environment environment;

    //@Scheduled(cron = "0 0 * * * *")
    //@Scheduled(initialDelay = 1000, fixedRate = Long.MAX_VALUE)
    public void gerarRecomendacaoTipoUserSimilarity() {
        try {
            List<Long> codigos = usuarioRepository.codigos("Controle");
            DataModel model = new PostgreSQLJDBCDataModel(dataSource(), "avaliacao", "usuario_code", "filme_code", "nota", "data_cadastro");
            RecommenderBuilder recommenderBuilder = model1 -> {
                UserSimilarity similarity = new LogLikelihoodSimilarity(model1);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood((int) (usuarioRepository.countAllByExcluidoIsFalse() * 0.0333), similarity, model1);
                return new GenericUserBasedRecommender(model1, neighborhood, similarity);
//                ALSWRFactorizer alswrFactorizer = new ALSWRFactorizer(model, 2, 0.065, 2);
//                return new SVDRecommender(model, alswrFactorizer);
            };
            Recommender recommender = recommenderBuilder.buildRecommender(model);
            codigos.forEach(codigo -> {
                logger.info(String.format("Processando usuario código %d", codigo));
                Usuario usuario = usuarioRepository.findById(codigo).get();
                if (!avaliacaoRepository.avaliacaoPorUsuario(usuario.getCode()).isEmpty()) {
                    logger.info(String.format("Usuario %d possui avaliacoes", codigo));
                    try {
                        logger.info(String.format("Recomendando para o usuario %d", codigo));
                        List<RecommendedItem> recommendations = recommender.recommend(codigo, 10);
                        logger.info(String.format("Gerado %d recomendacoes para o usuario %d", recommendations.size(), codigo));
                        Set<Recomendacao> recomendacoes = recomendacaoRepository.recomendacoesPorUsuarioTipo(usuario.getCode(), Recomendacao.TipoRecomendacao.USER_SIMILARITY);
                        recommendations.parallelStream().forEach(recommendation -> {
                            recomendacoes.add(Recomendacao.builder()
                                    .usuario(usuario)
                                    .filme(filmeRepository.findById(recommendation.getItemID()).get())
                                    .notaCalculada(Avaliacao.Nota.values()[(int) recommendation.getValue()])
                                    .tipoRecomendacao(Recomendacao.TipoRecomendacao.USER_SIMILARITY)
                                    .build());
                        });
                        recomendacaoRepository.saveAll(recomendacoes);
                    } catch (Exception e) {
                        logger.error(String.format("Falha ao recomendar para o usuário código %d", codigo));
                        throw new RuntimeException(e);
                    }
                } else
                    logger.info(String.format("Usuario %d nao possui avaliacao", codigo));
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(environment.getProperty("spring.datasource.url"));
        driverManagerDataSource.setUsername(environment.getProperty("spring.datasource.username"));
        driverManagerDataSource.setPassword(environment.getProperty("spring.datasource.password"));
        driverManagerDataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
        return new ConnectionPoolDataSource(driverManagerDataSource);
    }
}
