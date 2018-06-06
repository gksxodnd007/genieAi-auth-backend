package com.finder.genie_ai.model.user;

import javax.persistence.Column;

public class OauthClientModel {

    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Column(name = "resource_ids", unique = true)
    private String resourceIds;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "scope")
    private String scope;

    @Column(name = "authorized_grant_types")
    private String authorizedGrandTypes;

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri;

    @Column(name = "authorities")
    private String authorities;

    @Column(name = "access_token_validity")
    private int accessTokenValidity;

    @Column(name = "refresh_token_validity")
    private int refreshTokenValidity;

    @Column(name = "additional_information", length = 4096)
    private String additionalInformation;

    @Column(name = "auto_approve")
    private String autoApprove;

}
