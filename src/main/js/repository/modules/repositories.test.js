import {shouldFetchRepositories} from './repositories';

it('shouldFetchRepositories is currently loading', () => {
    const state = { repositories: { loading: true } };
    expect(shouldFetchRepositories(state)).toBeFalsy();
});

it('shouldFetchRepositories is already loaded', () => {
    const state = { repositories: { repositories: [], timestamp: (Date.now()) } };
    expect(shouldFetchRepositories(state)).toBeFalsy();
});

it('shouldFetchRepositories is already loaded but timestamp consideres to load again', () => {
    const state = { repositories: { repositories: [], timestamp: (Date.now()-10005)}};
    expect(shouldFetchRepositories(state)).toBeTruthy();
});

it('shouldFetchRepositories', () => {
    const state = { repositories: { } };
    expect(shouldFetchRepositories(state)).toBeTruthy();
});