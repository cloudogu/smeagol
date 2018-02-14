// @flow

// get api base url from environment
const apiUrl = process.env.API_URL || process.env.PUBLIC_URL || '/smeagol';

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
    return createUrl('authc?location=' + encodeURIComponent(window.location));
}

function createUrl(url: string) {
    return `${apiUrl}/api/v1/${url}`;
}

function redirect(redirectUrl: string) {
    window.location.href = redirectUrl;
}

class ApiClient {

    get(url: string) {
        return fetch(createUrl(url), fetchOptions)
            .then(this.handleCasAuthentication);
    }

    post(url: string, payload: any) {
        const postOptions = {
            method: 'POST',
            body: JSON.stringify(payload),
        };
        const options = Object.assign(postOptions, fetchOptions);
        options.headers['Content-Type'] = 'application/json';
        return fetch(createUrl(url), options)
            .then(this.handleCasAuthentication);
    }

    delete(url: string, payload: any) {
        const deleteOptions = {
            method: 'DELETE',
            body: JSON.stringify(payload),
        };
        const options = Object.assign(deleteOptions, fetchOptions);
        options.headers['Content-Type'] = 'application/json';
        return fetch(createUrl(url), options)
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