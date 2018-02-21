import {shouldFetchRepositories} from './repositories';

it('shouldFetchRepositories is currently loading', () => {
    const state = { repositories: { loading: true } };
    expect(shouldFetchRepositories(state)).toBeFalsy();
});

it('shouldFetchRepositories is already loaded', () => {
    const state = { repositories: { repositories: [] } };
    expect(shouldFetchRepositories(state)).toBeFalsy();
});

it('shouldFetchRepositories', () => {
    const state = { repositories: { } };
    expect(shouldFetchRepositories(state)).toBeTruthy();
});