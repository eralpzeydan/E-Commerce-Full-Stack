import { type HTMLAttributes } from "react";

import { cn } from "@/shared/utils/cn";

type BadgeVariant = "neutral" | "primary" | "success";

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant;
}

const variantClasses: Record<BadgeVariant, string> = {
  neutral: "border-[var(--color-border)] bg-[var(--color-surface-alt)] text-[var(--color-text-muted)]",
  primary: "border-teal-200 bg-teal-50 text-teal-700",
  success: "border-green-200 bg-green-50 text-green-700",
};

export function Badge({ className, variant = "neutral", ...props }: BadgeProps) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full border px-2.5 py-1 text-xs font-semibold",
        variantClasses[variant],
        className,
      )}
      {...props}
    />
  );
}
