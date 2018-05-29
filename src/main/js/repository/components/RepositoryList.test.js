import {createRepositoryGroups} from './RepositoryList';

test('createRepositoryGroups ordering and creation of repository groups', () => {
    const repositories = [{
        "name": "training/k8s-foundations-presentation",
    }, {
        "name": "training/test",
    }, {
        "name": "something",
    }, {
        "name": "different/two",
    }, {
        "name": "different/one",
    }];

    const groups = createRepositoryGroups(repositories);

    // ensure groups are ordered by name and main is always the last one
    expect(groups[0].name).toBe('different');
    expect(groups[0].repositories.length).toBe(2);
    expect(groups[1].name).toBe('training');
    expect(groups[1].repositories.length).toBe(2);
    expect(groups[2].name).toBe('main');
    expect(groups[2].repositories.length).toBe(1);
});