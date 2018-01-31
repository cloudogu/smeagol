const fetchOptions = {
    credentials: 'same-origin'
};

// TODO handle session timeout and cas redirect

function callApi(url) {
    return fetch(url, fetchOptions)
}

export default callApi;