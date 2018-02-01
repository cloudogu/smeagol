//@flow
import apiClient from '../../apiclient';

const FETCH_PAGE = 'smeagol/page/FETCH';
const FETCH_PAGE_SUCCESS = 'smeagol/page/FETCH_SUCCESS';
const FETCH_PAGE_FAILURE = 'smeagol/page/FETCH_FAILURE';

const EDIT_PAGE = 'smeagol/page/EDIT';
const EDIT_PAGE_SUCCESS = 'smeagol/page/EDIT_SUCCESS';
const EDIT_PAGE_FAILURE = 'smeagol/page/EDIT_FAILURE';

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
    return fetchPageFromUrl(`/smeagol/api/v1/repositories/${repositoryId}/branches/${branch}/pages/${path}`);
}

export function fetchPageFromUrl(url: string) {
    return function(dispatch) {
        dispatch(requestPage());
        // TODO context path
        return apiClient.get(url)
            .then(response => response.json())
            .then(json => dispatch(reveivePage(json)))
            .catch((err) => dispatch(failedToFetchPage(err)));
    }
}

export function requestEditPage() {
    return {
        type: EDIT_PAGE
    };
}

export function editPageSuccess() {
    return {
        type: EDIT_PAGE_SUCCESS
    };
}

export function editPageFailure(err) {
    return {
        type: EDIT_PAGE_FAILURE,
        payload: err
    };
}

export function editPage(page: any, message: string, content: string) {
    return function(dispatch) {
        dispatch(requestEditPage());
        return apiClient.post(page._links.edit.href, { message: message, content: content })
            .then(() => dispatch(editPageSuccess()))
            .then(() => dispatch(fetchPageFromUrl(page._links.self.href)))
            .catch((err) => dispatch(editPageFailure(err)));
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

        case EDIT_PAGE:
            return {
                ...state,
                loading: true,
                error: null
            };
        case EDIT_PAGE_SUCCESS:
            return {
                ...state,
                loading: false,
                error: null,
                page: null
            };
        case EDIT_PAGE_FAILURE:
            return {
                ...state,
                loading: false,
                error: action.payload
            };

        default:
            return state
    }
}