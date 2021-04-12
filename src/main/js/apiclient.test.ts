import { apiClient, UNAUTHORIZED_ERROR } from "./apiclient";

const DEFAULT_LOCATION = "http://localhost";

function mockFetchAndWindow(status, location) {
  global.fetch = jest.fn().mockImplementation(() => {
    const p = new Promise((resolve) => {
      resolve({
        status: status,
        ok: status < 300,
        headers: {
          get: function (key) {
            if (key === "location") {
              return location;
            }
            return null;
          }
        }
      });
    });

    return p;
  });

  Object.defineProperty(window, "location", {
    value: { href: DEFAULT_LOCATION },
    writable: true
  });
}

test("test apiClient.get", async () => {
  mockFetchAndWindow(200);
  const response = await apiClient.get("/api/v1/marvinctl");

  expect(response.status).toBe(200);

  // be sure no one has done a redirect
  expect(window.location.href).toBe(DEFAULT_LOCATION);
});

test("test apiClient.get with status 401 and without location header", async () => {
  mockFetchAndWindow(401);

  await expect(() => {
    return apiClient.get("/api/v1/marvinctl");
  }).rejects.toThrow(UNAUTHORIZED_ERROR);

  // be sure no one has done a redirect
  expect(window.location.href).toBe(DEFAULT_LOCATION);
});

test("test ApiClient.get with status 401 and location header", async () => {
  mockFetchAndWindow(401, "/hitchhikers");

  await expect(() => {
    return apiClient.get("/api/v1/marvinctl");
  }).rejects.toThrow(UNAUTHORIZED_ERROR);

  expect(window.location.href).toBe("/api/v1/authc?location=http%3A%2F%2Flocalhost");
});
