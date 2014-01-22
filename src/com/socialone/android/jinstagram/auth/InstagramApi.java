package com.socialone.android.jinstagram.auth;


import com.socialone.android.jinstagram.auth.exceptions.OAuthException;
import com.socialone.android.jinstagram.auth.model.Constants;
import com.socialone.android.jinstagram.auth.model.OAuthConfig;
import com.socialone.android.jinstagram.auth.model.Token;
import com.socialone.android.jinstagram.auth.oauth.InstagramService;
import com.socialone.android.jinstagram.http.Verbs;
import com.socialone.android.jinstagram.utils.Preconditions;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstagramApi {
	public String getAccessTokenEndpoint() {
		return Constants.ACCESS_TOKEN_ENDPOINT;
	}

	public Verbs getAccessTokenVerb() {
		return Verbs.POST;
	}

	public String getAuthorizationUrl(OAuthConfig config) {
		Preconditions.checkValidUrl(config.getCallback(),
                "Must provide a valid url as callback. Instagram does not support OOB");

		// Append scope if present
		if (config.hasScope()) {
			return String.format(Constants.SCOPED_AUTHORIZE_URL, config.getApiKey(),
					URLEncoder.encode(config.getCallback()), URLEncoder.encode(config.getScope()));
		}
		else {
			return String.format(Constants.AUTHORIZE_URL, config.getApiKey(), URLEncoder.encode(config.getCallback()));
		}
	}

	public AccessTokenExtractor getAccessTokenExtractor() {
		return new AccessTokenExtractor() {
			private Pattern accessTokenPattern = Pattern.compile(Constants.ACCESS_TOKEN_EXTRACTOR_REGEX);

			@Override
			public Token extract(String response) {
				Preconditions.checkEmptyString(response, "Cannot extract a token from a null or empty String");

				Matcher matcher = accessTokenPattern.matcher(response);

				if (matcher.find()) {
					return new Token(matcher.group(1), "", response);
				}
				else {
					throw new OAuthException("Cannot extract an acces token. Response was: " + response);
				}
			}
		};
	}

	public InstagramService createService(OAuthConfig config) {
		return new InstagramService(this, config);
	}
}
