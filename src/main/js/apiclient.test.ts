import { apiClient, UNAUTHORIZED_ERROR, module } from "./apiclient";

describe("apiClient", () => {
  beforeEach(() => {
    jest.resetAllMocks();
    jest.restoreAllMocks();
  });

  function mockFetch(status: number, location?: string) {
    global.fetch = jest.fn().mockImplementation(() =>
      Promise.resolve({
        status,
        ok: status < 300,
        headers: {
          get: (key: string) => (key === "location" ? location : null)
        },
        text: async () => ""
      } as any)
    );
  }

  it("returns response for 200", async () => {
    mockFetch(200);
    const response = await apiClient.get("/api/v1/marvinctl");
    expect(response.status).toBe(200);
  });

  it("throws UNAUTHORIZED_ERROR for 401 without location header", async () => {
    mockFetch(401);
    await expect(apiClient.get("/api/v1/marvinctl")).rejects.toThrow(UNAUTHORIZED_ERROR);
  });

  it("calls redirect for 401 with location header", async () => {
    mockFetch(401, "/hitchhikers");
    const redirectMock = jest.spyOn(module, "redirect").mockImplementation(() => {});
    await expect(apiClient.get("/api/v1/marvinctl")).rejects.toThrow(UNAUTHORIZED_ERROR);
    expect(redirectMock).toHaveBeenCalledWith(
      expect.stringContaining("/api/v1/authc?location=http%3A%2F%2Flocalhost%2F")
    );
  });
});
