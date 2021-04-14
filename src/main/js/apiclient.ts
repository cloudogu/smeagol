// get api base url from environment
const apiUrl = process.env.API_URL || process.env.PUBLIC_URL || "";

export const PAGE_NOT_FOUND_ERROR = Error("page not found");
export const UNAUTHORIZED_ERROR = Error("unauthorized");
export const MISSING_SMEAGOL_PLUGIN = Error("missing smeagol plugin");

// fetch does not send the X-Requested-With header (https://github.com/github/fetch/issues/17),
// but we need the header to detect ajax request (AjaxAwareAuthenticationRedirectStrategy).
const fetchOptions = {
  credentials: "same-origin",
  headers: {
    "X-Requested-With": "XMLHttpRequest"
  }
};

function isAuthenticationRedirect(response) {
  if (response.status === 401) {
    const redirectTarget = response.headers.get("location");
    if (redirectTarget) {
      return true;
    }
  }
  return false;
}

async function handleStatusCode(response: Response) {
  if (!response.ok) {
    if (response.status === 401) {
      throw UNAUTHORIZED_ERROR;
    }
    if (response.status === 404) {
      throw PAGE_NOT_FOUND_ERROR;
    }
    const body = await response.text();
    if (body === "SCM is missing Smeagol plugin") {
      throw MISSING_SMEAGOL_PLUGIN;
    }
    throw new Error(response.body.message || "server returned status code " + response.status);
  }
  return response;
}

function createRedirectUrl() {
  return createUrl("authc?location=" + encodeURIComponent(window.location.href));
}

function createUrl(url: string) {
  return `${apiUrl}/api/v1/${url}`;
}

function redirect(redirectUrl: string) {
  window.location.href = redirectUrl;
}

class ApiClient {
  get(url: string) {
    return fetch(createUrl(url), fetchOptions).then(this.handleCasAuthentication).then(handleStatusCode);
  }

  post(url: string, payload: any) {
    return this.httpRequestWithJSONBody(url, payload, "POST");
  }

  delete(url: string, payload: any) {
    return this.httpRequestWithJSONBody(url, payload, "DELETE");
  }

  httpRequestWithJSONBody(url: string, payload: any, method: string) {
    let options = {
      method: method,
      body: JSON.stringify(payload)
    };
    options = Object.assign(options, fetchOptions);
    options.headers["Content-Type"] = "application/json";

    return fetch(createUrl(url), options).then(this.handleCasAuthentication).then(handleStatusCode);
  }

  handleCasAuthentication(response: any) {
    if (isAuthenticationRedirect(response)) {
      const redirectUrl = createRedirectUrl();
      redirect(redirectUrl);
    }
    return response;
  }
}

export const apiClient = new ApiClient();
