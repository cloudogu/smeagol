import {shouldFetchSearchResults} from './search';

it('shouldFetchSearchResults without directory entry', () => {
    const state = { search: { } };
    expect(shouldFetchSearchResults(state, 'docs/Home')).toBeTruthy();
});

it('shouldFetchSearchResults with loading entry', () => {
    const state = { search: { 'docs/Home': { loading: true, timestamp: Date.now() } } };
    expect(shouldFetchSearchResults(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchSearchResults with notFound entry', () => {
    const state = { search: { 'docs/Home': { notFound: true } } };
    expect(shouldFetchSearchResults(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchSearchResults with error entry', () => {
    const state = { search: { 'docs/Home': { error: new Error('something went wrong') } } };
    expect(shouldFetchSearchResults(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchSearchResults with expired timestamp', () => {
    const state = { search: { 'docs/Home': { loading: true, timestamp: (Date.now()-10005) } } };
    expect(shouldFetchSearchResults(state, 'docs/Home')).toBeTruthy();
});