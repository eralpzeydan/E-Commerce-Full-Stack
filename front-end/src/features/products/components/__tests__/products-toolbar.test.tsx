import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

import { ProductsToolbar } from "@/features/products/components/products-toolbar";

describe("ProductsToolbar", () => {
  it("renders result summary and sort selector", () => {
    render(
      <ProductsToolbar totalElements={48} sort="newest" onSortChange={vi.fn()} isFetching={false} />,
    );

    expect(screen.getByText("48 products found")).toBeInTheDocument();
    expect(screen.getByRole("combobox", { name: /sort/i })).toBeInTheDocument();
  });

  it("triggers callback when sort changes", async () => {
    const user = userEvent.setup();
    const onSortChange = vi.fn();

    render(
      <ProductsToolbar totalElements={10} sort="newest" onSortChange={onSortChange} isFetching={false} />,
    );

    await user.selectOptions(screen.getByRole("combobox", { name: /sort/i }), "price-asc");

    expect(onSortChange).toHaveBeenCalledWith("price-asc");
  });

  it("shows updating indicator when fetch is in progress", () => {
    render(<ProductsToolbar totalElements={10} sort="newest" onSortChange={vi.fn()} isFetching />);

    expect(screen.getByText("Updating listing...")).toBeInTheDocument();
  });
});
