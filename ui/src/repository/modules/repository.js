const FETCH_REPOSITORY = 'smeagol/repository/FETCH';
const FETCH_REPOSITORY_SUCCESS = 'smeagol/repository/FETCH_SUCCESS';
const FETCH_REPOSITORY_FAILURE = 'smeagol/repository/FETCH_FAILURE';

export function requestRepository() {
    return {
        type: FETCH_REPOSITORY
    };
}

export function reveiveRepository(repository) {
    return {
        type: FETCH_REPOSITORY_SUCCESS,
        payload: repository
    };
}

export function failedToFetchRepository(err) {
    return {
        type: FETCH_REPOSITORY_FAILURE,
        payload: err
    };
}

export function fetchRepository(repository) {
    return function(dispatch) {
        dispatch(requestRepository())
        return fetch(`/smeagol/api/v1/repositories/${repository}.json`, {
            credentials: 'same-origin'
        })
        .then(response => response.json())
        .then(json => dispatch(reveiveRepository(json)))
        .catch((err) => dispatch(failedToFetchRepository(err)));
    }
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_REPOSITORY:
            return {
                ...state,
                loading: true,
                error: null
            };
        case FETCH_REPOSITORY_SUCCESS:
            return {
                ...state,
                loading: false,
                error: null,
                repository: action.payload
            };
        case FETCH_REPOSITORY_FAILURE:
            return {
                ...state,
                loading: false,
                error: action.payload
            };

        default:
            return state
    }
}