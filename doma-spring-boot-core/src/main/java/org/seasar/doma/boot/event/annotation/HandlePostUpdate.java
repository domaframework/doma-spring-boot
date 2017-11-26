package org.seasar.doma.boot.event.annotation;

import java.lang.annotation.*;
import org.seasar.doma.jdbc.entity.PostUpdateContext;

@HandleDomaEvent(contextClass = PostUpdateContext.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HandlePostUpdate {
}
