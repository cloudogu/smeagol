// @flow
import { apiClient } from '../../apiclient';

const FETCH_HISTORY = 'smeagol/history/FETCH';
const FETCH_HISTORY_SUCCESS = 'smeagol/history/FETCH_SUCCESS';
const FETCH_HISTORY_FAILURE = 'smeagol/history/FETCH_FAILURE';

export function createHistoryUrl(repositoryId: string, branch: string, path: string) {
    return `/repositories/${repositoryId}/branches/${branch}/history/${path}`;
}

export function fetchHistoryIfNeeded(url: string) {
    return function(dispatch, getState) {
        if (shouldFetchHistory(getState(), url)) {
            dispatch(fetchHistory(url));
        }
    }
}

function shouldFetchHistory(state, url) {
    const byUrl = state.pagehistory[url];
    if (byUrl) {
        return ! (byUrl.error || byUrl.loading || byUrl.notFound || byUrl.pagehistory);
    }
    return true;
}

function fetchHistory(url) {
    return function(dispatch) {
        dispatch(requestHistory(url));
        return apiClient.get(url)
            .then(response => response.json())
            .then(json => dispatch(receiveHistory(url, json)))
            .catch((err) => {
                dispatch(failedToFetchHistory(url, err));
            });
    }
}

function requestHistory(url: string) {
    return {
        type: FETCH_HISTORY,
        url
    };
}

function receiveHistory(url: string, pagehistory: any) {
    return {
        type: FETCH_HISTORY_SUCCESS,
        payload: pagehistory,
        url
    };
}

function failedToFetchHistory(url: string, err: Error) {
    return {
        type: FETCH_HISTORY_FAILURE,
        payload: err,
        url
    };
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_HISTORY:
            return {
                ...state,
                [action.url] : {
                    loading: true,
                    error: null,
                    notFound: false
                }
            };
        case FETCH_HISTORY_SUCCESS:
            return {
                ...state,
                [action.url] : {
                    loading: false,
                    pagehistory: action.payload
                }
            };
        case FETCH_HISTORY_FAILURE:
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