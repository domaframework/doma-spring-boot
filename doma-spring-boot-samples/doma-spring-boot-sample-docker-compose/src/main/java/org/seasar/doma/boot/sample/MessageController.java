package org.seasar.doma.boot.sample;

import java.util.List;

import org.seasar.doma.boot.Pageables;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MessageController {

    private final MessageDao messageDao;

    public MessageController(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @GetMapping
    List<Message> list(@PageableDefault Pageable pageable) {
        return messageDao.selectAll(Pageables.toSelectOptions(pageable));
    }

    @GetMapping(params = "text")
    Message add(@RequestParam String text) {
        Message message = new Message();
        message.text = text;
        messageDao.insert(message);
        return message;
    }
}
