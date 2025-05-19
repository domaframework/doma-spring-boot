package org.seasar.doma.boot.sample;

import org.seasar.doma.*;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    public String text;
}
