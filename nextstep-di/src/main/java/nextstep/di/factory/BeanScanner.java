package nextstep.di.factory;

import nextstep.annotation.ComponentScan;
import nextstep.annotation.Configuration;
import nextstep.stereotype.Controller;
import nextstep.stereotype.Repository;
import nextstep.stereotype.Service;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanScanner {
    private static final Class[] COMPONENT_ANNOTATIONS = {Controller.class, Repository.class, Service.class, Configuration.class};
    private final Set<Class<?>> clazz;
    private final Object[] basePackages;

    public BeanScanner(Object... basePackages) {
        this.basePackages = getBasePackages(basePackages);
        this.clazz = getTypesAnnotatedWith(COMPONENT_ANNOTATIONS);
    }

    private Object[] getBasePackages(Object[] basePackage) {
        if (basePackage == null || basePackage.length == 0) {
            Reflections reflections = new Reflections("");
            basePackage = reflections.getTypesAnnotatedWith(Configuration.class)
                    .stream()
                    .filter(clazz -> clazz.isAnnotationPresent(ComponentScan.class))
                    .map(clazz -> clazz.getAnnotation(ComponentScan.class))
                    .map(ComponentScan::basePackages)
                    .toArray();
        }
        return basePackage;
    }

    public Set<Class<?>> getBeans() {
        return clazz;
    }

    @SuppressWarnings("uncheked")
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Reflections reflections = new Reflections(basePackages);
        return Stream.of(annotations)
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
