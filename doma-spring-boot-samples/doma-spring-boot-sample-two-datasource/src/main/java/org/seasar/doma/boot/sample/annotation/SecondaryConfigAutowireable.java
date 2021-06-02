package org.seasar.doma.boot.sample.annotation;

import org.seasar.doma.AnnotateWith;
import org.seasar.doma.Annotation;
import org.seasar.doma.AnnotationTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@AnnotateWith(annotations = {
		@Annotation(target = AnnotationTarget.CLASS, type = Repository.class),
		@Annotation(target = AnnotationTarget.CONSTRUCTOR, type = Autowired.class),
		@Annotation(target = AnnotationTarget.CONSTRUCTOR_PARAMETER, type = Secondary.class),
})
public @interface SecondaryConfigAutowireable {

}
