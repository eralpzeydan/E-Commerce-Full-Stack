import { render, screen } from "@testing-library/react";

import { StorefrontShell } from "@/shared/components/layout/storefront-shell";

describe("StorefrontShell", () => {
  it("renders top bar, header, nav, category nav, and child content", () => {
    render(
      <StorefrontShell>
        <div>Store Content</div>
      </StorefrontShell>,
    );

    expect(screen.getByText(/free shipping/i)).toBeInTheDocument();
    expect(screen.getByText(/secure payment/i)).toBeInTheDocument();
    expect(screen.getByText("E-Commerce Studio")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Shop All" })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: "Electronics" })).toBeInTheDocument();
    expect(screen.getByText("Store Content")).toBeInTheDocument();
  });
});
