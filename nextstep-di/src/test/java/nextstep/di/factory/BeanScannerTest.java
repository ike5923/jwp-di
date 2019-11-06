package nextstep.di.factory;

import nextstep.di.factory.example.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BeanScannerTest {

    @Test
    @DisplayName("생성자에 basePackage 입력 해줬을 경우 스캔 확인")
    void scan_Test1() {
        BeanScanner beanScanner = new BeanScanner("nextstep.di.factory.example");
        Set<Class<?>> actual = Set.of(QnaController.class, JdbcUserRepository.class, JdbcQuestionRepository.class, MyQnaServiceImpl.class);
        Set<Class<?>> expected = beanScanner.getBeans();

        assertThat(expected).containsAll(actual);
    }

    @Test
    @DisplayName("@ComponentScan 해줬을 경우 스캔 확인")
    void scan_Test2() {
        BeanScanner beanScanner = new BeanScanner();
        Set<Class<?>> actual = Set.of(QnaController.class, JdbcUserRepository.class, JdbcQuestionRepository.class, MyQnaServiceImpl.class, IntegrationConfig.class, ExampleConfig.class);
        Set<Class<?>> expected = beanScanner.getBeans();

        assertThat(expected).containsAll(actual);
    }
}