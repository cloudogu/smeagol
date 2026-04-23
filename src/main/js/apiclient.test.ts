import { apiClient, UNAUTHORIZED_ERROR, module } from "./apiclient";

const DEFAULT_LOCATION = "http://localhost/";

describe("apiClient", () => {
  let originalLocation: Location;

  beforeAll(() => {
    // Save the original location object
    originalLocation = window.location;
    delete window.location;
    // @ts-ignore
    window.location = { href: DEFAULT_LOCATION };
  });

  afterAll(() => {
    // Restore the original location object
    window.location = originalLocation;
  });

  beforeEach(() => {
    jest.resetAllMocks();
    // Reset location
    window.location.href = DEFAULT_LOCATION;
  });

  function mockFetch(status: number, location?: string) {
    global.fetch = jest.fn().mockResolvedValue({
      status,
      ok: status < 300,
      headers: {
        get: (key: string) => (key === "location" ? location : null),
      },
      text: async () => "",
    } as any);
  }

  it("returns response for 200", async () => {
    mockFetch(200);
    const response = await apiClient.get("/api/v1/marvinctl");
    expect(response.status).toBe(200);
    expect(window.location.href).toBe(DEFAULT_LOCATION);
  });

  it("throws UNAUTHORIZED_ERROR for 401 without location header", async () => {
    mockFetch(401);
    await expect(apiClient.get("/api/v1/marvinctl")).rejects.toThrow(UNAUTHORIZED_ERROR);
    expect(window.location.href).toBe(DEFAULT_LOCATION);
  });

  it("calls redirect for 401 with location header", async () => {
    mockFetch(401, "/hitchhikers");
    const redirectMock = jest.spyOn(module, "redirect");
    await expect(apiClient.get("/api/v1/marvinctl")).rejects.toThrow(UNAUTHORIZED_ERROR);
    expect(redirectMock).toHaveBeenCalledWith(
      expect.stringContaining("/api/v1/authc?location=http%3A%2F%2Flocalhost%2F")
    );
  });
});
