import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

import { useProductsQuery } from "@/features/products/hooks/use-products-query";
import { ProductsListScreen } from "@/features/products/components/products-list-screen";

vi.mock("@/features/products/hooks/use-products-query", () => ({
  useProductsQuery: vi.fn(),
}));

const mockedUseProductsQuery = vi.mocked(useProductsQuery);

describe("ProductsListScreen", () => {
  beforeEach(() => {
    mockedUseProductsQuery.mockReset();
  });

  it("renders left filter panel and loading state", () => {
    mockedUseProductsQuery.mockReturnValue({
      data: undefined,
      isPending: true,
      isError: false,
      refetch: vi.fn(),
    } as never);

    render(<ProductsListScreen />);

    expect(screen.getByTestId("products-filter-panel")).toBeInTheDocument();
    expect(screen.getByTestId("products-loading")).toBeInTheDocument();
  });

  it("renders empty state", () => {
    mockedUseProductsQuery.mockReturnValue({
      data: { content: [] },
      isPending: false,
      isError: false,
      refetch: vi.fn(),
    } as never);

    render(<ProductsListScreen />);

    expect(screen.getByText("No products found")).toBeInTheDocument();
  });

  it("renders success grid and applies filters", async () => {
    const user = userEvent.setup();

    mockedUseProductsQuery.mockReturnValue({
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
      },
      isPending: false,
      isError: false,
      refetch: vi.fn(),
    } as never);

    render(<ProductsListScreen />);

    expect(screen.getByTestId("products-grid")).toBeInTheDocument();
    expect(screen.getByText("Laptop")).toBeInTheDocument();

    await user.type(screen.getByTestId("products-filter-name"), "gaming");
    await user.selectOptions(screen.getByTestId("products-filter-sort"), "desc");
    await user.click(screen.getByTestId("products-filter-apply"));

    expect(mockedUseProductsQuery).toHaveBeenLastCalledWith({
      name: "gaming",
      sortDir: "desc",
    });
  });
});
