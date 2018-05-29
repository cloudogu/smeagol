import {apiClient} from "../../apiclient";


const FETCH_SEARCH = 'smeagol/search/FETCH';
const FETCH_SEARCH_SUCCESS = 'smeagol/search/FETCH_SUCCESS';
const FETCH_SEARCH_FAILURE = 'smeagol/search/FETCH_FAILURE';

export function createSearchUrl(repositoryId: string, branch: string, query: string) {
    return `/repositories/${repositoryId}/branches/${branch}/search?query=${query}`;
}

export function fetchSearchResultsIfNeeded(url: string) {
    return function(dispatch, getState) {
        if (shouldFetchSearchResults(getState(), url)) {
            dispatch(fetchSearchResults(url));
        }
    }
}

export function shouldFetchSearchResults(state: any, url: string): boolean {
    const byUrl = state.search[url];
    if (byUrl) {
        return ! (byUrl.error || byUrl.loading || byUrl.notFound || byUrl.results);
    }
    return true;
}

function fetchSearchResults(url: string) {
    return function(dispatch) {
        dispatch(requestSearchResults(url));
        return apiClient.get(url)
            .then(response => response.json())
            .then(json => dispatch(receiveSearchResults(url, json)))
            .catch((err) => {
                dispatch(failedToFetchSearchResults(url, err));
            });
    }
}

function requestSearchResults(url: string) {
    return {
        type: FETCH_SEARCH,
        url
    };
}

function receiveSearchResults(url: string, results: any) {
    return {
        type: FETCH_SEARCH_SUCCESS,
        payload: results,
        url
    };
}

function failedToFetchSearchResults(url: string, err: Error) {
    return {
        type: FETCH_SEARCH_FAILURE,
        payload: err,
        url
    };
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_SEARCH:
            return {
                ...state,
                [action.url] : {
                    loading: true,
                    error: null,
                    notFound: false
                }
            };
        case FETCH_SEARCH_SUCCESS:
            return {
                ...state,
                [action.url] : {
                    loading: false,
                    results: action.payload
                }
            };
        case FETCH_SEARCH_FAILURE:
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