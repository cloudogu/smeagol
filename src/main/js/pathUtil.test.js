import {pathWithTrailingSlash} from './pathUtil';

test('test pathWithTrailingSlash for path with slash', () => {
    let path = "foo/";
    let newPath = pathWithTrailingSlash(path);

    expect(newPath).toBe(path);
});

test('test pathWithTrailingSlash for path without slash', () => {
    let path = "foo/baa";
    let newPath = pathWithTrailingSlash(path);

    expect(newPath).toBe(path + "/");
});