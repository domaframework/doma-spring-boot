package org.seasar.doma.boot.event.annotation;

import java.lang.annotation.*;
import org.seasar.doma.jdbc.entity.PostInsertContext;

@HandleDomaEvent(contextClass = PostInsertContext.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HandlePostInsert {
}
