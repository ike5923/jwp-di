package nextstep.di.factory;

import nextstep.annotation.ComponentScan;
import nextstep.annotation.Configuration;

import java.util.Set;
import java.util.stream.Stream;

public class ConfigurationBeanScanner implements BeanScanner {

    private static final Class<Configuration> ANNOTATION = Configuration.class;

    private final Object[] basePackages;
    private final Set<Class<?>> clazz;

    public ConfigurationBeanScanner(Class<?>... configurations) {
        this.basePackages = getBasePackages(configurations);
        if (this.basePackages.length == 0) {
            throw new IllegalArgumentException("ComponentScan이 없습니다.");
        }

        this.clazz = getTypesAnnotatedWith(basePackages, ANNOTATION);
    }

    // TODO: 2019/11/11 가변인자 혹은 이름
    private Object[] getBasePackages(Class<?>... configurations) {
        return Stream.of(configurations)
                .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                .map(clazz -> clazz.getAnnotation(ComponentScan.class))
                .map(ComponentScan::basePackages)
                .toArray();
    }

    public Object[] getBasePackages() {
        return basePackages;
    }

    @Override
    public Set<Class<?>> getClassTypes() {
        return clazz;
    }
}