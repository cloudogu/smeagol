import {shouldFetchRepository} from './repository';

it('shouldFetchRepository without repository entry', () => {
    const state = { repository: {} };
    expect(shouldFetchRepository(state, 42)).toBeTruthy();
});

it('shouldFetchRepository with loading entry', () => {
    const state = { repository: { 42: { loading: true } } };
    expect(shouldFetchRepository(state, 42)).toBeFalsy();
});

it('shouldFetchRepository with already loaded', () => {
    const state = { repository: { 42: { loading: false, timestamp: Date.now(), repository: {} } }};
    expect(shouldFetchRepository(state, 42)).toBeFalsy();
});

it('shouldFetchRepository with expired timestamp', () => {
    const state = { repository: { 42: { loading: false, timestamp: (Date.now()-10005), repository: {} } }};
    expect(shouldFetchRepository(state)).toBeTruthy();
});