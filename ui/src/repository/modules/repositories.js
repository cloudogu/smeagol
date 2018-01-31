//@flow
const FETCH_REPOSITORIES = 'smeagol/repositories/FETCH';
const FETCH_REPOSITORIES_SUCCESS = 'smeagol/repositories/FETCH_SUCCESS';
const FETCH_REPOSITORIES_FAILURE = 'smeagol/repositories/FETCH_FAILURE';

export function requestRepositories() {
    return {
        type: FETCH_REPOSITORIES
    };
}

export function reveiveRepositories(repositories) {
    return {
        type: FETCH_REPOSITORIES_SUCCESS,
        payload: repositories
    };
}

export function failedToFetchRepositories(err) {
    return {
        type: FETCH_REPOSITORIES_FAILURE,
        payload: err
    };
}

export function fetchRepositories() {
    return function(dispatch) {
        dispatch(requestRepositories());
        // TODO context path
        return fetch('/smeagol/api/v1/repositories.json', {
            credentials: 'same-origin'
        })
        .then(response => response.json())
        .then(json => dispatch(reveiveRepositories(json)))
        .catch((err) => dispatch(failedToFetchRepositories(err)));
    }
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_REPOSITORIES:
            return {
                ...state,
                loading: true,
                error: null
            };
        case FETCH_REPOSITORIES_SUCCESS:
            return {
                ...state,
                loading: false,
                error: null,
                items: action.payload
            };
        case FETCH_REPOSITORIES_FAILURE:
            return {
                ...state,
                loading: false,
                error: action.payload
            };

        default:
            return state
    }
}