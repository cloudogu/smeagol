import {shouldFetchHistory} from './pagehistory';

it('shouldFetchHistory without directory entry', () => {
    const state = { pagehistory: { } };
    expect(shouldFetchHistory(state, 'docs/Home')).toBeTruthy();
});

it('shouldFetchHistory with loading entry', () => {
    const state = { pagehistory: { 'docs/Home': { loading: true, timestamp: Date.now() } } };
    expect(shouldFetchHistory(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchHistory with notFound entry', () => {
    const state = { pagehistory: { 'docs/Home': { notFound: true } } };
    expect(shouldFetchHistory(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchHistory with error entry', () => {
    const state = { pagehistory: { 'docs/Home': { error: new Error('something went wrong') } } };
    expect(shouldFetchHistory(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchHistory with expired timestamp', () => {
    const state = { pagehistory: { 'docs/Home': { loading: true, timestamp: (Date.now()-10005) } } };
    expect(shouldFetchHistory(state, 'docs/Home')).toBeTruthy();
});