import Link from "next/link";
import { type ReactNode } from "react";

import { Card } from "@/shared/components/ui/card";
import { PageContainer } from "@/shared/components/layout/page-container";

interface AuthPageShellProps {
  title: string;
  subtitle: string;
  footerText: string;
  footerHref: string;
  footerLabel: string;
  children: ReactNode;
}

export function AuthPageShell({
  title,
  subtitle,
  footerText,
  footerHref,
  footerLabel,
  children,
}: AuthPageShellProps) {
  return (
    <PageContainer>
      <div className="mx-auto max-w-md">
        <Card>
          <h1 className="text-2xl font-bold text-[var(--color-text)]">{title}</h1>
          <p className="mt-2 text-sm text-[var(--color-text-muted)]">{subtitle}</p>
          <div className="mt-6">{children}</div>
          <p className="mt-6 text-sm text-[var(--color-text-muted)]">
            {footerText} <Link className="font-semibold text-teal-700" href={footerHref}>{footerLabel}</Link>
          </p>
        </Card>
      </div>
    </PageContainer>
  );
}
