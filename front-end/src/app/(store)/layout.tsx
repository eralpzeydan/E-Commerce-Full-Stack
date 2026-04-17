import { type ReactNode } from "react";

import { StorefrontShell } from "@/shared/components/layout/storefront-shell";

interface StoreLayoutProps {
  children: ReactNode;
}

export default function StoreLayout({ children }: StoreLayoutProps) {
  return <StorefrontShell>{children}</StorefrontShell>;
}
