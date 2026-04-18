import { render, screen } from "@testing-library/react";
import { vi } from "vitest";

import ProductsPage from "@/app/(store)/products/page";

vi.mock("@/features/products/components/products-screen", () => ({
  ProductsScreen: () => <div data-testid="products-screen-feature" />,
}));

describe("/products route", () => {
  it("stays thin and composes the feature screen", () => {
    render(<ProductsPage />);

    expect(screen.getByTestId("products-screen-feature")).toBeInTheDocument();
  });
});
