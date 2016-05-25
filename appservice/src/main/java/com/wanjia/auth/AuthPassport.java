package com.wanjia.auth;

import java.lang.annotation.*;

/**
 * Created by blake on 2016/5/25.
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthPassport {
    boolean validate() default true;
}
