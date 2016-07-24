/*
 * Copyright (C) 2004-2016 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.boot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DomaBootSampleSimpleApplication {

	@Autowired
	MessageDao messageDao;

	@RequestMapping("/")
	List<Message> list(@PageableDefault Pageable pageable) {
		return messageDao.selectAll(Pageables.toSelectOptions(pageable));
	}

	@RequestMapping(value = "/", params = "text")
	Message add(@RequestParam String text) {
		Message message = new Message();
		message.text = text;
		messageDao.insert(message);
		return message;
	}

	public static void main(String[] args) {
		SpringApplication.run(DomaBootSampleSimpleApplication.class, args);
	}
}
