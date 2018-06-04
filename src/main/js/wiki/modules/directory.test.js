import {shouldFetchDirectory} from './directory';

it('shouldFetchDirectory without directory entry', () => {
    const state = { directory: { } };
    expect(shouldFetchDirectory(state, 'docs/Home')).toBeTruthy();
});

it('shouldFetchDirectory with loading entry', () => {
    const state = { directory: { 'docs/Home': { loading: true, timestamp: Date.now() } } };
    expect(shouldFetchDirectory(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchDirectory with notFound entry', () => {
    const state = { directory: { 'docs/Home': { notFound: true } } };
    expect(shouldFetchDirectory(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchDirectory with error entry', () => {
    const state = { directory: { 'docs/Home': { error: new Error('something went wrong') } } };
    expect(shouldFetchDirectory(state, 'docs/Home')).toBeFalsy();
});

it('shouldFetchDirectory with expired timestamp', () => {
    const state = { directory: { 'docs/Home': { loading: true, timestamp: (Date.now()-10005) } } };
    expect(shouldFetchDirectory(state, 'docs/Home')).toBeTruthy();
});

it('should not fetch directory if timestamp is not expired', () => {
    const state = { directory: { 'docs/Home': { loading: true, timestamp: (Date.now()-9995) } } };
    expect(shouldFetchDirectory(state, 'docs/Home')).toBeFalsy();
});