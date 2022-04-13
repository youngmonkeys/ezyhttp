package com.tvd12.ezyhttp.core.constant;

public final class StatusCodes {

	public static final int OK 				= 200;
	public static final int CREATED			= 201;
	public static final int ACCEPTED		= 202;
	public static final int NO_CONTENT		= 204;
	public static final int MOVED_PERMANENTLY		= 301;
	public static final int MOVED_TEMPORARILY		= 302;
	public static final int BAD_REQUEST		= 400;
	public static final int UNAUTHORIZED	= 401;
	public static final int PAYMENT_REQUIRED		= 402;
	public static final int FORBIDDEN		= 403;
	public static final int NOT_FOUND 		= 404;
	public static final int METHOD_NOT_ALLOWED 		= 405;
	public static final int NOT_ACCEPTABLE	= 406;
	public static final int REQUEST_TIMEOUT	= 408;
	public static final int CONFLICT		= 409;
	public static final int GONE			= 410;
	public static final int LENGTH_REQUIRED = 411;
	public static final int UNSUPPORTED_MEDIA_TYPE	= 415;
	public static final int TOO_MANY_REQUESTS		= 429;
	public static final int INTERNAL_SERVER_ERROR	= 500;
	
	
	private StatusCodes() {}
	
}
