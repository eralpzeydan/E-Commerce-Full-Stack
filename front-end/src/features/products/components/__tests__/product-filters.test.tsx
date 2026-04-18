import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { vi } from "vitest";

import { ProductFilters } from "@/features/products/components/product-filters";

describe("ProductFilters", () => {
  it("applies keyword and selected category", async () => {
    const user = userEvent.setup();
    const onApply = vi.fn();

    render(
      <ProductFilters
        value={{
          page: 0,
          size: 12,
          sort: "newest",
          name: "",
          categoryId: undefined,
        }}
        categories={[
          { id: 1, name: "Electronics", description: null },
          { id: 2, name: "Books", description: null },
        ]}
        isCategoriesPending={false}
        onApply={onApply}
        onReset={vi.fn()}
      />,
    );

    await user.type(screen.getByPlaceholderText(/search by product name/i), "gaming");
    await user.click(screen.getByLabelText("Books"));
    await user.click(screen.getByRole("button", { name: /apply/i }));

    expect(onApply).toHaveBeenCalledWith({
      name: "gaming",
      categoryId: 2,
      sort: "newest",
    });
  });

  it("resets local fields and triggers reset callback", async () => {
    const user = userEvent.setup();
    const onReset = vi.fn();

    render(
      <ProductFilters
        value={{
          page: 0,
          size: 12,
          sort: "name-asc",
          name: "monitor",
          categoryId: 1,
        }}
        categories={[{ id: 1, name: "Electronics", description: null }]}
        isCategoriesPending={false}
        onApply={vi.fn()}
        onReset={onReset}
      />,
    );

    await user.clear(screen.getByPlaceholderText(/search by product name/i));
    await user.click(screen.getByRole("button", { name: /reset/i }));

    expect(onReset).toHaveBeenCalledTimes(1);
    expect(screen.getByPlaceholderText(/search by product name/i)).toHaveValue("");
    expect(screen.getByLabelText("All categories")).toBeChecked();
  });

  it("shows pending message while categories are loading", () => {
    render(
      <ProductFilters
        value={{
          page: 0,
          size: 12,
          sort: "newest",
          name: "",
          categoryId: undefined,
        }}
        categories={[]}
        isCategoriesPending
        onApply={vi.fn()}
        onReset={vi.fn()}
      />,
    );

    expect(screen.getByText(/loading categories/i)).toBeInTheDocument();
  });
});
