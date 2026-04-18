import { describe, expect, it, vi } from "vitest";

import { getProductById, listProducts } from "@/features/products/api/products-api";
import { API_ENDPOINTS } from "@/shared/api/endpoints";
import { apiRequest } from "@/shared/api/request";

vi.mock("@/shared/api/request", () => ({
  apiRequest: vi.fn(),
}));

const mockedApiRequest = vi.mocked(apiRequest);

describe("products-api", () => {
  it("listProducts sends query params and trims name", async () => {
    mockedApiRequest.mockResolvedValueOnce({
      content: [
        {
          id: 1,
          name: "Laptop",
          description: "Portable workstation",
          price: 1200,
          stock: 7,
          categoryId: 3,
          categoryName: "Electronics",
        },
      ],
      page: 0,
      size: 12,
      totalElements: 1,
      totalPages: 1,
      first: true,
      last: true,
      sortBy: "price",
      sortDir: "asc",
    });

    const result = await listProducts({
      page: 0,
      size: 12,
      sortBy: "price",
      sortDir: "asc",
      name: "  laptop  ",
      categoryId: 3,
    });

    expect(mockedApiRequest).toHaveBeenCalledWith({
      method: "GET",
      url: API_ENDPOINTS.PRODUCTS,
      params: {
        page: 0,
        size: 12,
        sortBy: "price",
        sortDir: "asc",
        name: "laptop",
        categoryId: 3,
      },
    });

    expect(result.content).toHaveLength(1);
    expect(result.content[0]?.name).toBe("Laptop");
  });

  it("getProductById calls product detail endpoint", async () => {
    mockedApiRequest.mockResolvedValueOnce({
      id: 22,
      name: "Mechanical Keyboard",
      description: "Tactile switches",
      price: 199,
      stock: 15,
      categoryId: 5,
      categoryName: "Accessories",
    });

    const result = await getProductById("22");

    expect(mockedApiRequest).toHaveBeenCalledWith({
      method: "GET",
      url: `${API_ENDPOINTS.PRODUCTS}/22`,
    });
    expect(result.id).toBe(22);
    expect(result.name).toBe("Mechanical Keyboard");
  });

  it("throws when server returns invalid product shape", async () => {
    mockedApiRequest.mockResolvedValueOnce({
      id: 22,
      name: "Broken Product",
      price: "199",
      stock: 15,
    });

    await expect(getProductById("22")).rejects.toThrow();
  });
});
