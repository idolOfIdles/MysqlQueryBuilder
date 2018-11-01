package safayat.orm.annotation;

/**
 * Created by safayat on 10/25/18.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by safayat on 10/25/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //can use in method only.

public @interface OneToMany {
    String outer() default "";
    String inner() default "";
    String name() default "";
    Class type();
}