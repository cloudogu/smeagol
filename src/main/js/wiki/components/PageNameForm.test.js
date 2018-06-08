import PageNameForm from "./PageNameForm";

jest.mock('react-i18next', () => ({
    // this mock makes sure any components using the translate HoC receive the t function as a prop
    translate: () => Component => {
        Component.defaultProps = { ...Component.defaultProps, t: () => "" };
        return Component;
    },
}));

const form = new PageNameForm();

it('should return true for the paths', () => {
    expect(form.isPageNameValid('abc')).toBeTruthy();
    expect(form.isPageNameValid('some/path')).toBeTruthy();
    expect(form.isPageNameValid('some/deeper/cool/path')).toBeTruthy();
    expect(form.isPageNameValid('Home')).toBeTruthy();
    expect(form.isPageNameValid('Home.page')).toBeTruthy();
    expect(form.isPageNameValid('Home_page')).toBeTruthy();
    expect(form.isPageNameValid('Home-page')).toBeTruthy();
    expect(form.isPageNameValid('./Home')).toBeTruthy();
});

it('should not contain ..', () => {
    expect(form.isPageNameValid('abc..def')).toBeFalsy();
    expect(form.isPageNameValid('..')).toBeFalsy();
});

it('should not contain //', () => {
    expect(form.isPageNameValid('abc//def')).toBeFalsy();
});

it('should start with /', () => {
    expect(form.isPageNameValid('/def')).toBeFalsy();
});

it('should have at least one char', () => {
    expect(form.isPageNameValid('')).toBeFalsy();
});

it('should not contain illegal chars', () => {
    expect(form.isPageNameValid('abc?def')).toBeFalsy();
    expect(form.isPageNameValid('abc!def')).toBeFalsy();
    expect(form.isPageNameValid('abc&def')).toBeFalsy();
    expect(form.isPageNameValid('abc;def')).toBeFalsy();
    expect(form.isPageNameValid('abc*def')).toBeFalsy();
});
