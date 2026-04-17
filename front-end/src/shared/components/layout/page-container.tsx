import { type ReactNode } from "react";

import { StoreContainer } from "@/shared/components/layout/store-container";
import { cn } from "@/shared/utils/cn";

interface PageContainerProps {
  title?: string;
  description?: string;
  className?: string;
  children: ReactNode;
}

export function PageContainer({
  title,
  description,
  className,
  children,
}: PageContainerProps) {
  return (
    <section className={cn("py-8", className)}>
      <StoreContainer>
        {title ? (
          <h1 className="font-[var(--font-space-grotesk)] text-2xl font-bold text-[var(--color-text)]">
            {title}
          </h1>
        ) : null}
        {description ? (
          <p className="mt-2 max-w-3xl text-sm text-[var(--color-text-muted)]">{description}</p>
        ) : null}
        <div className="mt-6">{children}</div>
      </StoreContainer>
    </section>
  );
}
