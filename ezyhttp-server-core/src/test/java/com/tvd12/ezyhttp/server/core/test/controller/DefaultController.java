package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.server.core.annotation.Api;
import com.tvd12.ezyhttp.server.core.annotation.Authenticated;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoDelete;

@Controller
public class DefaultController {

	@DoDelete
	public void delete1() {}
	
	@DoDelete("/delete2")
	public void delete2() {}
	
	@Api
	@Authenticated
	@DoDelete(uri = "delete3", responseType = ContentTypes.APPLICATION_JSON)
	public void delete3() {}
}
