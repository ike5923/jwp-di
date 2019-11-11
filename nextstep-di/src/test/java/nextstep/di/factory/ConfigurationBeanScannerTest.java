package nextstep.di.factory;

import nextstep.di.factory.example.ExampleConfig;
import nextstep.di.factory.example.IntegrationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationBeanScannerTest {
    @Test
    @DisplayName("@ComponentScan 해줬을 경우 스캔 확인")
    void scan_Test() {
        BeanScanner beanScanner = new ConfigurationBeanScanner(ExampleConfig.class);
        Set<Class<?>> actual = Set.of(IntegrationConfig.class, ExampleConfig.class);
        Set<Class<?>> expected = beanScanner.getClassTypes();
        assertThat(expected).containsAll(actual);
    }
}