//@flow
import {apiClient, PAGE_NOT_FOUND_ERROR} from '../../apiclient';


const FETCH_WIKI = 'smeagol/wiki/FETCH';
const FETCH_WIKI_SUCCESS = 'smeagol/wiki/FETCH_SUCCESS';
const FETCH_WIKI_FAILURE = 'smeagol/wiki/FETCH_FAILURE';
const FETCH_WIKI_NOTFOUND = 'smeagol/wiki/FETCH_NOTFOUND';

const THRESHOLD_TIMESTAMP = 10000;

function requestWiki(id: string) {
    return {
        type: FETCH_WIKI,
        id
    };
}

function receiveWiki(id: string, wiki: any, timestamp: number) {
    return {
        type: FETCH_WIKI_SUCCESS,
        payload: wiki,
        timestamp,
        id
    };
}

function failedToFetchWiki(id: string, err: Error) {
    return {
        type: FETCH_WIKI_FAILURE,
        payload: err,
        id
    };
}

function wikiNotFound(id: string) {
    return {
        type: FETCH_WIKI_NOTFOUND,
        id
    };
}


function fetchWiki(repositoryId: string, branch: string) {
    const id = createId(repositoryId, branch);
    return function(dispatch) {
        dispatch(requestWiki(id));
        // TODO context path
        return apiClient.get(`/repositories/${repositoryId}/branches/${branch}.json`)
            .then(response => response.json())
            .then(json => dispatch(receiveWiki(id, json, Date.now())))
            .catch((err) => {
                if (err === PAGE_NOT_FOUND_ERROR) {
                    dispatch(wikiNotFound(id))
                } else {
                    dispatch(failedToFetchWiki(id, err))
                }
            });
    }
}

export function createId(repositoryId: string, branch: string): string {
    return repositoryId + '@' + branch;
}

export function shouldFetchWiki(state: any, repositoryId: string, branch: string): boolean {
    const id = createId(repositoryId, branch);
    const byId = state.wiki[id];
    if (byId) {
        return (! (byId.loading|| byId.wiki)) || ((byId.timestamp + THRESHOLD_TIMESTAMP) < Date.now());
    }
    return true;
}

export function fetchWikiIfNeeded(repositoryId: string, branch: string) {
    return function(dispatch, getState) {
        if (shouldFetchWiki(getState(), repositoryId, branch)) {
            dispatch(fetchWiki(repositoryId, branch));
        }
    };
}

export function selectByRepositoryAndBranch(state: any, repositoryId: string, branch: string) {
    const id = createId(repositoryId, branch);
    return state.wiki[id] || {};
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_WIKI:
            return {
                ...state,
                [action.id]: {
                    loading: true,
                    payload: null,
                    error: null
                }
            };
        case FETCH_WIKI_SUCCESS:
            return {
                ...state,
                [action.id]: {
                    loading: false,
                    timestamp: action.timestamp,
                    wiki: action.payload
                }
            };
        case FETCH_WIKI_FAILURE:
            return {
                ...state,
                [action.id]: {
                    loading: false,
                    error: action.payload
                }
            };
        case FETCH_WIKI_NOTFOUND:
            return {
                ...state,
                [action.id]: {
                    loading: false,
                    notFound: true
                }
            };
        default:
            return state
    }
}