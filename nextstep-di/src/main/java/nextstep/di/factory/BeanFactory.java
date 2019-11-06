package nextstep.di.factory;

import com.google.common.collect.Maps;
import nextstep.annotation.Bean;
import nextstep.annotation.Configuration;
import nextstep.exception.BeanFactoryException;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanFactory.class);

    private Set<Class<?>> preInstantiatedBeans;

    private Map<Class<?>, Object> beans = Maps.newHashMap();

    public BeanFactory(Set<Class<?>> preInstantiatedBeans) {
        this.preInstantiatedBeans = preInstantiatedBeans;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> requiredType) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(requiredType, beans.keySet());
        return (T) beans.get(concreteClass);
    }

    public void initialize() {
        preInstantiatedBeans.forEach(this::createBean);
        preInstantiatedBeans.forEach(x -> {
            if (x.isAnnotationPresent(Configuration.class)) {
                Method[] methods = x.getMethods();
                Stream.of(methods)
                        .filter(method -> method.isAnnotationPresent(Bean.class))
                        .map(method -> {
                            try {
                                Object[] params = Arrays.stream(method.getParameterTypes())
                                        .map(param -> getBean(param))
                                        .toArray();
                                return method.invoke(getBean(x), params);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                                throw new RuntimeException();
                            }
                        })
                        .forEach(y -> beans.put(y.getClass(), y));
            }
        });
    }

    private Object createBean(final Class<?> preInstantiatedBean) {
        Class<?> concreteClass = BeanFactoryUtils.findConcreteClass(preInstantiatedBean, preInstantiatedBeans);
        if (beans.containsKey(concreteClass)) {
            return beans.get(concreteClass);
        }
        Object bean = createInstance(concreteClass);
        beans.put(concreteClass, bean);
        return bean;
    }

    private Object createInstance(final Class<?> preInstantiatedBean) {
        try {
            Constructor<?> constructor = getBeanConstructor(preInstantiatedBean);
            List<Object> parameters = createParameters(constructor.getParameterTypes());

            return constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage());
            throw new BeanFactoryException(e);
        }
    }

    private Constructor<?> getBeanConstructor(final Class<?> preInstantiatedBean) {
        Optional<Constructor<?>> injectedConstructor = BeanFactoryUtils.getInjectedConstructor(preInstantiatedBean);
        return injectedConstructor.orElseGet(() -> getConstructor(preInstantiatedBean));
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage());
            throw new BeanFactoryException(e);
        }
    }

    private List<Object> createParameters(final Class<?>[] parameterTypes) {
        return Stream.of(parameterTypes)
                .map(this::createBean)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public Set<Method> findMethodsByAnnotation(Class<? extends Annotation> methodAnnotation, Class<? extends Annotation> classAnnotation) {
        return beans.keySet().stream()
                .filter(clazz -> clazz.isAnnotationPresent(classAnnotation))
                .map(clazz -> ReflectionUtils.getAllMethods(clazz, ReflectionUtils.withAnnotation(methodAnnotation)))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
