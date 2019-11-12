package nextstep.di.factory;

import nextstep.di.factory.example.JdbcQuestionRepository;
import nextstep.di.factory.example.JdbcUserRepository;
import nextstep.di.factory.example.MyQnaServiceImpl;
import nextstep.di.factory.example.QnaController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ClasspathBeanScannerTest {

    @Test
    @DisplayName("@Controller, @Service, @Repository 스캔 확인")
    void scan_Test() {
        ClasspathBeanScanner classpathBeanScanner = new ClasspathBeanScanner("nextstep.di.factory.example");
        Set<Class<?>> actual = Set.of(QnaController.class, JdbcUserRepository.class, JdbcQuestionRepository.class, MyQnaServiceImpl.class);
        Set<Class<?>> expected = classpathBeanScanner.getClassTypes();

        assertThat(expected).containsAll(actual);
    }
}