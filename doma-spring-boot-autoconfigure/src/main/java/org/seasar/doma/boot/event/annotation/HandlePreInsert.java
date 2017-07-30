package org.seasar.doma.boot.event.annotation;

import java.lang.annotation.*;
import org.seasar.doma.jdbc.entity.PreInsertContext;

@HandleDomaEvent(contextClass = PreInsertContext.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HandlePreInsert {
}
