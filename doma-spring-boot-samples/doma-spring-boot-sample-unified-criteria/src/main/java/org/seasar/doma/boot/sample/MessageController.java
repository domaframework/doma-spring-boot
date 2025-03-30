package org.seasar.doma.boot.sample;

import java.util.List;

import org.seasar.doma.boot.UnifiedCriteriaPageable;
import org.seasar.doma.jdbc.criteria.QueryDsl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MessageController {

	private final QueryDsl queryDsl;

	public MessageController(QueryDsl queryDsl) {
		this.queryDsl = queryDsl;
	}

	@GetMapping
	List<Message> list(@PageableDefault(page = 0, size = 3, sort = "id,asc") Pageable pageable) {
		var m = new Message_();
		var p = UnifiedCriteriaPageable.from(pageable, m);
		return queryDsl.from(m)
				.offset(p.offset())
				.limit(p.limit())
				.orderBy(p.orderBy())
				.fetch();
	}

	@PostMapping("add")
	Message add(@RequestBody Message message) {
		var m = new Message_();
		var result = queryDsl.insert(m).single(message).execute();
		return result.getEntity();
	}
}
