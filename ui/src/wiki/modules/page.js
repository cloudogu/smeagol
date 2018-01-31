//@flow
import callApi from '../../apiclient';

const FETCH_PAGE = 'smeagol/page/FETCH';
const FETCH_PAGE_SUCCESS = 'smeagol/page/FETCH_SUCCESS';
const FETCH_PAGE_FAILURE = 'smeagol/page/FETCH_FAILURE';

export function requestPage() {
    return {
        type: FETCH_PAGE
    };
}

export function reveivePage(page) {
    return {
        type: FETCH_PAGE_SUCCESS,
        payload: page
    };
}

export function failedToFetchPage(err) {
    return {
        type: FETCH_PAGE_FAILURE,
        payload: err
    };
}

export function fetchPage(repositoryId, branch, path) {
    return function(dispatch) {
        dispatch(requestPage());
        // TODO context path
        return callApi(`/smeagol/api/v1/repositories/${repositoryId}/branches/${branch}/pages/${path}`)
        .then(response => response.json())
        .then(json => dispatch(reveivePage(json)))
        .catch((err) => dispatch(failedToFetchPage(err)));
    }
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_PAGE:
            return {
                ...state,
                loading: true,
                error: null
            };
        case FETCH_PAGE_SUCCESS:
            return {
                ...state,
                loading: false,
                error: null,
                page: action.payload
            };
        case FETCH_PAGE_FAILURE:
            return {
                ...state,
                loading: false,
                error: action.payload
            };

        default:
            return state
    }
}