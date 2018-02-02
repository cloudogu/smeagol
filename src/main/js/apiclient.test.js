import apiClient from './apiclient';

const DEFAULT_LOCATION = 'http://localhost';

function mockFetchAndWindow(status, location) {
    global.fetch = jest.fn().mockImplementation(() => {
        var p = new Promise((resolve) => {
            resolve({
                status: status,
                headers: {
                    get: function(key) {
                        if (key === 'location') {
                            return location;
                        }
                        return null;
                    }
                }
            });
        });

        return p;
    });

    Object.defineProperty(window.location, 'href', {
        writable: true,
        value: DEFAULT_LOCATION
    })
}

test('test apiClient.get', async () => {
    mockFetchAndWindow(200);
    const response = await apiClient.get('/api/v1/marvinctl');

    expect(response.status).toBe(200);

    // be sure no one has done a redirect
    expect(window.location.href).toBe(DEFAULT_LOCATION);
});

test('test apiClient.get with status 401 and without location header', async () => {
    mockFetchAndWindow(401);
    const response = await apiClient.get('/api/v1/marvinctl');

    expect(response.status).toBe(401);

    // be sure no one has done a redirect
    expect(window.location.href).toBe(DEFAULT_LOCATION);
});

test('test ApiClient.get with status 401 and location header', async () => {
    mockFetchAndWindow(401, '/hitchhikers');
    await apiClient.get('/api/v1/marvinctl');

    expect(window.location.href).toBe('/smeagol/api/v1/authc?location=http%3A%2F%2Flocalhost%2F');
});