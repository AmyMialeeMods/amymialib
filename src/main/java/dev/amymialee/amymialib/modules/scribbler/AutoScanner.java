package dev.amymialee.amymialib.modules.scribbler;

import dev.amymialee.amymialib.modules.iinterfaces.AMSilencer;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

@ApiStatus.Internal @SuppressWarnings("unchecked")
public class AutoScanner {
    private static final HashMap<String, Reflections> reflected = new HashMap<>();

    public static Reflections findAnnotations(String packageName) {
        return reflected.computeIfAbsent(packageName, p -> new Reflections(((AMSilencer<ConfigurationBuilder>) new ConfigurationBuilder()).am$silence().forPackage(p).filterInputsBy(new FilterBuilder().includePackage(p)).setScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated)));
    }

    public static <FI> List<FI> findFunctionalInterfaces(Logger errorLogger, Class<? extends Annotation> annotation, String packageName, Class<FI> functionalInterface) {
        var result = getFunctionalData(annotation, packageName, functionalInterface);
        var found = new ArrayList<FI>();
        result.set.forEach(m -> {
            var methodParams = m.getParameterTypes();
            if (result.functionalParams().length != methodParams.length) return;
            for (var i = 0; i < methodParams.length; i++) if (!methodParams[i].isAssignableFrom(result.functionalParams()[i])) return;
            var error = "";
            if (!Modifier.isStatic(m.getModifiers())) error += "it is not static";
            var flags = m.accessFlags();
            if (flags.contains(AccessFlag.PROTECTED)) {
                if (!error.isEmpty()) error += ", and ";
                error += "it is protected";
            } else if (flags.contains(AccessFlag.PRIVATE)) {
                if (!error.isEmpty()) error += ", and ";
                error += "it is private";
            }
            if (!error.isEmpty()) {
                error = "Unable to access method %s in %s as %s.".formatted(m.getName(), m.getDeclaringClass().getName(), error);
                errorLogger.error(error);
                throw new RuntimeException(error, null, true, false) {};
            }
            try {
                m.setAccessible(true);
                found.add((FI) LambdaMetafactory.metafactory(
                        result.lookup(), result.functionalMethod().getName(), result.factory(), result.functionalMethodType(),
                        MethodHandles.privateLookupIn(m.getDeclaringClass(), result.lookup()).unreflect(m), result.functionalMethodType()
                ).getTarget().invoke());
            } catch (LambdaConversionException | IllegalAccessException _) {
            } catch (Throwable t) { throw new RuntimeException(t); }
        });
        return found;
    }

    public static <FI> boolean checkForFunctionalInterfaces(Class<? extends Annotation> annotation, String packageName, Class<FI> functionalInterface) {
        var result = getFunctionalData(annotation, packageName, functionalInterface);
        return result.set.stream().anyMatch(m -> {
            var methodParams = m.getParameterTypes();
            if (result.functionalParams.length != methodParams.length) return false;
            for (var i = 0; i < methodParams.length; i++) if (!methodParams[i].isAssignableFrom(result.functionalParams[i])) return false;
            var flags = m.accessFlags();
            if (!Modifier.isStatic(m.getModifiers()) || flags.contains(AccessFlag.PROTECTED) || flags.contains(AccessFlag.PRIVATE)) return false;
            try {
                m.setAccessible(true);
                functionalInterface.cast(LambdaMetafactory.metafactory(
                        result.lookup, result.functionalMethod.getName(), result.factory, result.functionalMethodType,
                        MethodHandles.privateLookupIn(m.getDeclaringClass(), result.lookup).unreflect(m), result.functionalMethodType
                ).getTarget().invoke());
                return true;
            } catch (Throwable _) {}
            return false;
        });
    }

    private static <FI> @NonNull FunctionalData getFunctionalData(Class<? extends Annotation> annotation, String packageName, Class<FI> functionalInterface) {
        var functionalMethod = Arrays.stream(functionalInterface.getMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers()) && m.getDeclaringClass() != Object.class)
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> Optional.ofNullable(list.size() == 1 ? list.getFirst() : null)))
                .orElseThrow(() -> new IllegalArgumentException(functionalInterface + " not a functional interface"));
        var lookup = MethodHandles.lookup();
        var factory = MethodType.methodType(functionalInterface);
        var functionalMethodType = MethodType.methodType(functionalMethod.getReturnType(), functionalMethod.getParameterTypes());
        var functionalParams = functionalMethod.getParameterTypes();
        return new FunctionalData(AutoScanner.findAnnotations(packageName).getMethodsAnnotatedWith(annotation), functionalMethod, lookup, factory, functionalMethodType, functionalParams);
    }

    private record FunctionalData(Set<Method> set, Method functionalMethod, MethodHandles.Lookup lookup, MethodType factory, MethodType functionalMethodType, Class<?>[] functionalParams) {}
}