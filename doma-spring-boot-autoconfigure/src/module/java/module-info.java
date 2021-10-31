module org.seasar.doma.boot.autoconfigure {
    exports org.seasar.doma.boot.autoconfigure;

    requires org.seasar.doma.boot.core;
    requires org.seasar.doma.core;
    requires org.seasar.doma.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.core;
    requires spring.context;
    requires spring.data.commons;
    requires spring.jcl;
    requires spring.jdbc;
    requires spring.tx;

    opens org.seasar.doma.boot.autoconfigure to spring.core;
}
