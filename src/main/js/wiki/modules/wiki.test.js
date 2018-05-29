import {createId, shouldFetchWiki} from './wiki';

it('createId', () => {
    expect(createId('42', 'master')).toBe('42@master');
});

it('shouldFetchWiki without wiki entry', () => {
    const state = { wiki: { } };
    expect(shouldFetchWiki(state, '42', 'master')).toBeTruthy();
});

it('shouldFetchWiki with loading entry', () => {
    const state = { wiki: { '42@master': { loading: true, timestamp: Date.now() } } };
    expect(shouldFetchWiki(state, '42', 'master')).toBeFalsy();
});

it('shouldFetchWiki with wiki entry', () => {
    const state = { wiki: { '42@master': { loading: false, wiki: {} } } };
    expect(shouldFetchWiki(state, '42', 'master')).toBeFalsy();
});

it('shouldFetchWiki is already loaded but timestamp consideres to load again', () => {
    const state = { wiki: { '42@master': { loading: true, timestamp: (Date.now()-10005) } } };
    expect(shouldFetchWiki(state, '42', 'master')).toBeTruthy();
});