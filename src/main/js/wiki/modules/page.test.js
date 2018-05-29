import {shouldFetchPage} from './page';

it('shouldFetchPage without wiki entry', () => {
    const state = { page: { } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeTruthy();
});

it('shouldFetchPage with loading entry', () => {
    const state = { page: { 'docs/Home': { loading: true, timestamp: Date.now() } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchPage with notFound entry', () => {
    const state = { page: { 'docs/Home': { notFound: true } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchPage with error entry', () => {
    const state = { page: { 'docs/Home': { error: new Error('something went wrong') } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchPage with wiki entry', () => {
    const state = { page: { 'docs/Home': { loading: false, page: {} } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeFalsy();
});


it('shouldFetchPage with expired timestamp', () => {
    const state = { page: { 'docs/Home': { loading: true, timestamp: (Date.now()-10005) } } };
    expect(shouldFetchPage(state, 'docs/Home')).toBeTruthy();
});