package seminars.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect // объявляет класс аспектом
@Component // делает аспект бином Spring
public class LoggingAspect {
/**
 * Измеряет время выполнения метода и выводит его в консоль.
 * @param joinPoint точка соединения - метод к которому применятся
 * @param logExecutionTime имя логирования
 * @return результат выполнения исходного метода
 * @throws Throwable если метод выбросил исключение
 */
    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - start;
        System.out.printf(logExecutionTime.name() + ": метод %s выполнен за %d мс%n",
                joinPoint.getSignature().toString(), elapsedTime);

        return result;
    }

}
