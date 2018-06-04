import {shouldFetchRepositories} from './repositories';

it('shouldFetchRepositories is currently loading', () => {
    const state = { repositories: { loading: true } };
    expect(shouldFetchRepositories(state)).toBeFalsy();
});

it('shouldFetchRepositories is already loaded', () => {
    const state = { repositories: { repositories: [], timestamp: (Date.now()) } };
    expect(shouldFetchRepositories(state)).toBeFalsy();
});

it('shouldFetchRepositories with expired timestamp', () => {
    const state = { repositories: { repositories: [], timestamp: (Date.now()-10005)}};
    expect(shouldFetchRepositories(state)).toBeTruthy();
});

it('should not fetch repositories if timestamp is not expired', () => {
    const state = { repositories: { repositories: [], timestamp: (Date.now()-9995)}};
    expect(shouldFetchRepositories(state)).toBeFalsy();
});

it('shouldFetchRepositories', () => {
    const state = { repositories: { } };
    expect(shouldFetchRepositories(state)).toBeTruthy();
});