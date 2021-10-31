module org.seasar.doma.boot.core {
    exports org.seasar.doma.boot;
    exports org.seasar.doma.boot.event;
    exports org.seasar.doma.boot.event.annotation;

    requires org.seasar.doma.core;
    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.data.commons;
    requires spring.jdbc;
    requires spring.tx;

    opens org.seasar.doma.boot to spring.core;
    opens org.seasar.doma.boot.event to spring.core;
}
