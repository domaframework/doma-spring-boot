package org.seasar.doma.boot.sample.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier("secondary")
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Secondary {
}
