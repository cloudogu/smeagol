import { orderFiles } from './FileBrowser';

it('should return an empty array, if the files are undefined', () => {
    const files = orderFiles(undefined);
    expect(files).toEqual([]);
});

it('should order the files by type and name', () => {
    const files = [{
        type: "directory",
        name: "b"
    },{
        type: "page",
        name: "c"
    }, {
        type: "directory",
        name: "a"
    }, {
        type: "page",
        name: "a"
    }, {
        type: "page",
        name: "b"
    }];

    const expectFile = (file, type, name) => {
        expect(file.type).toBe(type);
        expect(file.name).toBe(name);
    };

    const ordered = orderFiles(files);

    let i = 0;
    expectFile(ordered[i++], "directory", "a");
    expectFile(ordered[i++], "directory", "b");
    expectFile(ordered[i++], "page", "a");
    expectFile(ordered[i++], "page", "b");
    expectFile(ordered[i], "page", "c");
});