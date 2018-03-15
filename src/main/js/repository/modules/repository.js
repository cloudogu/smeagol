// @flow
import {apiClient} from '../../apiclient';

const FETCH_REPOSITORY = 'smeagol/repository/FETCH';
const FETCH_REPOSITORY_SUCCESS = 'smeagol/repository/FETCH_SUCCESS';
const FETCH_REPOSITORY_FAILURE = 'smeagol/repository/FETCH_FAILURE';

function requestRepository(id: string) {
    return {
        type: FETCH_REPOSITORY,
        id
    };
}

function receiveRepository(id: string, repository: any) {
    return {
        type: FETCH_REPOSITORY_SUCCESS,
        payload: repository,
        id
    };
}

function failedToFetchRepository(id: string, err: Error) {
    return {
        type: FETCH_REPOSITORY_FAILURE,
        payload: err,
        id
    };
}

function fetchRepository(id: string) {
    return function(dispatch) {
        dispatch(requestRepository(id));
        return apiClient.get(`/repositories/${id}`)
            .then(response => response.json())
            .then(json => dispatch(receiveRepository(id, json)))
            .catch((err) => dispatch(failedToFetchRepository(id, err)));
    }
}

export function shouldFetchRepository(state: any, id: string) {
    const byId = state.repository[id];
    if (byId) {
        return !(byId.loading || byId.repository);
    }
    return true;
}

export function fetchRepositoryByIdIfNeeded(id: string) {
    return function(dispatch, getState) {
        if (shouldFetchRepository(getState(), id)) {
            dispatch(fetchRepository(id));
        }
    }
}

export function selectById(state: any, id: string) : any {
    return state.repository[id] || {};
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_REPOSITORY:
            return {
                ...state,
                [action.id]: {
                    loading: true,
                    error: null
                }
            };
        case FETCH_REPOSITORY_SUCCESS:
            return {
                ...state,
                [action.id]: {
                    loading: false,
                    error: null,
                    repository: action.payload
                }
            };
        case FETCH_REPOSITORY_FAILURE:
            return {
                ...state,
                [action.id]: {
                    loading: false,
                    error: action.payload
                }
            };

        default:
            return state
    }
}