INSERT INTO `weapons` (`damage`, `name`, `price`) VALUES (0, "hand", 0);
INSERT INTO `weapons` (`damage`, `name`, `price`) VALUES (50, "knife", 100);
INSERT INTO `weapons` (`damage`, `name`, `price`) VALUES (100, "gun", 200);
INSERT INTO oauth_client_details
(
	client_id,
	client_secret,
	resource_ids,
	scope,
	authorized_grant_types,
	web_server_redirect_uri,
	authorities,
	access_token_validity,
	refresh_token_validity,
	additional_information,
	autoapprove
)
VALUES
(
	'codingsquid',
	'1234',
	null,
	'read, write',
	'authorization_code,password, implicit, refresh_token',
	null,
	'ROLE_YOUR_CLIENT',
	36000,
	2592000,
	null,
	null
);