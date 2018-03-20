//@flow
import {apiClient, PAGE_NOT_FOUND_ERROR} from '../../apiclient';

const FETCH_PAGE = 'smeagol/page/FETCH';
const FETCH_PAGE_SUCCESS = 'smeagol/page/FETCH_SUCCESS';
const FETCH_PAGE_FAILURE = 'smeagol/page/FETCH_FAILURE';

const FETCH_PAGE_NOTFOUND = 'smeagol/page/FETCH_NOTFOUND';

const EDIT_PAGE = 'smeagol/page/EDIT';
const EDIT_PAGE_SUCCESS = 'smeagol/page/EDIT_SUCCESS';
const EDIT_PAGE_FAILURE = 'smeagol/page/EDIT_FAILURE';

const CREATE_PAGE = 'smeagol/page/CREATE';
const CREATE_PAGE_SUCCESS = 'smeagol/page/CREATE_SUCCESS';
const CREATE_PAGE_FAILURE = 'smeagol/page/CREATE_FAILURE';

const DELETE_PAGE = 'smeagol/page/DELETE';
const DELETE_PAGE_SUCCESS = 'smeagol/page/DELETE_SUCCESS';
const DELETE_PAGE_FAILURE = 'smeagol/page/DELETE_FAILURE';

const MOVE_PAGE = 'smeagol/page/MOVE';
const MOVE_PAGE_SUCCESS = 'smeagol/page/MOVE_SUCCESS';
const MOVE_PAGE_FAILURE = 'smeagol/page/MOVE_FAILURE';

const RESTORE_PAGE = 'smeagol/page/RESTORE';
const RESTORE_PAGE_SUCCESS = 'smeagol/page/RESTORE_SUCCESS';
const RESTORE_PAGE_FAILURE = 'smeagol/page/RESTORE_FAILURE';

function requestPage(url: string) {
    return {
        type: FETCH_PAGE,
        url
    };
}

function receivePage(url: string, page: any) {
    return {
        type: FETCH_PAGE_SUCCESS,
        payload: page,
        url
    };
}

function failedToFetchPage(url: string, err: Error) {
    return {
        type: FETCH_PAGE_FAILURE,
        payload: err,
        url
    };
}

function pageNotFound(url: string) {
    return {
        type: FETCH_PAGE_NOTFOUND,
        url
    };
}

function fetchPage(url: string) {
    return function(dispatch) {
        dispatch(requestPage(url));
        return apiClient.get(url)
            .then(response => {
                return response;
            })
            .then(response => response.json())
            .then(json => dispatch(receivePage(url, json)))
            .catch((err) => {
                if (err === PAGE_NOT_FOUND_ERROR) {
                    dispatch(pageNotFound(url));
                } else {
                    dispatch(failedToFetchPage(url, err));
                }
            });
    }
}

export function createPageUrl(repositoryId: string, branch: string, path: string) {
    return `/repositories/${repositoryId}/branches/${branch}/pages/${path}`;
}

export function shouldFetchPage(state: any, url: string): boolean {
    const byUrl = state.page[url];
    if (byUrl) {
        return ! (byUrl.error || byUrl.loading || byUrl.notFound || byUrl.page);
    }
    return true;
}

export function fetchPageIfNeeded(url: string) {
    return function(dispatch, getState) {
        if (shouldFetchPage(getState(), url)) {
            dispatch(fetchPage(url));
        }
    }
}

function requestEditPage(url: string) {
    return {
        type: EDIT_PAGE,
        url
    };
}

function editPageSuccess(url: string) {
    return {
        type: EDIT_PAGE_SUCCESS,
        url
    };
}

function editPageFailure(url: string, err: Error) {
    return {
        type: EDIT_PAGE_FAILURE,
        payload: err,
        url
    };
}

export function editPage(url: string, message: string, content: string) {
    return function(dispatch) {
        dispatch(requestEditPage(url));
        return apiClient.post(url, { message: message, content: content })
            .then(() => dispatch(editPageSuccess(url)))
            .catch((err) => dispatch(editPageFailure(url, err)));
    }
}

function requestCreatePage(url: string) {
    return {
        type: CREATE_PAGE,
        url
    };
}

function createPageSuccess(url: string) {
    return {
        type: CREATE_PAGE_SUCCESS,
        url
    };
}

function createPageFailure(url: string, err: Error) {
    return {
        type: CREATE_PAGE_FAILURE,
        payload: err,
        url
    };
}

export function createPage(url: string, message: string, content: string) {
    return function(dispatch) {
        dispatch(requestCreatePage(url));
        return apiClient.post(url, { message: message, content: content })
            .then(() => dispatch(createPageSuccess(url)))
            .catch((err) => dispatch(createPageFailure(url, err)));
    }
}

function requestDeletePage(url: string) {
    return {
        type: DELETE_PAGE,
        url
    };
}

function deletePageSuccess(url: string) {
    return {
        type: DELETE_PAGE_SUCCESS,
        url
    };
}

function deletePageFailure(url: string, err: Error) {
    return {
        type: DELETE_PAGE_FAILURE,
        payload: err,
        url
    };
}

export function deletePage(url: string, message: string, callback: () => void) {
    return function(dispatch) {
        dispatch(requestDeletePage(url));
        return apiClient.delete(url, { message })
            .then(() => {
                callback();
                dispatch(deletePageSuccess(url));
            })
            .catch((err) => dispatch(deletePageFailure(url, err)));
    }
}

function requestMovePage(url: string) {
    return {
        type: MOVE_PAGE,
        url
    };
}

function movePageSuccess(url: string) {
    return {
        type: MOVE_PAGE_SUCCESS,
        url
    };
}

function movePageFailure(url: string, err: Error) {
    return {
        type: MOVE_PAGE_FAILURE,
        payload: err,
        url
    };
}

export function movePage(url: string, message: string, target: string, callback: () => void) {
    return function(dispatch) {
        dispatch(requestMovePage(url));
        return apiClient.post(url, { message , "moveTo": target })
            .then(() => {
                dispatch(movePageSuccess(url));
                callback();
            })
            .catch((err) => dispatch(movePageFailure(url, err)));
    }
}

export function restorePage(url: string, message: string, commit: string, callback: () => void) {
    return function(dispatch) {
        dispatch(requestRestorePage(url));
        return apiClient.post(url, { message: message , restore: commit })
            .then(() => {
                dispatch(restorePageSuccess(url));
                callback();
            })
            .catch((err) => dispatch(restorePageFailure(url, err)));
    }
}

function requestRestorePage(url: string) {
    return {
        type: RESTORE_PAGE,
        url
    };
}

function restorePageSuccess(url: string) {
    return {
        type: RESTORE_PAGE_SUCCESS,
        url
    };
}

function restorePageFailure(url: string, err: Error) {
    return {
        type: RESTORE_PAGE_FAILURE,
        payload: err,
        url
    };
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_PAGE:
        case EDIT_PAGE:
        case MOVE_PAGE:
        case CREATE_PAGE:
        case DELETE_PAGE:
        case RESTORE_PAGE:
            return {
                ...state,
                [action.url] : {
                    loading: true,
                    error: null,
                    notFound: false
                }
            };
        case FETCH_PAGE_SUCCESS:
            return {
                ...state,
                [action.url] : {
                    loading: false,
                    page: action.payload
                }
            };
        case FETCH_PAGE_FAILURE:
        case EDIT_PAGE_FAILURE:
        case MOVE_PAGE_FAILURE:
        case CREATE_PAGE_FAILURE:
        case DELETE_PAGE_FAILURE:
        case RESTORE_PAGE_FAILURE:
            return {
                ...state,
                [action.url] : {
                    loading: false,
                    error: action.payload
                }
            };
        case FETCH_PAGE_NOTFOUND:
            return {
                ...state,
                [action.url] : {
                    loading: false,
                    notFound: true
                }
            };
        case CREATE_PAGE_SUCCESS:
        case EDIT_PAGE_SUCCESS:
        case MOVE_PAGE_SUCCESS:
        case DELETE_PAGE_SUCCESS:
        case RESTORE_PAGE_SUCCESS:
            return {
                ...state,
                [action.url] : {
                    loading: false,
                    error: null,
                    page: null
                }
            };

        default:
            return state
    }
}