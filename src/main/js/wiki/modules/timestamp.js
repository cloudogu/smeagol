// @flow
const FETCH_TIMESTAMP = 'smeagol/timestamp/FETCH';

export function requestTimestamp() {
    return {
        type: FETCH_TIMESTAMP
    };
}

export default function reducer(state = {}, action = {}) {
    switch (action.type) {
        case FETCH_TIMESTAMP:
            return {
                ...state,
                time: Date.now()
            };
        default:
            return state
    }
}