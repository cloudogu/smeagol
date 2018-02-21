import {shouldFetchPage} from './page';

it('shouldFetchWiki without wiki entry', () => {
    const state = { page: { } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeTruthy();
});

it('shouldFetchWiki with loading entry', () => {
    const state = { page: { 'docs/Home': { loading: true } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchWiki with notFound entry', () => {
    const state = { page: { 'docs/Home': { notFound: true } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchWiki with error entry', () => {
    const state = { page: { 'docs/Home': { error: new Error('something went wrong') } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchWiki with wiki entry', () => {
    const state = { page: { 'docs/Home': { loading: false, page: {} } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeFalsy();
});
