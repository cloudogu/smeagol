//@flow
import {apiClient} from '../../apiclient';

const FETCH_REPOSITORIES = 'smeagol/repositories/FETCH';
const FETCH_REPOSITORIES_SUCCESS = 'smeagol/repositories/FETCH_SUCCESS';
const FETCH_REPOSITORIES_FAILURE = 'smeagol/repositories/FETCH_FAILURE';

const THRESHOLD_TIMESTAMP = 10000;

function requestRepositories() {
    return {
        type: FETCH_REPOSITORIES
    };
}

function receiveRepositories(repositories, timestamp: number) {
    return {
        type: FETCH_REPOSITORIES_SUCCESS,
        payload: repositories,
        timestamp
    };
}

function failedToFetchRepositories(err) {
    return {
        type: FETCH_REPOSITORIES_FAILURE,
        payload: err
    };
}

function fetchRepositories() {
    return function(dispatch) {
        dispatch(requestRepositories());
        return apiClient.get('/repositories')
        .then(response => response.json())
        .then(json => dispatch(receiveRepositories(json, Date.now())))
        .catch((err) => dispatch(failedToFetchRepositories(err)));
    }
}

export function shouldFetchRepositories(state: any): boolean {
    const repositories = state.repositories;
    return (! (repositories.loading || repositories.repositories)) || ((repositories.timestamp + THRESHOLD_TIMESTAMP) < Date.now());
}

export function fetchRepositoriesIfNeeded() {
    return (dispatch, getState) => {
        if (shouldFetchRepositories(getState())) {
            dispatch(fetchRepositories());
        }
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
                timestamp: action.timestamp,
                error: null,
                repositories: action.payload
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