// @flow

// fetch does not send the X-Requested-With header (https://github.com/github/fetch/issues/17),
// but we need the header to detect ajax request (AjaxAwareAuthenticationRedirectStrategy).
const fetchOptions = {
    credentials: 'same-origin',
    headers: {
        'X-Requested-With': 'XMLHttpRequest'
    }
};

function isAuthenticationRedirect(response) {
    if (response.status === 401) {
        const redirectTarget = response.headers.get('location');
        if (redirectTarget) {
            return true;
        }
    }
    return false;
}

function createRedirectUrl() {
    // TODO context path
    return '/smeagol/api/v1/authc?location=' + encodeURIComponent(window.location);
}

function redirect(redirectUrl: string) {
    window.location.href = redirectUrl;
}

class ApiClient {

    get(url: string) {
        return fetch(url, fetchOptions)
            .then(this.handleCasAuthentication);
    }

    post(url: string, payload: any) {
        const postOptions = {
            method: 'POST',
            body: JSON.stringify(payload),
        };
        const options = Object.assign(postOptions, fetchOptions);
        options.headers['Content-Type'] = 'application/json';
        return fetch(url, options)
            .then(this.handleCasAuthentication);
    }

    handleCasAuthentication(response: any) {
        if (isAuthenticationRedirect(response)){
            const redirectUrl = createRedirectUrl();
            redirect(redirectUrl);
        }
        return response;
    }

}

export default new ApiClient();