//@flow
import callApi from '../../apiclient';

const FETCH_WIKI = 'smeagol/wiki/FETCH';
const FETCH_WIKI_SUCCESS = 'smeagol/wiki/FETCH_SUCCESS';
const FETCH_WIKI_FAILURE = 'smeagol/wiki/FETCH_FAILURE';

export function requestWiki() {
    return {
        type: FETCH_WIKI
    };
}

export function reveiveWiki(wiki) {
    return {
        type: FETCH_WIKI_SUCCESS,
        payload: wiki
    };
}

export function failedToFetchWiki(err) {
    return {
        type: FETCH_WIKI_FAILURE,
        payload: err
    };
}

export function fetchWiki(repositoryId, branch) {
    return function(dispatch) {
        dispatch(requestWiki());
        // TODO context path
        return callApi(`/smeagol/api/v1/repositories/${repositoryId}/branches/${branch}.json`)
        .then(response => response.json())
        .then(json => dispatch(reveiveWiki(json)))
        .catch((err) => dispatch(failedToFetchWiki(err)));
    }
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_WIKI:
            return {
                ...state,
                loading: true,
                error: null
            };
        case FETCH_WIKI_SUCCESS:
            return {
                ...state,
                loading: false,
                error: null,
                wiki: action.payload
            };
        case FETCH_WIKI_FAILURE:
            return {
                ...state,
                loading: false,
                error: action.payload
            };

        default:
            return state
    }
}