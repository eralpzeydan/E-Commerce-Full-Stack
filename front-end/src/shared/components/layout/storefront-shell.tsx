import { type ReactNode } from "react";

import { StoreCategoryNav } from "@/shared/components/layout/store-category-nav";
import { StoreHeader } from "@/shared/components/layout/store-header";
import { StoreNav } from "@/shared/components/layout/store-nav";
import { StoreTopBar } from "@/shared/components/layout/store-top-bar";

interface StorefrontShellProps {
  children: ReactNode;
}

export function StorefrontShell({ children }: StorefrontShellProps) {
  return (
    <div className="min-h-screen">
      <StoreTopBar />
      <StoreHeader />
      <StoreNav />
      <StoreCategoryNav />
      <main>{children}</main>
    </div>
  );
}
