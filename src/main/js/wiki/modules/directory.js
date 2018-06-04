// @flow
import {apiClient} from '../../apiclient';

const FETCH_DIRECTORY = 'smeagol/directory/FETCH';
const FETCH_DIRECTORY_SUCCESS = 'smeagol/directory/FETCH_SUCCESS';
const FETCH_DIRECTORY_FAILURE = 'smeagol/directory/FETCH_FAILURE';

const THRESHOLD_TIMESTAMP = 10000;

export function createDirectoryUrl(repositoryId: string, branch: string, path: string) {
    return `/repositories/${repositoryId}/branches/${branch}/directories/${path}`;
}

export function fetchDirectoryIfNeeded(url: string) {
    return function(dispatch, getState) {
        if (shouldFetchDirectory(getState(), url)) {
            dispatch(fetchDirectory(url));
        }
    }
}

export function shouldFetchDirectory(state, url) {
    const byUrl = state.directory[url];
    if (byUrl) {
        return (! (byUrl.error || byUrl.loading || byUrl.notFound || byUrl.directory)) || ((byUrl.timestamp + THRESHOLD_TIMESTAMP) < Date.now());
    }
    return true;
}

function fetchDirectory(url) {
    return function(dispatch) {
        dispatch(requestDirectory(url));
        return apiClient.get(url)
            .then(response => response.json())
            .then(json => dispatch(receiveDirectory(url, json, Date.now())))
            .catch((err) => {
                dispatch(failedToFetchDirectory(url, err));
            });
    }
}

function requestDirectory(url: string) {
    return {
        type: FETCH_DIRECTORY,
        url
    };
}

function receiveDirectory(url: string, directory: any, timestamp: number) {
    return {
        type: FETCH_DIRECTORY_SUCCESS,
        payload: directory,
        timestamp,
        url
    };
}

function failedToFetchDirectory(url: string, err: Error) {
    return {
        type: FETCH_DIRECTORY_FAILURE,
        payload: err,
        url
    };
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_DIRECTORY:
            return {
                ...state,
                [action.url] : {
                    loading: true,
                    error: null,
                    notFound: false
                }
            };
        case FETCH_DIRECTORY_SUCCESS:
            return {
                ...state,
                [action.url] : {
                    loading: false,
                    timestamp: action.timestamp,
                    directory: action.payload
                }
            };
        case FETCH_DIRECTORY_FAILURE:
            return {
                ...state,
                [action.url] : {
                    loading: false,
                    error: action.payload
                }
            };
        default:
            return state
    }
}