import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

import { ProductsScreen } from "@/features/products/components/products-screen";
import { useProductCategories } from "@/features/products/hooks/use-product-categories";
import { useProductFilters } from "@/features/products/hooks/use-product-filters";
import { useProducts } from "@/features/products/hooks/use-products";

vi.mock("@/features/products/hooks/use-products", () => ({
  useProducts: vi.fn(),
}));

vi.mock("@/features/products/hooks/use-product-categories", () => ({
  useProductCategories: vi.fn(),
}));

vi.mock("@/features/products/hooks/use-product-filters", () => ({
  useProductFilters: vi.fn(),
}));

const mockedUseProducts = vi.mocked(useProducts);
const mockedUseProductCategories = vi.mocked(useProductCategories);
const mockedUseProductFilters = vi.mocked(useProductFilters);

const applyFilters = vi.fn();
const goToPage = vi.fn();
const resetFilters = vi.fn();
const refetch = vi.fn();

function setupDefaultMocks() {
  mockedUseProductFilters.mockReturnValue({
    filters: {
      page: 0,
      size: 12,
      sort: "newest",
      name: "",
      categoryId: undefined,
    },
    applyFilters,
    goToPage,
    resetFilters,
  });

  mockedUseProductCategories.mockReturnValue({
    data: [
      { id: 1, name: "Electronics", description: null },
      { id: 2, name: "Home", description: null },
    ],
    isPending: false,
  } as never);

  mockedUseProducts.mockReturnValue({
    data: {
      content: [
        {
          id: 1,
          name: "Laptop",
          description: "Portable workstation",
          price: 1299,
          stock: 12,
          categoryName: "Electronics",
        },
      ],
      page: 0,
      size: 12,
      totalElements: 1,
      totalPages: 1,
      first: true,
      last: true,
      sortBy: "id",
      sortDir: "desc",
    },
    isPending: false,
    isError: false,
    isFetching: false,
    refetch,
  } as never);
}

describe("ProductsScreen", () => {
  beforeEach(() => {
    applyFilters.mockReset();
    goToPage.mockReset();
    resetFilters.mockReset();
    refetch.mockReset();

    setupDefaultMocks();
  });

  it("renders storefront listing structure (filters, toolbar, grid)", () => {
    render(<ProductsScreen />);

    expect(screen.getByTestId("products-filter-panel")).toBeInTheDocument();
    expect(screen.getByRole("combobox", { name: /sort/i })).toBeInTheDocument();
    expect(screen.getByTestId("products-grid")).toBeInTheDocument();
  });

  it("renders loading state when products are pending", () => {
    mockedUseProducts.mockReturnValue({
      data: undefined,
      isPending: true,
      isError: false,
      isFetching: false,
      refetch,
    } as never);

    render(<ProductsScreen />);

    expect(screen.getByTestId("products-loading")).toBeInTheDocument();
  });

  it("renders empty state when products response is empty", () => {
    mockedUseProducts.mockReturnValue({
      data: {
        content: [],
        page: 0,
        size: 12,
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: true,
        sortBy: "id",
        sortDir: "desc",
      },
      isPending: false,
      isError: false,
      isFetching: false,
      refetch,
    } as never);

    render(<ProductsScreen />);

    expect(screen.getByText("No products matched these filters")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /clear filters/i })).toBeInTheDocument();
  });

  it("renders error state and retries on user action", async () => {
    const user = userEvent.setup();

    mockedUseProducts.mockReturnValue({
      data: undefined,
      isPending: false,
      isError: true,
      isFetching: false,
      refetch,
    } as never);

    render(<ProductsScreen />);

    expect(screen.getByText("There was an issue loading products")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /retry/i }));

    expect(refetch).toHaveBeenCalledTimes(1);
  });

  it("renders product card essentials from data", () => {
    render(<ProductsScreen />);

    expect(screen.getByText("Laptop")).toBeInTheDocument();
    expect(screen.getByText("$1,299.00")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /add to cart/i })).toBeInTheDocument();
  });

  it("updates sort through toolbar and delegates to filter orchestrator", async () => {
    const user = userEvent.setup();

    render(<ProductsScreen />);

    await user.selectOptions(screen.getByRole("combobox", { name: /sort/i }), "price-desc");

    expect(applyFilters).toHaveBeenCalledWith({
      name: "",
      categoryId: undefined,
      sort: "price-desc",
    });
  });
});
